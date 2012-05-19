package bonsai.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity {

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        
    }

    public void goStart(View v) {
    	// Aquí sacar�amos el mensaje en el Toast para imprimirlo en un mensajito emergente
    	//Toast.makeText(this, "Donation Webpage", Toast.LENGTH_LONG).show();
    	finish();

    }
}