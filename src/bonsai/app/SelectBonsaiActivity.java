package bonsai.app;




import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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
        bonsaidb.open();						// 


        Cursor bonsaisCursor = bonsaidb.fetchAllBonsais();
        startManagingCursor(bonsaisCursor);
        String[] from = new String[]{BonsaiDbUtil.KEY_NAME};
        int[] to = new int[]{R.id.bonsairowtext};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter bonsais = 
            new SimpleCursorAdapter(this, R.layout.bonsai_row, bonsaisCursor, from, to);
        setListAdapter(bonsais);
        
        
        
        
    }
    
    
    
    
}