package bonsai.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class EnvioMailActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviomail);
        
        final TextView etEmail = (TextView) findViewById(R.id.etEmail);
        final EditText etSubject = (EditText) findViewById(R.id.etSubject);
        final EditText etBody = (EditText) findViewById(R.id.etBody);
        final CheckBox chkAttachment = (CheckBox) findViewById(R.id.chkAttachment);
        Bundle bundle = getIntent().getExtras();
        etEmail.setText(bundle.getString("MAIL"));
        
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {

/* es necesario un intent que levante la actividad deseada */
                            Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
                            
                            /* vamos a enviar texto plano a menos que el checkbox esté marcado */
                            itSend.setType("plain/text");
                            
                            /* colocamos los datos para el envio */
                            itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ etEmail.getText().toString()});
                            itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, etSubject.getText().toString());
                            itSend.putExtra(android.content.Intent.EXTRA_TEXT, etBody.getText());
                            
                            /* revisamos si el checkbox está marcado enviamos el icono de la aplicaci—n como adjunto */
                            if (chkAttachment.isChecked()) {
                             /* colocamos el adjunto en el stream */
                             itSend.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher));
                            
                             /* indicamos el tipo de dato */
                             itSend.setType("image/png");
                            }
                            /* iniciamos la actividad */
                            startActivity(itSend);
}
                });
    }
}