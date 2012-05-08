package bonsai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

	private Button start;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        start= (Button)findViewById(R.id.startbutton);
        
    }

    public void goStart(View v) {
    	// Aqu’ sacar’amos el mensaje en el Toast para imprimirlo en un mensajito emergente
    	//Toast.makeText(this, "Donation Webpage", Toast.LENGTH_LONG).show();
    	finish();

    }
}