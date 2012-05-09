package bonsai.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BonsaiActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bonsai);
        
    }
    
    public void goGallery(View v) {
    	Toast.makeText(this, "Gallery will be soon available", Toast.LENGTH_SHORT).show();
    }
}