package bonsai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MoreActivity extends Activity {
	
	private Button button1;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more);

        button1= (Button)findViewById(R.id.button1);
        
    }
    
    public void goDonate(View v) {
    	// Aqu’ sacar’amos el mensaje en el Toast para imprimirlo en un mensajito emergente
    	//Toast.makeText(this, "Donation Webpage", Toast.LENGTH_LONG).show();
    	Uri uri = Uri.parse("http://www.merlitec.com");
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    	startActivity(intent);

    }
}