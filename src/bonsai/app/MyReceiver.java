package bonsai.app;

import android.widget.Toast;

public class MyReceiver extends android.content.BroadcastReceiver {
	
	@Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
         Toast.makeText(context, "lógica de negocio irá aquí", Toast.LENGTH_LONG).show();
    }
}