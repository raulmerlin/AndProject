package bonsai.app;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectbonsai);
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();						// Abrimos la base de datos


        Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();
        startManagingCursor(bonsaisCursor);

        String[] from = new String[]{BonsaiDbUtil.KEY_NAME};
        int[] to = new int[]{R.id.bonsairowtext};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter bonsais = 
            new SimpleCursorAdapter(this, R.layout.bonsai_row, bonsaisCursor, from, to);
        setListAdapter(bonsais);

       /* 
        Cursor bonsaisCursor2 = bonsaidb.fetchAllBonsais();
        startManagingCursor(bonsaisCursor2);
        String[] from2 = new String[]{BonsaiDbUtil.KEY_PHOTO};
        int[] to2 = new int[]{R.id.bonsaiImage};
        SimpleCursorAdapter bonsais2 = new SimpleCursorAdapter(this, R.layout.bonsai_row, bonsaisCursor2, from2, to2);
        setListAdapter(bonsais2);*/

        
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AndroidProjectActivity.bonsaiactual = id;
        
        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
            startManagingCursor(bonsai);
            //String nombre = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	//Toast.makeText(this, "El bonsai actual es: " + nombre, Toast.LENGTH_SHORT).show();

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
            	Toast.makeText(this,"Select your bonsai to check for current information. Press '+' to add a new Bosai", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    
    
}