package bonsai.app.alarm;

import bonsai.app.BonsaiDbUtil;
import bonsai.app.FamilyDbUtil;
import android.app.ListActivity;
import android.database.Cursor;
import java.util.Date;
import java.util.List;
import bonsai.app.weather.Weather;
import bonsai.app.weather.XmlParserSax;
import android.widget.Toast;

/**
 * 
 * @author ruben y raul
 * Clase que contiene un método que calcula para los bonsais de la tabla si
 * tienen alguna tarea pendiente relaccionada con el riego
 * 
 *
 */

public class CheckRiego extends ListActivity {
	
	private Weather w;
	private List<Weather> weather;
	BonsaiDbUtil bonsaidb;
	FamilyDbUtil familydb;
	
	/**
	 * Método encargado de verificar si los bonsais tienen 
	 * tareas pendientes
	 * @return true si hay tareas pendientes y false en caso contrario
	 * 
	 */
	
	public boolean check(){
	 bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
     bonsaidb.open();
     familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
     familydb.open();
     

     Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();
     startManagingCursor(bonsaisCursor);
     String[] tasks = new String[bonsaisCursor.getCount() * 3];
     int cont = 0;
     
     bonsaisCursor.moveToFirst();
     for(int i = 0; i < bonsaisCursor.getCount(); i++) {
        	long id = bonsaisCursor.getLong(bonsaisCursor.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
        	String possibletask = checkWater(id);
        	if(possibletask != null) {
        		tasks[cont] = possibletask;
        		cont++;
        	}
        	if(i < bonsaisCursor.getCount()-1) bonsaisCursor.moveToNext();
        	}
     		int cuenta = 0;
     		for(int i = 0; i < tasks.length; i++) {
     		if(tasks[i] != null) cuenta++;
     		}
     		if (cuenta==0)return false;
     		else return true;
     				
	}
	
	
	
	
	
	private String checkWater(long id) {
    	String name;
    	String family;
    	long lastwatered;
    	long waterfrec;
    	int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
   
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(id);
        	startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastwatered = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_WATER));
        	height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	waterfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_WATER_FRECUENCY));
        	
        	
        
        	
        	// LOGICA DE REGADO
        	
        	if(lastwatered == 0) return ("Set water info on " + name);
        	else if(w.getTempMax() > 35) return("Water " + name + " 2 times with " + height/4 + " cl.");
        	else if(w.getTempMedia() < 0) return null;
        	else if((hoursTime - lastwatered) > waterfrec) return("Water " + name + " today once with " + height/2 +" cl.");
        	else return null;
 
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        }
		
    	
    }
	
 
}
