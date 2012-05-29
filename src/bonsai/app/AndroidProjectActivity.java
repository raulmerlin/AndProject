package bonsai.app;

import bonsai.app.alarm.NotificationService;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TabHost;

public class AndroidProjectActivity extends TabActivity {
	public static long bonsaiactual;
	public static boolean fullversion;
	public static boolean iamediting;
	
    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	
    /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    

	    iamediting = false;
	    fullversion = true;

    	try {

        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();
        
    	Cursor bonsai = bonsaidb.fetchAllBonsais();
    	bonsai.moveToLast();
    		bonsaiactual = bonsai.getInt(
                bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
    	bonsai.close();
    	bonsaidb.close();
    	
	    
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, SelectBonsaiActivity.class);
	    spec = tabHost.newTabSpec("all").setIndicator("All",
	                      res.getDrawable(R.drawable.ic_tab_selectbonsai))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    
	    intent = new Intent().setClass(this, BonsaiActivity.class);
	    spec = tabHost.newTabSpec("bonsai").setIndicator("Bonsai",
	                      res.getDrawable(R.drawable.ic_tab_bonsai))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, TaskActivity.class);
	    spec = tabHost.newTabSpec("calendar").setIndicator("Tasks",
	                      res.getDrawable(R.drawable.ic_tab_calendar))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, MoreActivity.class);
	    spec = tabHost.newTabSpec("more").setIndicator("More",
	                      res.getDrawable(R.drawable.ic_tab_more))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	    
	    if(NotificationService.active == false) {
	    	intent=new Intent(this, NotificationService.class);  
	    	startService(intent);
	    }
	    
	    Intent startmessage = new Intent().setClass(this, StartActivity.class);
	    startActivity(startmessage);
	    
    	} catch (Exception e) {
    		bonsaiactual = 0;
    		System.out.println(e.toString());
    	}

	}
	
	public void changeTab(int i) {
		getTabHost().setCurrentTab(i);
	}

}