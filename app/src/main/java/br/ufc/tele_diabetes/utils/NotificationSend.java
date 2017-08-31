package br.ufc.tele_diabetes.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

import br.ufc.tele_diabetes.R;

/**
 * Created by robertcabral on 8/27/17.
 */

public class NotificationSend {

    public static void sendNotification(Context context, Intent intent, String text){
        Random rand = new Random();
        long[] pattern = {0, 500, 300, 500};
        int number = rand.nextInt(10000);
        Bitmap bp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.iconnot);
        PendingIntent pi = PendingIntent.getActivity(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean tes = sp.getBoolean("notifications_new_message",false);
        boolean vib = sp.getBoolean("notifications_new_message_vibrate",false);
        String som = sp.getString("notifications_new_message_ringtone","xxx");
        Log.i("Service","ABC: " + tes);

        if(tes) {
            Log.i("Service", "Som: " + som);

            Uri uri = Uri.parse(som);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setLargeIcon(bp)
                    .setSmallIcon(R.mipmap.iconnot)
                    .setSound(uri)
                    .setContentText(text)
                    .setContentTitle("Tele-Diabetes")
                    .setContentIntent(pi);

            if (vib) {
                builder.setVibrate(pattern);
            }

            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(number, notification);
        }

    }

}
