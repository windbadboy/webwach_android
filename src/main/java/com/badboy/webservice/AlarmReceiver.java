package com.badboy.webservice;
/**
 * Created by badboy on 3/17/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by badboy on 3/17/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        Log.d("data", "onReceive");
        context.startService(i);

    }

}
