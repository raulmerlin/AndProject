package bonsai.app;


import java.util.List;


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
import bonsai.app.weather.*;

public class BonsaiActivity extends Activity {
	

    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	private TextView name;
	private TextView family;
	private ImageView photo;
	
	
	//Será un campo de la base de datos que se rellena al crear un bonsai,
	//con el formato códigoPostal,Codígo Pais o códigoPostal,Pais
	//de momento para el ejemplo pruebo con:
	private String location = "28040,ES";
	private TextView textWeather;
	private ImageView weatherIcon;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bonsai);
        

        name = (TextView)findViewById(R.id.textName);
        family = (TextView)findViewById(R.id.textFamily);
        photo = (ImageView)findViewById(R.id.bonsaiImage);
        //nuevas añadidas
        textWeather =(TextView)findViewById(R.id.textweather);
        weatherIcon = (ImageView)findViewById(R.id.imageWeather);
        
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
            
            
            //añado las sentencias que determinarán el tiempo atmosférico
            XmlParserSax saxparser =
                    new XmlParserSax("http://www.google.com/ig/api?weather="+location);
            List<Weather> weather = saxparser.parse();
            Weather w=weather.get(0);
            textWeather.setText(Double.toString(w.getTempMedia())+"º");
            //debo obtener el nombre del icono que tengo en la base de datos
            String s=w.getIcon();
            s=s.replaceAll("/ig/images/weather/", "");
            s=s.replaceAll(".gif", "");
            System.out.println("El nombre del icono que me queda es "+s);
            if(s.equals("chance_of_rain"))
            	weatherIcon.setImageResource(R.drawable.chance_of_rain);
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
            Toast.makeText(this, "No es un buen día para que su bonsai esté en el exterior", Toast.LENGTH_SHORT).show();}
            if(s.equals("mist"))
            	weatherIcon.setImageResource(R.drawable.mist);
            if(s.equals("mostly_sunny"))
            	weatherIcon.setImageResource(R.drawable.mostly_sunny);
            if(s.equals("smoke"))
            	weatherIcon.setImageResource(R.drawable.smoke);
            if(s.equals("snow"))
            	weatherIcon.setImageResource(R.drawable.snow);
            if(s.equals("storm"))
            	weatherIcon.setImageResource(R.drawable.storm);
            if(s.equals("sunny")){
            	System.out.println("EStoy en el if de sunny!!!1");
            	weatherIcon.setImageResource(R.drawable.sunny);
            	Toast.makeText(this, "Su bonsai estará muy contento de poder hoy tomar el sol!!", Toast.LENGTH_LONG).show();}
            if(s.equals("thunderstorm")){
            	weatherIcon.setImageResource(R.drawable.thunderstorm);
            	Toast.makeText(this, "Si su bonsai está en el exterior estará encantado de pasar a casa con Usted", Toast.LENGTH_SHORT).show();
            }
            
            	
            	
        	
            
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
         	
             
         } catch (Exception e) {
         	Toast.makeText(this, "None Bonsai selected", Toast.LENGTH_SHORT).show();
         	
         }
    }
    
    
    public void goGallery(View v) {
    	Toast.makeText(this, "Gallery will be soon available", Toast.LENGTH_SHORT).show();
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
    
}