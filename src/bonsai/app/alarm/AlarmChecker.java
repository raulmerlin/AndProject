package bonsai.app.alarm;


import bonsai.app.*;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.app.Notification;

import android.app.PendingIntent;

import android.util.Log;



public class AlarmChecker extends Service  implements Runnable {
	 
public static final int APP_ID_NOTIFICATION = 0;
private NotificationManager mManager;

 
private final int MSG_KEY_NOTIFOFF = 1;
private final int MSG_KEY_NOTIFOK = 2;
 
/**
 * Método del hilo asíncrono, que revisa las tareas y comprueba si hay alguna para mostrar
 */
public void run() {
	
	CheckRiego c = null;
	boolean b=c.check();
	b=true;
 
    System.out.println("se comprobaron las tareas y el resultado fué " + b);
    if (b==false)
        //Respondemos que no hay tareas
        handler.sendEmptyMessage(MSG_KEY_NOTIFOFF);
    else{
        handler.sendEmptyMessage(MSG_KEY_NOTIFOK);
    }
}
 


/**
 * Procesa eventos desde el hilo run
 */
private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
            switch (msg.what){
            case MSG_KEY_NOTIFOK: //Hemos obtenido que hay tareas pendientes
                 Notificar();
                break;
            }
    }
};
 
/**
 * prepara y lanza la notificacion
 */
private void Notificar() {
 
    //Prepara la actividad que se abrira cuando el usuario pulse la notificacion
        Intent intentNot = new Intent(this, TaskActivity.class);
 
    //Prepara la notificacion
        Notification notification = new Notification(R.drawable.bonsai, "Hay cambios", System.currentTimeMillis());
        notification.setLatestEventInfo(this, getString(R.string.app_name), "Notificación de Bonsai Cares",
                PendingIntent.getActivity(this.getBaseContext(), 0, intentNot, PendingIntent.FLAG_CANCEL_CURRENT));
 
    //Le añade sonido
        notification.defaults |= Notification.DEFAULT_SOUND;
    //Le añade vibración
        notification.defaults |= Notification.DEFAULT_VIBRATE;
 
    //Le añade luz mediante LED
        notification.defaults |= Notification.DEFAULT_LIGHTS;
 
    //La notificación se detendrá cuando el usuario pulse en ella
        notification.flags = Notification.FLAG_AUTO_CANCEL;
 
    //Intenta establecer el color y el parpadeo de la bombilla lED
        try
        {
            notification.ledARGB = 0xff00ff00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }catch(Exception ex)
        {
            //Nothing
        }
 
        //Lanza la notificación
        mManager.notify(APP_ID_NOTIFICATION, notification);
 
     }

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}
 
      //interface events
 
}