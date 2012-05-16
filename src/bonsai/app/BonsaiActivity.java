package bonsai.app;


import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class BonsaiActivity extends Activity {
	

    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	private TextView name;
	private TextView family;
	private ImageView photo;
	private TextView textWater;
	private TextView textTransplant;
	private TextView textPrune;
	private TextView textTemperature;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bonsai);
        

        name = (TextView)findViewById(R.id.textName);
        family = (TextView)findViewById(R.id.textFamily);
        photo = (ImageView)findViewById(R.id.bonsaiImage);
        textWater = (TextView)findViewById(R.id.textWater);
        textTransplant = (TextView)findViewById(R.id.textTransplant);
        textPrune = (TextView)findViewById(R.id.textPrune);
        textTemperature = (TextView)findViewById(R.id.textTemperature);
        
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();
        familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
        familydb.open();
        
        
        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
            startManagingCursor(bonsai);
            name.setText(bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME)));
            family.setText(bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY)));
            String photouri = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_PHOTO));
            if(photouri.length() > 1)
            	photo.setImageURI(Uri.parse(photouri));
            else photo.setImageResource(R.drawable.ic_launcher);
            checkWater();
            checkTransplant();
            checkPode();
            checkWeather();
        	
            
        } catch (Exception e) {
        	Toast.makeText(this, "None Bonsai selected", Toast.LENGTH_SHORT).show();
        	
        }
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
    	 try {
         	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
             startManagingCursor(bonsai);
             name.setText(bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME)));
             family.setText(bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY)));
             String photouri = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_PHOTO));
             if(photouri.length() > 1) {
             	photo.setImageURI(Uri.parse(photouri));
             	photo.setScaleType(ScaleType.FIT_CENTER);
             } else photo.setImageResource(R.drawable.ic_launcher);
             checkWater();
             checkTransplant();
             checkPode();
             checkWeather();
         	
             
         } catch (Exception e) {
         	Toast.makeText(this, "None Bonsai selected", Toast.LENGTH_SHORT).show();
         	
         }
    }
    
    public void goEdit(View v) {
    	try {
    	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        startManagingCursor(bonsai);
        bonsai.getString(
                bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
    	AndroidProjectActivity.iamediting = true;
	    Intent editAct = new Intent().setClass(this, EditBonsaiActivity.class);
	    startActivity(editAct);
    	} catch(Exception e) {
        	Toast.makeText(this, "None Bonsai selected.", Toast.LENGTH_SHORT).show();
    	}
    }

    private void checkWater() {
    	String family;
    	long lastwatered;
    	long waterfrec;
    	int height = 30;
    	int temperature = 20;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        	startManagingCursor(bonsai);
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastwatered = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_WATER));
        	height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	waterfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_WATER_FRECUENCY));
        	
        	// LOGICA DE REGADO
        	
        	if(lastwatered == 0) textWater.setText("Never watered (or no info)");
        	else if(temperature > 35) textWater.setText("You should water 2 times with " + height/4 + " cl today.");
        	else if(temperature < 0) textWater.setText("It's really cold, you should not water today.");
        	else if((hoursTime - lastwatered) > waterfrec) textWater.setText("You should water today once with " + height/2 +" cl.");
        	else textWater.setText("Water OK. Last watered: " + new Date((long)(lastwatered * (1000*60*60))).toLocaleString().toString().substring(0,16));
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
    	
    }
    private void checkTransplant() {
    	String family;
    	long lasttransplant;
    	long transplantfrec;
    	long age = 1;
    	int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        	startManagingCursor(bonsai);
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lasttransplant = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_TRASPLANT));
        	long date = new Date().getTime() / (1000*60*60);
            age = ((date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24));
        	height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	transplantfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_TRANSPLANT_FRECUENCY));
        	
        	// LOGICA DE TRANSPLANTE
        	
        	if(age == 0) textTransplant.setText("You have a baby bonsai! Don't transplant it.");
        	else textTransplant.setText("The age of your bonsai is: " + age);
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
    	
    }
    private void checkPode() {
    	
    }
    private void checkWeather() {
    	
    }
    
    public void makeWater(View v) {
    	String name = "";
    	try {
    		Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
    		startManagingCursor(bonsai);
    		name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
    	} catch(Exception e) {}
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	bonsaidb.waterBonsai(AndroidProjectActivity.bonsaiactual, hoursTime);
    	Toast.makeText(this, name + " has been watered.", Toast.LENGTH_SHORT).show();
    	onResume();
    }
    
}