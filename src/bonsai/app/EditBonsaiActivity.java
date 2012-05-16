package bonsai.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
	private AlertDialog deletealert;
	
	
	
	
	
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
    	createDeleteAlert();
        
        bonsaidb = new BonsaiDbUtil(this);	// Construinos el DDBBAdapter
        bonsaidb.open();

        if(AndroidProjectActivity.iamediting) try {
        	Cursor bonsai = bonsaidb.fetchBonsai(AndroidProjectActivity.bonsaiactual);
            startManagingCursor(bonsai);
            
            name = bonsai.getString(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_NAME));
            editName.setText(name);

            family_id = bonsai.getInt(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_FAMILY_ID));
            editFamily.setText(String.valueOf(family_id));
            
            age = bonsai.getInt(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE));
            editAge.setText(String.valueOf(age));

            height = bonsai.getInt(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
            editHeight.setText(String.valueOf(height));

            photo = bonsai.getString(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_PHOTO));
        	 if(photo.length() < 18) {
        		photoURLtext.setText(photo);
        	 } else {
        	  	photoURLtext.setText("..." + photo.substring((photo.length() - 18), photo.length()));
        	 }
        	 
            
        } catch (Exception e) {
        	Toast.makeText(this, "None bonsai created: " + e.toString(), Toast.LENGTH_LONG).show();
        	
        }
        
    }
    
    public void selectImage(View v) {
    	   // TODO Auto-generated method stub
    	   Intent intent = new Intent(Intent.ACTION_PICK,
    	     android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	   startActivityForResult(intent, 0);
    }
    
    public void deleteImage(View v) {
    	   // TODO Auto-generated method stub
    	   photo = "";
    	   photoURLtext.setText(photo);
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
    	if(photoURLtext.getText().length() < 2) photo = "";
    	} catch(Exception e) {
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    	if((name.length() > 1)) {
    		if(AndroidProjectActivity.iamediting) {
    			bonsaidb.updateBonsai(AndroidProjectActivity.bonsaiactual, name, family_id, age, height, photo, 0, 0, 0, situation);
    			Toast.makeText(this, name + " changed.", Toast.LENGTH_LONG).show();
    			finish();
    		} else {
    			AndroidProjectActivity.bonsaiactual = bonsaidb.createBonsai(name, family_id, age, height, photo, 0, 0, 0, situation);
    			Toast.makeText(this, name + " created.", Toast.LENGTH_LONG).show();
    			finish();
    		}
    	} else {
    		Toast.makeText(this, "Please fill all data", Toast.LENGTH_LONG).show();
    	}
    }
    
    public void goDelete(View v) {
        	deletealert.show();
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
    
    private void createDeleteAlert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure you want to delete?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   if(AndroidProjectActivity.iamediting = true)
    	                bonsaidb.deleteBonsai(AndroidProjectActivity.bonsaiactual);
    	                EditBonsaiActivity.this.finish();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });

    	deletealert = builder.create();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	alert.show();
    	return false;
    }

}