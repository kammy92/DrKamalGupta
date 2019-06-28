package com.bjp.drkamalgupta.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.bjp.drkamalgupta.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;



public class NotificationUtils {
    int notification_id = new Random ().nextInt ((200 - 100) + 1) + 100;
    private Context mContext;
    
    public NotificationUtils (Context mContext) {
        this.mContext = mContext;
    }
    
    public static boolean isAppIsInBackground (Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses ();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals (context.getPackageName ())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks (1);
            ComponentName componentInfo = taskInfo.get (0).topActivity;
            if (componentInfo.getPackageName ().equals (context.getPackageName ())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
    
    // Clears notification tray messages
    public static void clearNotifications (Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll ();
    }
    
    public static long getTimeMilliSec (String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse (timeStamp);
            return date.getTime ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return 0;
    }
    
    public void showNotificationMessage (com.bjp.drkamalgupta.model.Notification notification) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder (mContext);
       
        //Check for empty push message
        if (TextUtils.isEmpty (notification.getMessage ()))
            return;
        mBuilder
                .setSmallIcon (R.drawable.ic_notification_bjp)
                .setSound (Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults (Notification.DEFAULT_ALL)
                .setStyle (new NotificationCompat.BigTextStyle().bigText (notification.getMessage ()))
                .setContentTitle (notification.getTitle ())
                .setColor (mContext.getResources ().getColor (R.color.colorAccent))
                .setContentText (notification.getMessage ());
        
        // now show notification..
        NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService (Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify (notification_id, mBuilder.build ());
    }
    
    public Bitmap getBitmapFromURL (String strURL) {
        try {
            URL url = new URL (strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
            connection.setDoInput (true);
            connection.connect ();
            InputStream input = connection.getInputStream ();
            Bitmap myBitmap = BitmapFactory.decodeStream (input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace ();
            return null;
        }
    }
    
    // Playing notification sound
    public void playNotificationSound () {
        try {
            Uri alarmSound = Uri.parse (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName () + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone (mContext, alarmSound);
            r.play ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
