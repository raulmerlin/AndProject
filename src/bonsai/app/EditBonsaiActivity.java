package bonsai.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditBonsaiActivity extends Activity {
	
    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;

	private EditText editName;
	private EditText editFamily;
	private EditText editAge;
	private EditText editHeight;
	private TextView photoURLtext;
	private Spinner editSituation;
	
	private String name;
	private int family_id;
	private int age;
	private int height;
	private String photo;
	private long last_pode;
	private long last_water;
	private long last_transplant;
	private int situation;
	
	private AlertDialog alert;
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbonsai);
        

        editName= (EditText)findViewById(R.id.editName);
        editFamily= (EditText)findViewById(R.id.editFamily);
        editAge= (EditText)findViewById(R.id.editAge);
        editHeight= (EditText)findViewById(R.id.editHeight);
        photoURLtext= (TextView)findViewById(R.id.photoURLtext);
        
        editSituation = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.situation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSituation.setAdapter(adapter);
        
    	createCancelAlert();
        
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();

        try {
        	Cursor bonsai = bonsaidb.fetchBonsai(1);
            startManagingCursor(bonsai);
            String nombre = bonsai.getString(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
        	Toast.makeText(this, "El bonsai actual es: " + nombre, Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
        	Toast.makeText(this, "None bonsai created", Toast.LENGTH_LONG).show();
        	
        }
        
    }
    
    public void selectImage(View v) {
    	   // TODO Auto-generated method stub
    	   Intent intent = new Intent(Intent.ACTION_PICK,
    	     android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	   startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     // TODO Auto-generated method stub
     super.onActivityResult(requestCode, resultCode, data);

     if (resultCode == RESULT_OK){
      Uri targetUri = data.getData();
  	  photo = targetUri.toString();
  	  if(photo.length() < 18) {
	  photoURLtext.setText(photo);
  	  } else {
  		photoURLtext.setText("..." + photo.substring((photo.length() - 18), photo.length()));
  	  }
     }
    }
    
    
    public void goSave(View v) {
    	try {
    	name =  editName.getText().toString();
    	family_id =  Integer.parseInt(editFamily.getText().toString());
    	age =  Integer.parseInt(editAge.getText().toString());
    	height =  Integer.parseInt(editHeight.getText().toString());
    	situation = Integer.parseInt(editSituation.getSelectedItem().toString());
    	} catch(Exception e) {
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    	if((name.length() > 1)) {
    		bonsaidb.createBonsai(name, family_id, age, height, photo, 0, 0, 0, situation);
    		Toast.makeText(this, name + " changed.", Toast.LENGTH_LONG).show();
    		finish();
    	} else {
    		Toast.makeText(this, "Please fill all data", Toast.LENGTH_LONG).show();
    	}
    }
    
    public void goDelete(View v) {
    	Toast.makeText(this, "DELETE - Still not Implemented", Toast.LENGTH_LONG).show();
    	finish();
    }
    
    public void goCancel(View v) {
    	alert.show();
    }
    
    private void createCancelAlert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure you want to cancel?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                EditBonsaiActivity.this.finish();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });

    	alert = builder.create();
    }
}