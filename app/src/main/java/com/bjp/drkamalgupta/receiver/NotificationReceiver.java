package com.bjp.drkamalgupta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        Log.e ("karman", "I got this awesome intent and will now do stuff in the background!");
    }
}
