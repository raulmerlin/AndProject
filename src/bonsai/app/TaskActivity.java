package bonsai.app;

import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

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

        setListAdapter(new ArrayAdapter<String>(this, R.layout.task_row, tasks));

     //   ListView lv = getListView();
     //   lv.setTextFilterEnabled(true);

        
        
    }
    

    private String checkWater(long id) {
    	String family;
    	long lastwatered;
    	long waterfrec;
    	int height = 30;
    	int temperature = 20;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(id);
        	startManagingCursor(bonsai);
        	family = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY));
        	lastwatered = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LAST_WATER));
        	height = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
        	
        	Cursor cfamily = familydb.fetchFamilybyName(family);
        	startManagingCursor(cfamily);
        	waterfrec = cfamily.getInt(cfamily.getColumnIndexOrThrow(FamilyDbUtil.KEY_WATER_FRECUENCY));
        	
        	// LOGICA DE REGADO
        	
        	if(lastwatered == 0) return("Never watered (or no info)");
        	else if(temperature > 35) return("You should water 2 times with " + height/4 + " cl today.");
        	else if(temperature < 0) return("It's really cold, you should not water today.");
        	else if((hoursTime - lastwatered) > waterfrec) return("You should water today once with " + height/2 +" cl.");
        	else return("Water OK. Last watered: " + new Date((long)(lastwatered * (1000*60*60))).toLocaleString().toString().substring(0,16));
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        }
    	
    }
    private String checkTransplant(long id) {
    	String family;
    	long lasttransplant;
    	long transplantfrec;
    	long age = 1;
    	//int height = 30;
    	long hoursTime = (new Date().getTime())/(1000*60*60);
    	

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(id);
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
        	
        	if(age < 2) return ("You have a baby bonsai! Don't transplant it.");
        	else if(lasttransplant == 0) return("Never transplanted (or no info).");
        	else {
        		if((hoursTime - lasttransplant) > transplantfrec) return("You should transplant your bonsai." +
        				"\nLast trasplant was " + (hoursTime - lasttransplant)/24 + " days ago.");
        		else return("Transplant OK! Last transplant was " + (hoursTime - lasttransplant)/24 + " days ago.");
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
        	if(lastpode == 0) return("No info about " + name + " prunes.\nMaybe never pruned.");
        	else if(age < 2) {	// Los bonsais con menos de dos a–os se suelen defoliar al 50% cada 2 meses, aprox
        		if(hoursTime - lastpode > 60 * 24) return("Defoliate your bonsai 50%");
        		else return("Your bonsai prune is not necessary");
        	}
        	else if(age >= 2 && (hoursTime - lastpode > podefrecuency)) return("You should defoliate " + name + " 20%");
        	else return(name + " prune is not necessary");
        	
        } catch(Exception e) {
        	System.out.println(e.toString());
        	return null;
        }	
    }
}