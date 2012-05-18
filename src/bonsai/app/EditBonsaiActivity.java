package bonsai.app;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditBonsaiActivity extends Activity {
	
    // Utilidad de manejo de Base de Datos
	private BonsaiDbUtil bonsaidb;

	private EditText editName;
	private Spinner editFamily;
	private EditText editAge;
	private EditText editHeight;
	private TextView photoURLtext;
	private Spinner editSituation;
	private EditText editpostCode;
	private EditText editCountry;

	
	private String name;
	private String family;
	private long age;
	private int height;
	private String photo;
	private String localization;
	private String situation;
	
	private AlertDialog alert;
	private AlertDialog deletealert;
	
	private LocationManager locManager;
	private LocationListener locListener;
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbonsai);

        editName= (EditText)findViewById(R.id.editName);
        
        editFamily = (Spinner)findViewById(R.id.familySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.family_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editFamily.setAdapter(adapter);
        
        editAge= (EditText)findViewById(R.id.editAge);
        
        editHeight= (EditText)findViewById(R.id.editHeight);
        
        photoURLtext= (TextView)findViewById(R.id.photoURLtext);
        
        editSituation = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.situation_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSituation.setAdapter(adapter2);
        

        editpostCode=(EditText)findViewById(R.id.editPostCode);
        editCountry=(EditText)findViewById(R.id.editCountry);

        
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

			long date = new Date().getTime() / (1000*60*60);
            age = (date - bonsai.getLong(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_AGE)))/(365 * 24);
            editAge.setText(String.valueOf(age));

            height = bonsai.getInt(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_HEIGHT));
            editHeight.setText(String.valueOf(height));

            photo = bonsai.getString(
                    bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_PHOTO));
            
            localization = bonsai.getString(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_LOCALIZATION));
            editCountry.setText(localization.substring(6,8));
            editpostCode.setText(localization.substring(0,5));
            
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
    	family =  editFamily.getSelectedItem().toString();
    	age =  Long.parseLong(editAge.getText().toString());
    	height =  Integer.parseInt(editHeight.getText().toString());
    	situation = editSituation.getSelectedItem().toString();
    	localization = editpostCode.getText().toString() + "," + editCountry.getText().toString();
    	if(photoURLtext.getText().length() < 2) photo = "";
    	} catch(Exception e) {
    		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    	}
    	if((name.length() > 1) && (family.length() > 1) && (situation.length()>1) && localization.length()>2) {
    		if(AndroidProjectActivity.iamediting) {
    			long date = (new Date().getTime()) / (1000*60*60);
    			bonsaidb.updateBonsai(AndroidProjectActivity.bonsaiactual, name, family, (date - (age * 365 * 24)), height, photo, localization, situation);
    			Toast.makeText(this, name + " changed.", Toast.LENGTH_LONG).show();
    			finish();
    		} else {
    			long date = new Date().getTime() / (1000*60*60);
    			AndroidProjectActivity.bonsaiactual = bonsaidb.createBonsai(name, family, (date - (age * 365 * 24)), height, photo, 0, 0, 0, localization, situation);
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
    	           	Cursor bonsai = bonsaidb.fetchAllBonsais();
    	        	bonsai.moveToLast();
    	        	try {
    	        		AndroidProjectActivity.bonsaiactual = bonsai.getInt(bonsai.getColumnIndexOrThrow(BonsaiDbUtil.KEY_ROWID));
    	        	} catch (Exception e) {
    	        		AndroidProjectActivity.bonsaiactual=0;
    	        	}
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

    
    //Método para la localización
    private Location comenzarLocalizacion() {

    	locListener = new LocationListener() {
	    	public void onLocationChanged(Location location) {
	    	}
	    	public void onProviderDisabled(String provider){
	    	}
	    	public void onProviderEnabled(String provider){
	    	}
	    	public void onStatusChanged(String provider, int status, Bundle extras){
	    		Log.i("", "Provider Status: " + status);
	    	}
    	};
    	
    	locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    	Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(loc == null) {
        	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        	loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	}
    	
    	return loc;
    }
    
    public void goMakeCountry(View v) {
        Location loc = comenzarLocalizacion();

    	if(loc == null) {
         	Toast.makeText(this, "Incapaz de obtener localizacion, turn on 3g please", Toast.LENGTH_SHORT).show();
         	return;
    	}
        Geocoder myloc = new Geocoder(this,Locale.getDefault());
        Address ad;

        try {
			List<Address> addresses = myloc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	        ad = addresses.get(0);
	        editCountry.setText(ad.getCountryCode());
        } catch (IOException e) {
			// TODO Auto-generated catch block
         	Toast.makeText(this, "Error trying to get country, turn on 3g please " + e.toString(), Toast.LENGTH_SHORT).show();
		}
        
    }
    
    public void goMakePostCode(View v){
        Location loc = comenzarLocalizacion();
        
    	if(loc == null) {
         	Toast.makeText(this, "Cannot get info about localization, turn on 3g please", Toast.LENGTH_SHORT).show();
         	return;
    	}
    	
        Geocoder myloc = new Geocoder(this,Locale.getDefault());
         Address ad;

	        try {
 			List<Address> addresses = myloc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
 	        ad = addresses.get(0);
 	        editpostCode.setText(ad.getPostalCode());
         } catch (Exception e) {
 			// TODO Auto-generated catch block
         	Toast.makeText(this, "Error trying to get location, turn on 3g please " + e.toString(), Toast.LENGTH_SHORT).show();
 		}
     	
     }

}