package br.ufc.tele_diabetes.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by robertcabral on 8/26/17.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            Intent pushIntent = new Intent(context, TestService.class);
            context.startService(pushIntent);
    }
}
