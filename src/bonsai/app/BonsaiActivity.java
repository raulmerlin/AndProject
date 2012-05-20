package bonsai.app;


import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import bonsai.app.weather.Weather;
import bonsai.app.weather.XmlParserSax;

public class BonsaiActivity extends Activity {
	

    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	private TextView name;
	private TextView family;
	private ImageView photo;
	private TextView age;
	private TextView textWater;
	private TextView textTransplant;
	private TextView textPrune;
	private TextView textTemperature;
	private TextView textWeather;
	private ImageView weatherIcon;
	private Weather w;
	private boolean weatherAvail;
	
	private String location;
	private double temperature;
	public boolean isWeatherAvail() {
		return weatherAvail;
	}



	public double getTemperature() {
		return temperature;
	}

	private String imageWeather;

	private final Handler handler = new Handler();
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bonsai);
        


        name = (TextView)findViewById(R.id.textName);
        family = (TextView)findViewById(R.id.textFamily);
        photo = (ImageView)findViewById(R.id.bonsaiImage);
        age = (TextView)findViewById(R.id.textYears);
        textWater = (TextView)findViewById(R.id.textWater);
        textTransplant = (TextView)findViewById(R.id.textTransplant);
        textPrune = (TextView)findViewById(R.id.textPrune);
        textTemperature = (TextView)findViewById(R.id.textTemperature);
        textWeather =(TextView)findViewById(R.id.textweather);
        weatherIcon = (ImageView)findViewById(R.id.imageWeather);
        textWeather =(TextView)findViewById(R.id.textweather);
        weatherIcon = (ImageView)findViewById(R.id.imageWeather);
        
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();
        familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
        familydb.open();

        

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
             long date = new Date().getTime() / (1000*60*60);
             age.setText("" + ((date - bonsai.getLong(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24)));
             checkWater();
             checkTransplant();
             checkPode();
             checkWeather();
             weatherAction();
            
             
             
             
         	 
         } catch (Exception e) {
         	Toast.makeText(this, "None Bonsai selected", Toast.LENGTH_SHORT).show();
         	
         }
    }

    
    private void weatherAction() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            	if(weatherAvail) {
           	 		setTempInfo();
           	 		setWeatherInfo();
           	 		setWeatherComment();
            	} else {
            		weatherAction();
            	}
            }
        }, 1000);
        
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
    
    
    public void toastImage(View v) {
    	Toast ImageToast = new Toast(getBaseContext());
        LinearLayout toastLayout = new LinearLayout(getBaseContext());
        toastLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView image = new ImageView(getBaseContext());
    	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        startManagingCursor(bonsai);
        String photouri = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_PHOTO));
        if(photouri.length() > 1)
        	image.setImageURI(Uri.parse(photouri));
        else image.setImageResource(R.drawable.ic_launcher);
        //text.setText("");
        toastLayout.addView(image);
        //toastLayout.addView(text);
        ImageToast.setView(toastLayout);
        ImageToast.setDuration(Toast.LENGTH_SHORT);
        ImageToast.show();
    }

    private void checkWater() {
    	String family;
    	long lastwatered;
    	long waterfrec;
    	int height = 30;
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
    	//int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        	startManagingCursor(bonsai);
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lasttransplant = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_TRASPLANT));
        	long date = new Date().getTime() / (1000*60*60);
            age = ((date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24));
        	//height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	transplantfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_TRANSPLANT_FRECUENCY));
        	
        	// LOGICA DE TRANSPLANTE
        	
        	if(age < 2) textTransplant.setText("You have a baby bonsai! Don't transplant it.");
        	else if(lasttransplant == 0) textTransplant.setText("Never transplanted (or no info).");
        	else {
        		if((hoursTime - lasttransplant) > transplantfrec) textTransplant.setText("You should transplant your bonsai." +
        				"\nLast trasplant was " + (hoursTime - lasttransplant)/24 + " days ago.");
        		else textTransplant.setText("Transplant OK! Last transplant was " + (hoursTime - lasttransplant)/24 + " days ago.");
        	}
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
    	
    }
    private void checkPode() {
    	String name;
    	String family;
    	long lastpode;
    	long podefrecuency;
    	long age = 1;
    	//int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
        	startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastpode = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_PODE));
        	long date = new Date().getTime() / (1000*60*60);
            age = ((date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24));
        	//height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	podefrecuency = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_PODE_FRECUENCY));
        	
        	// LOGICA DE TRANSPLANTE
        	if(lastpode == 0) textPrune.setText("No info about " + name + " prunes.\nMaybe never pruned.");
        	else if(age < 2) {	// Los bonsais con menos de dos a�os se suelen defoliar al 50% cada 2 meses, aprox
        		if(hoursTime - lastpode > 60 * 24) textPrune.setText("Defoliate your bonsai 50%");
        		else textPrune.setText("Your bonsai prune is not necessary");
        	}
        	else if(age >= 2 && (hoursTime - lastpode > podefrecuency)) textPrune.setText("You should defoliate " + name + " 20%");
        	else textPrune.setText(name + " prune is not necessary");
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        }
    	
    	
    }
    private void checkWeather() {
       	weatherAvail = false;
       	Thread thread = new Thread() {
       		
       		public void run() {
       			try{
       			Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
       			startManagingCursor(bonsai);	
       			location = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LOCALIZATION));
       	        XmlParserSax saxparser =  new XmlParserSax("http://www.google.com/ig/api?weather="+location);
       	        List<Weather> weather = saxparser.parse();
       	        w=weather.get(0);
       	     
       	         temperature=w.getTempMedia();
       	         String s=w.getIcon();
       	         imageWeather = s;
       	         weatherAvail = true;
       	     	} catch(Exception e){
       	     		System.out.println("Salta una excepcion: " + e.toString());

       	     	}
       			
       		}
       		
       	};
       	thread.start();	
    }
    
    public void setTempInfo() {
    	try{
	   		textWeather.setText(Integer.toString(w.getTempMedia())+"ºC");
    	} catch (Exception e) {
    	
    	}

    }
    
    
    public void setWeatherInfo() {
    	try{
		String s = imageWeather;
	         s=s.replaceAll("/ig/images/weather/", "");
	         s=s.replaceAll(".gif", "");
	         System.out.println("El nombre del icono que me queda es "+s);
	         if(s.equals("chance_of_rain")){
	         	weatherIcon.setImageResource(R.drawable.chance_of_rain);
	         }
	         if(s.equals("chance_of_snow"))
	         	weatherIcon.setImageResource(R.drawable.chance_of_snow);
	         if(s.equals("chance_of_storm"))
	         	weatherIcon.setImageResource(R.drawable.chance_of_storm);
	         if(s.equals("cloudly"))
	         	weatherIcon.setImageResource(R.drawable.cloudy);
	         if(s.equals("dust"))
	         	weatherIcon.setImageResource(R.drawable.dust);
	         if(s.equals("fog"))
	         	weatherIcon.setImageResource(R.drawable.fog);
	         if(s.equals("haze"))
	         	weatherIcon.setImageResource(R.drawable.haze);
	         if(s.equals("icy")){
	         	weatherIcon.setImageResource(R.drawable.icy);
	         		}
	         if(s.equals("mist"))
	         	weatherIcon.setImageResource(R.drawable.mist);
	         if(s.equals("mostly_sunny")){
	         	weatherIcon.setImageResource(R.drawable.mostly_sunny);
	         }
	         if(s.equals("smoke"))
	         	weatherIcon.setImageResource(R.drawable.smoke);
	         if(s.equals("snow")){
	         	weatherIcon.setImageResource(R.drawable.snow);
	         		}
	         if(s.equals("storm")){
	         	weatherIcon.setImageResource(R.drawable.storm);
	         		}
	         if(s.equals("sunny")){
	         	weatherIcon.setImageResource(R.drawable.sunny);
	         	}
	         if(s.equals("thunderstorm")){
	         	weatherIcon.setImageResource(R.drawable.thunderstorm);
	         }
       	} catch (Exception e) {
        	
    	}

    }
    


    public void setWeatherComment() {
    	try{
   	    	String situation; 	
   			Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
   			startManagingCursor(bonsai);	
   	     	bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
   	   		startManagingCursor(bonsai);	
   	   		situation= bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_SITUATION));
   	   		System.out.println("la situacion es "+situation);
   	   		String s = imageWeather;
	        s=s.replaceAll("/ig/images/weather/", "");
	        s=s.replaceAll(".gif", "");
	        System.out.println("El nombre del icono que me queda es " + s);

	         if(s.equals("icy")){
	         	if(situation.equals("Exterior"))
	         	textTemperature.setText("Your Bonsai is frozen, please put it indoor");
	         		}	         
	         if(s.equals("snow")||s.equals("chance_of_snow")){
	         	if(situation.equals("Exterior"))
	             	textTemperature.setText("Your Bonsai looks like snowman");
	         		}
	         if(s.equals("thunderstorm")){
	         	if(situation.equals("Exterior"))
	             	textTemperature.setText("Your Bonsai is scare of thunderstorm");
	         		}
	         if(s.equals("sunny")||s.equals("mostly_sunny")){
	         	if(situation.equals("Interior"))
	             	textTemperature.setText("Your Bonsai would like to " +
	             			"have some sunbathing today");
	         	}

       	} catch (Exception e) {
        	
    	}

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
    	Toast.makeText(this, name + " has been watered.", Toast.LENGTH_LONG).show();
    	onResume();
    }
    
    public void makePode(View v) {
    	String name = "";
    	try {
    		Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
    		startManagingCursor(bonsai);
    		name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
    	} catch(Exception e) {}
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	bonsaidb.podeBonsai(AndroidProjectActivity.bonsaiactual, hoursTime);
    	Toast.makeText(this, name + " has been pruned.", Toast.LENGTH_LONG).show();
    	onResume();
    }
    
    public void makeTransplant(View v) {
    	String name = "";
    	try {
    		Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
    		startManagingCursor(bonsai);
    		name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
    	} catch(Exception e) {}
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	bonsaidb.transplantBonsai(AndroidProjectActivity.bonsaiactual, hoursTime);
    	Toast.makeText(this, name + " has been transplanted.", Toast.LENGTH_LONG).show();
    	onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
}