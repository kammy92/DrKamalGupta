package com.bjp.drkamalgupta.service;

import android.util.Log;

import com.bjp.drkamalgupta.model.Notification;
import com.bjp.drkamalgupta.utils.NotificationUtils;
import com.bjp.drkamalgupta.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("karman", "message received");
        Utils.showLog (Log.DEBUG, TAG, "from " + remoteMessage.getFrom (), true);
        Utils.showLog (Log.DEBUG, TAG, "To " + remoteMessage.getTo (), true);
        Utils.showLog (Log.DEBUG, TAG, "Collapse Key " + remoteMessage.getCollapseKey (), true);
        Utils.showLog (Log.DEBUG, TAG, "Message ID " + remoteMessage.getMessageId (), true);
        Utils.showLog (Log.DEBUG, TAG, "Message Type" + remoteMessage.getMessageType (), true);
        Utils.showLog (Log.DEBUG, TAG, "Sent Time " + remoteMessage.getSentTime (), true);
        Utils.showLog (Log.DEBUG, TAG, "TTL " + remoteMessage.getTtl (), true);
        
        
        notificationUtils = new NotificationUtils (getApplicationContext());
        
        
        if (remoteMessage == null)
            return;
        
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Utils.showLog(Log.ERROR, TAG, "Notification Body: " + remoteMessage.getNotification().getBody(), true);
            Notification notification = new Notification ();
            notification.setMessage (remoteMessage.getNotification ().getBody ());
            notification.setTitle (remoteMessage.getNotification ().getTitle ());
            notificationUtils.showNotificationMessage (notification);
        }
        
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Utils.showLog(Log.ERROR, TAG, "Data Payload: " + remoteMessage.getData().toString(), true);
            try {
                Utils.showLog (Log.ERROR, TAG, "Notification Body: " + remoteMessage.getNotification ().getBody (), true);
                Notification notification = new Notification ();
                notification.setMessage (remoteMessage.getNotification ().getBody ());
                notification.setTitle (remoteMessage.getNotification ().getTitle ());
                notification.setPayload (new JSONObject (remoteMessage.getData ().toString ()));
                notificationUtils.showNotificationMessage (notification);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
}
