package bonsai.app;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SelectBonsaiActivity extends ListActivity {
	
    
    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	private Cursor bonsaisCursor = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.selectbonsai);
        try {
        	bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        	bonsaidb.open();						// Abrimos la base de datos


        	bonsaisCursor = bonsaidb.fetchAllBonsais();
        	startManagingCursor(bonsaisCursor);

        	String[] from = new String[]{BonsaiDbUtil.KEY_NAME};
        	int[] to = new int[]{R.id.bonsairowtext};
        	// Now create a simple cursor adapter and set it to display
        	SimpleCursorAdapter bonsais = 
        			new SimpleCursorAdapter(this, R.layout.bonsai_row, bonsaisCursor, from, to);
        	setListAdapter(bonsais);
        } catch (Exception e) {
        	System.out.println(e.toString());
        }
        	//bonsaidb.close();
    }

    @Override
    public void onPause() {
    	super.onPause();
    	//bonsaisCursor.close();
    	//bonsaidb.close();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AndroidProjectActivity.bonsaiactual = id;
        
        try {
        	bonsaidb.close();
        	AndroidProjectActivity tabs = (AndroidProjectActivity) this.getParent();
        	tabs.changeTab(1);

        } catch (Exception e) {
        	Toast.makeText(this, "Error changing bonsai.", Toast.LENGTH_LONG).show();
        	
        }
        
        
    }
    
    public void goCreate(View v) {
    	if(AndroidProjectActivity.fullversion == true) {
    		AndroidProjectActivity.iamediting = false;
    		Intent editAct = new Intent().setClass(this, EditBonsaiActivity.class);
    		startActivity(editAct);
    	} else {
    		Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();
    		startManagingCursor(bonsaisCursor);
    		if(bonsaisCursor.getCount() >= 1) {
            	Toast.makeText(this, "Please, buy full version to create more than one bonsai", Toast.LENGTH_LONG).show();
            	AndroidProjectActivity tabs = (AndroidProjectActivity) this.getParent();
            	tabs.changeTab(3);
    		} else {
        		AndroidProjectActivity.iamediting = false;
        		Intent editAct = new Intent().setClass(this, EditBonsaiActivity.class);
        		startActivity(editAct);
    		}
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
            	Toast.makeText(this,"Select your bonsai to check for current information. Press '+' to add a new Bonsai", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    
    
}