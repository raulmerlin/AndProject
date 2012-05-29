package bonsai.app.alarm;

import java.util.Date;
import bonsai.app.AndroidProjectActivity;
import bonsai.app.BonsaiDbUtil;
import bonsai.app.FamilyDbUtil;
import bonsai.app.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;

public class NotificationService extends IntentService {
	public static boolean notificado;
	public static boolean enabled;
	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	


	public NotificationService() {
	    super("NotificationService");
	    // TODO Auto-generated constructor stub
	}
	   
	@Override
	protected void onHandleIntent(Intent intent) {
	    // TODO Auto-generated method stub
		while(true) {
	        try {
	    		enabled = true;
    			Thread.sleep(30000);
                if(!enabled) break;
                	boolean tarea = checkForTasks();
                	if(notificado) {
                		if(tarea) {
                		} else {
                			notificado = false;
                		}	
                	} else {
                		if(tarea) {
                			notifica();
                			notificado = true;
                		} else {
                		}
                	}
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            System.out.println(e.toString());
	        }
	    }
	}
	
	private boolean checkForTasks() {
		Cursor bonsaisCursor = null;
		try {
		 bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
         bonsaidb.open();
         familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
         familydb.open();
         
         
         bonsaisCursor = bonsaidb.fetchAllBonsais();	// Cogemos todos los bonsais
         //startManagingCursor(bonsaisCursor);
         
         bonsaisCursor.moveToFirst();		// Cogemos el primer bonsai de todos
         
         for(int i = 0; i < bonsaisCursor.getCount(); i++) {	// Desde el primero hasta el ultimo
      	   long id = bonsaisCursor.getLong(bonsaisCursor.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
      	   //startManagingCursor(bonsai);

         	
         		String possibletask = checkWater(id);	// Si hay que regarlo, se pone en la lista
         		if(possibletask != null) {
         			return true;
         		}
         		
         		possibletask = checkPode(id);			// Si hay que podarlo, se pone en la lista
         		if(possibletask != null) {
         			return true;
         		}
         		
         		possibletask = checkTransplant(id);		// Si hay que trasplantarlo, se pone en la lista
         		if(possibletask != null) {
         			return true;
         		}
         		
         		if(i < bonsaisCursor.getCount()-1) bonsaisCursor.moveToNext();	// Siguiente bonsai
         }
        bonsaisCursor.close();
        bonsaidb.close();
        familydb.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			bonsaisCursor.close();
			bonsaidb.close();
			familydb.close();
		}
	    return false;
	}
	
	/**
	 * prepara y lanza la notificacion
	 */
	private void notifica() {

        try {
        	int icon = R.drawable.bonsai;
        	CharSequence tickerText = "BonsaiCares";
        	long when = System.currentTimeMillis();

        	Notification notification = new Notification(icon, tickerText, when);
        	
        	NotificationManager notificationManager =
        		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        	Intent notificationIntent = new Intent(this, AndroidProjectActivity.class);
        	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        		notification.setLatestEventInfo(this, "Bonsai Cares","One bonsai needs your cares", contentIntent);
        		
        		 notification.defaults |= Notification.DEFAULT_SOUND;
    	        notification.defaults |= Notification.DEFAULT_VIBRATE;
    	        notification.defaults |= Notification.DEFAULT_LIGHTS;
    	        notification.flags = Notification.FLAG_AUTO_CANCEL;
    	        notification.ledARGB = 0xff00ff00;
    	        notification.ledOnMS = 300;
    	        notification.ledOffMS = 1000;
    	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        		
        		notificationManager.notify(1, notification);
	        
        } catch(Exception e) {
	            System.out.println(e.toString());
	        }
	 
	     }
	
    /*
     * checkWater - Devuelve la accion a realizar respecto al riego de cierto bonsai, a partir
     * de su ultimo riego, su frecuencia de regado y su tama–o.
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
    	Cursor bonsai = null;
    	Cursor cfamily = null;
    	

        try {
        	bonsai = bonsaidb.fetchBonsai(id);
        	//startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastwatered = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_WATER));
        	height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	cfamily = familydb.fetchFamilybyName(family);
        	//startManagingCursor(cfamily);
        	waterfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_WATER_FRECUENCY));
        	
        	// LOGICA DE REGADO
        	if(lastwatered == 0) return ("Set water info on " + name);
        	else if((hoursTime - lastwatered) > waterfrec) return("Water " + name + " today once with " + height/2 +" cl.");
        	else return null;
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        } finally {
        	bonsai.close();
        	cfamily.close();
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
    	Cursor bonsai = null;
    	Cursor cfamily = null;
    	

        try {
        	bonsai = bonsaidb.fetchBonsai(id);
        	//startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lasttransplant = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_TRASPLANT));
        	long date = new Date().getTime() / (1000*60*60);
            age = ((date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24));
        	//height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	cfamily = familydb.fetchFamilybyName(family);
        	//startManagingCursor(cfamily);
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
        	System.out.println(e.toString());
        	return null;
        } finally {
        	bonsai.close();
        	cfamily.close();
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
    	Cursor bonsai = null;
    	Cursor cfamily = null;

        try {
        	bonsai = bonsaidb.fetchBonsai(id);
        	//startManagingCursor(bonsai);
        	name = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastpode = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_PODE));
        	long date = new Date().getTime() / (1000*60*60);
            age = ((date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365*24));
        	//height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	cfamily = familydb.fetchFamilybyName(family);
        	//startManagingCursor(cfamily);
        	podefrecuency = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_PODE_FRECUENCY));
        
        	
        	// LOGICA DE PODA
        	if(lastpode == 0) return("Set info about " + name + " prunes.");
        	else if(age < 2) {	// Los bonsais con menos de dos aï¿½os se suelen defoliar al 50% cada 2 meses, aprox
        		if(hoursTime - lastpode > 60 * 24) return("Defoliate " + name + " 50%");
        		else return null;
        	}
        	else if(age >= 2 && (hoursTime - lastpode > podefrecuency)) return("Defoliate " + name + " 20%");
        	else return null;
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        } finally {
        	bonsai.close();
        	cfamily.close();
        }
    }
}