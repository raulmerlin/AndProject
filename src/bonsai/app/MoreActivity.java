package bonsai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MoreActivity extends Activity {
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more);
        
        final Button btn = (Button)findViewById(R.id.contactbutton);
        btn.setOnClickListener(new OnClickListener() {
          public void onClick(View arg0) {
              Intent intent = new Intent(MoreActivity.this, EnvioMailActivity.class);
              Bundle bundle = new Bundle();
              bundle.putString("MAIL", "raulmerlin@gmail.com ; rubevalero@gmail.com");
              intent.putExtras(bundle);
              startActivity(intent);
          }
    });
        
    }
    
    public void goDonate(View v) {
    	// Aqu’ sacar’amos el mensaje en el Toast para imprimirlo en un mensajito emergente
    	//Toast.makeText(this, "Donation Webpage", Toast.LENGTH_LONG).show();
    	Uri uri = Uri.parse("http://www.merlitec.com");
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    	startActivity(intent);

    }
    
    public void doExit(View v) {
    	finish();
    }
    
    public void buyFullVersion(View v) {
    	Toast.makeText(this, "Here will be the link to buy full version", Toast.LENGTH_SHORT).show();
    }
}