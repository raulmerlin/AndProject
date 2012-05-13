package bonsai.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class EditBonsaiActivity extends Activity {
	

    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbonsai);
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();
        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(0);
            startManagingCursor(bonsai);
            String nombre = bonsai.getString(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	Toast.makeText(this, "El bonsai actual es: " + nombre, Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
        	Toast.makeText(this, "None bonsai created", Toast.LENGTH_LONG).show();
        	
        }
        
    }
    
    
    
    public void goCancel(View v) {
    	finish();
    }
}