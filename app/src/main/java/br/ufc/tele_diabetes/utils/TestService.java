package br.ufc.tele_diabetes.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import br.ufc.tele_diabetes.R;
import br.ufc.tele_diabetes.activitys.LoginActivity;
import br.ufc.tele_diabetes.activitys.SettingsActivity;
import br.ufc.tele_diabetes.activitys.UserActivity;
import br.ufc.tele_diabetes.activitys.UserInformationsActivity;

/**
 * Created by robertcabral on 8/26/17.
 */

public class TestService extends Service
{

    boolean isRunning;
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TestService.this);
                int frequencia = Integer.parseInt(sp.getString("sync_frequency","-1"));

                Log.i("Service","Frequencia: " + frequencia);

                    try {
                        if(frequencia != -1)
                            Thread.sleep(frequencia * 60000);
                        else
                            return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent i = new Intent(TestService.this, UserActivity.class);

                    NotificationSend.sendNotification(TestService.this,i,"Teste: " + frequencia);

                    startService(new Intent(TestService.this, TestService.class));
                }

        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Intent restartService = new Intent("BootCompletedIntentReceiver");
        sendBroadcast(restartService);
    }
}
