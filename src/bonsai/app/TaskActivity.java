package bonsai.app;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


import bonsai.app.alarm.AlarmChecker;
import bonsai.app.weather.Weather;
import bonsai.app.weather.XmlParserSax;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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

public class TaskActivity extends ListActivity {
	

	private BonsaiDbUtil bonsaidb;
	private FamilyDbUtil familydb;
	private String[] tasks;
	private int cont;
	
	//He de calcular otra vez la temperatura, ya que es posible que solo 
	//utilice esta actividad de la aplicación, posteriormente investigaré una manera
	//de asegurarse que no hay que hacerlo dos veces, si cambio entre esta actividad y 
	//BonsaiActivity
	private ProgressDialog dialog;
	private String location;
	private boolean flagWeather=false;
	private long idtemp;
	private Weather w;
	private List<Weather> weather;
	private Button m_btnAlarma = null;
	private static PendingIntent pendingIntent;
	private static final int ALARM_REQUEST_CODE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        
        m_btnAlarma = ((Button)findViewById(R.id.btnAlarm));
        m_btnAlarma.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        CambiarEstadoAlarma();
                    }});
     //   ListView lv = getListView();
     //   lv.setTextFilterEnabled(true);

        
        
    }
    @Override
    public void onResume() {
        super.onResume();
    	   bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
           bonsaidb.open();
           familydb = new FamilyDbUtil(this);	// Construinos el DDBBAdapter
           familydb.open();
           
           Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();
           startManagingCursor(bonsaisCursor);

           tasks = new String[bonsaisCursor.getCount() * 3];
           cont = 0;
           
           bonsaisCursor.moveToFirst();
           
           for(int i = 0; i < bonsaisCursor.getCount(); i++) {
           	long id = bonsaisCursor.getLong(bonsaisCursor.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
           	
            //si la localización del siguiente bonsai, es la misma que este no
  			//vuelvo a calcular la temperatura
           	Cursor bonsai = bonsaidb.fetchBonsai(id);
  			startManagingCursor(bonsai);
           	if(location!=bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LOCALIZATION)))
           		flagWeather=false;
           	if(flagWeather==false);
           		checkTemp(id);
           	
           	String possibletask = checkWater(id);
           	if(possibletask != null) {
           		tasks[cont] = possibletask;
           		cont++;
           	}
           	possibletask = checkPode(id);
           	if(possibletask != null) {
           		tasks[cont] = possibletask;
           		cont++;
           	}
           	possibletask = checkTransplant(id);
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
           int situac = 0;
           String[] finaltasks = new String[cuenta];
           for(int i = 0; i < tasks.length; i++){
        	   if(tasks[i] != null) {
        		   finaltasks[situac] = tasks[i];
        		   situac++;
        	   }
           }
           
           	setListAdapter(new ArrayAdapter<String>(this, R.layout.task_row, finaltasks));
           
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
        	else if(w.getTempMedia() > 35) return("Water " + name + " 2 times with " + height/4 + " cl.");
        	else if(w.getTempMedia() < 0) return null;
        	else if((hoursTime - lastwatered) > waterfrec) return("Water " + name + " today once with " + height/2 +" cl.");
        	else return null;
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        }
    	
    }
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
        	System.out.println(e.toString());
        	return null;
        }
    	
    }
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
        	System.out.println(e.toString());
        	return null;
        }	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MnuOpc1:
            	Toast.makeText(this,"Displays the daily care of your bonsai. Press Alarm ON / OFF to enable / disable notifications for the taskbar of your device", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    

    
    private void checkTemp(long id) {
      	dialog = ProgressDialog.show(this, "", "Data obtaining…", true);
      	idtemp=id;
      	
      	Thread thread = new Thread() {
      		@Override
      		public void run() {
      			try{
      			Cursor bonsai = bonsaidb.fetchBonsai(idtemp);
      			startManagingCursor(bonsai);	
      			location = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LOCALIZATION));
      	        XmlParserSax saxparser =  new XmlParserSax("http://www.google.com/ig/api?weather="+location);
      	      dialog.dismiss();
      	        weather = saxparser.parse();
      	        w=weather.get(0);
      	        System.out.println("La temperatura de w es " +w.getTempMax());
      				
      			}catch(Exception e){
      				Toast tx;
      				System.out.println(e.toString());
      				tx = Toast.makeText(getApplicationContext(), "Conexion is not aviable", Toast.LENGTH_LONG);
      			    tx.show();
      			  dialog.dismiss();
      			}
      			dialog.dismiss();
      		}

      		
      	};
      	thread.start();	
        flagWeather=true;
    	
   	
    }
    
    
    /**
     * Desactiva o activa la alarma, estableciendola al estado contrario del actual
     */
    private void CambiarEstadoAlarma()
    {
        if (pendingIntent == null)
        {
            //La alarma está desactivada, la activamos
            ActivarAlarma();
        }else
        {
            //La alarma está activada, la desactivamos
            DesactivarAlarma();
        }
    }
    
    private void DesactivarAlarma()
    {
    //     AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
      //   alarmManager.cancel(pendingIntent);
 
         m_btnAlarma.setText("Alarm OFF");
         pendingIntent = null;
 
         Toast.makeText(TaskActivity.this, "Notifications is OFF", Toast.LENGTH_LONG).show();
 
    }
    
    /**
     * Activa la alarma
     */
    private void ActivarAlarma()
    {
    
        int comprobacionIntervaloSegundos = 5;
 /**
           Intent myIntent = new Intent(TaskActivity.this, AlarmChecker.class);
           pendingIntent = PendingIntent.getService(TaskActivity.this, 0, myIntent, 0);
 
           AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
 
           Calendar calendar = Calendar.getInstance();
           calendar.setTimeInMillis(System.currentTimeMillis());
           calendar.add(Calendar.SECOND, 10);
           alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), comprobacionIntervaloSegundos * 1000, pendingIntent);
 */
    	
        	AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    	  Intent 	 	 intent  = new Intent(this, MyReceiver.class);
          PendingIntent pIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
          manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3 * 1000, pIntent);
    	  
          m_btnAlarma.setText("Alarm ON");
 
           Toast.makeText(TaskActivity.this, "Notification is ON", Toast.LENGTH_LONG).show();
 
    }
}