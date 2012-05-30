package bonsai.app;

import java.util.Date;
import bonsai.app.alarm.NotificationService;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

/*
 * Task Activity. Shows all pending activities for all bonsais on a list
 * 
 * @author: Raul de Pablos Martin
 * 		    Ruben Valero Garcia
 * @version: May 2012
 */
public class TaskActivity extends ListActivity {
	
	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	private String[] tasks;
	private int cont;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
    }
    
    /** Called when activity is shown. */
    @Override
    public void onResume() {
        super.onResume();
    	   bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
           bonsaidb.open();
           familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
           familydb.open();
           
           Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();	// Cogemos todos los bonsais
           startManagingCursor(bonsaisCursor);
           tasks = new String[bonsaisCursor.getCount() * 3];	// Creamos la lista de tareas
           cont = 0;
           
           bonsaisCursor.moveToFirst();		// Cogemos el primer bonsai de todos
           
           for(int i = 0; i < bonsaisCursor.getCount(); i++) {	// Desde el primero hasta el ultimo
        	   long id = bonsaisCursor.getLong(bonsaisCursor.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
        	   Cursor bonsai = bonsaidb.fetchBonsai(id);
        	   startManagingCursor(bonsai);
        	   
           	
           		String possibletask = checkWater(id);	// Si hay que regarlo, se pone en la lista
           		if(possibletask != null) {
           			tasks[cont] = possibletask;
           			cont++;
           		}
           		
           		possibletask = checkPode(id);			// Si hay que podarlo, se pone en la lista
           		if(possibletask != null) {
           			tasks[cont] = possibletask;
           			cont++;
           		}
           		
           		possibletask = checkTransplant(id);		// Si hay que trasplantarlo, se pone en la lista
           		if(possibletask != null) {
           			tasks[cont] = possibletask;
           			cont++;
           		}
           		
           		if(i < bonsaisCursor.getCount()-1) bonsaisCursor.moveToNext();	// Siguiente bonsai
           }
           
           // Contamos el numero de tareas
           int cuenta = 0;
           for(int i = 0; i < tasks.length; i++) {
        	   if(tasks[i] != null) cuenta++;
           }
           
           // Creamos el array con las tareas sin nulos
           int situac = 0;
           String[] finaltasks = new String[cuenta];
           for(int i = 0; i < tasks.length; i++){
        	   if(tasks[i] != null) {
        		   finaltasks[situac] = tasks[i];
        		   situac++;
        	   }
           }
           
           // Y finalmente, mostramos las tareas
           setListAdapter(new ArrayAdapter<String>(this, R.layout.task_row, finaltasks));
           
    }
    
    
    /** This happens when menu key is pressed */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    /** This happens when first menu option is selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MnuOpc1:
            	Toast.makeText(this,"Displays the daily care of your bonsai.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    /*
     * checkWater - Devuelve la accion a realizar respecto al riego de cierto bonsai, a partir
     * de su ultimo riego, su frecuencia de regado y su tama�o.
     * 
     * @param id - fila de la base de datos del bonsai a comprobar
     * @return String con la accion a realizar, o null si no hay que hacer nada.
     * 
     */
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
        	else if((hoursTime - lastwatered) > waterfrec) return("Water " + name + " today once with " + height/2 +" cl.");
        	else return null;
        	
        } catch(Exception e) {
        	System.out.println("checkWater " + e.toString());
        	return null;
        }
    	
    }
    
    /*
     * checkTransplant - Devuelve la accion a realizar respecto al trasplante de cierto bonsai, a partir
     * de su ultimo trasplante y su frecuencia de trasplante.
     * 
     * @param id - fila de la base de datos del bonsai a comprobar
     * @return String con la accion a realizar, o null si no hay que hacer nada.
     */
    private String checkTransplant(long id) {
    	String name;
    	String family;
    	long lasttransplant;
    	long transplantfrec;
    	long age = 1;
    	//int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(id);
        	startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
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
        	
        	if(age < 2) return null;
        	else if(lasttransplant == 0) return("Set transplant info on " + name);
        	else {
        		if((hoursTime - lasttransplant) > transplantfrec) return("Transplant " + name + 
        				"\nLast trasplant was " + (hoursTime - lasttransplant)/24 + " days ago");
        		else return null;
        	}
        	
        } catch(Exception e) {
        	System.out.println("checkTransplant" + e.toString());
        	return null;
        }
    	
    }
    
    /*
     * checkPode - Devuelve la accion a realizar respecto a la poda de cierto bonsai, a partir
     * de su ultima poda, su edad y su frecuencia de podado.
     * 
     * @param id - fila de la base de datos del bonsai a comprobar
     * @return String con la accion a realizar, o null si no hay que hacer nada.
     */
    private String checkPode(long id) {
    	String name;
    	String family;
    	long lastpode;
    	long podefrecuency;
    	long age = 1;
    	//int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(id);
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
        	if(lastpode == 0) return("Set info about " + name + " prunes.");
        	else if(age < 2) {	// Los bonsais con menos de dos a�os se suelen defoliar al 50% cada 2 meses, aprox
        		if(hoursTime - lastpode > 60 * 24) return("Defoliate " + name + " 50%");
        		else return null;
        	}
        	else if(age >= 2 && (hoursTime - lastpode > podefrecuency)) return("Defoliate " + name + " 20%");
        	else return null;
        	
        } catch(Exception e) {
        	System.out.println("checkPode " + e.toString());
        	return null;
        }	
    }    
}