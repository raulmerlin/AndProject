package bonsai.app;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends android.content.BroadcastReceiver {
	
	public static final int APP_ID_NOTIFICATION = 0;
	private static final int NOTIF_ALERTA_ID = 1;
	
	
	@Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
         Toast.makeText(context, "Y Por fin!!! Salta la notificación!!", Toast.LENGTH_LONG).show();
     Notificar(context);    
	}
	
	public void Notificar(android.content.Context context){
       //Obtenemos una referencia al servicio de notificaciones
         NotificationManager notManager = 
        		 (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE) ; 
         
       //Configuramos la notificación
         int icono = 0x7f020000;
         CharSequence textoEstado = "Mensaje de Bonsai Cares";
         long hora = System.currentTimeMillis();
          
         Notification notif =
             new Notification(icono, textoEstado, hora);
         
       //Configuramos el Intent
         Context contexto =context;
         CharSequence titulo = "Mensaje de Bonsai Cares";
         CharSequence descripcion = "Notificación de Riego";
          
         Intent notIntent = new Intent(contexto,
             TaskActivity.class);
         
         PendingIntent contIntent = PendingIntent.getActivity(
        		    contexto, 0, notIntent, 0);
        		 
        		notif.setLatestEventInfo(
        		    contexto, titulo, descripcion, contIntent);
         
       //AutoCancel: cuando se pulsa la notificaión ésta desaparece
         notif.flags |= Notification.FLAG_AUTO_CANCEL;
         //mando la notificación
         notManager.notify(NOTIF_ALERTA_ID, notif);

    }
	
}