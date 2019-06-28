package com.bjp.drkamalgupta.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.bjp.drkamalgupta.R;
import com.bjp.drkamalgupta.app.AppController;

import java.util.List;

/**
 * Created by Admin on 23-12-2015.
 */
public class Utils {
    public static void showToast (Activity activity, String message, boolean duration_long) {
        if (duration_long) {
            Toast.makeText (activity, message, Toast.LENGTH_LONG).show ();
        } else {
            Toast.makeText (activity, message, Toast.LENGTH_SHORT).show ();
        }
    }
    
    public static void showProgressDialog (Activity activity, final ProgressDialog progressDialog, String message, boolean cancelable) {
        // Initialize the progressDialog before calling this function
        TextView tvMessage;
        progressDialog.show ();
        progressDialog.setCancelable (false);
        progressDialog.setCanceledOnTouchOutside (false);
        progressDialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
        progressDialog.setContentView (R.layout.progress_dialog);
    
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run () {
                progressDialog.setCancelable (true);
                progressDialog.setCanceledOnTouchOutside (true);
            }
        }, 5000);
    
    
        tvMessage = (TextView) progressDialog.findViewById (R.id.tvProgressDialogMessage);
        if (message != null) {
            tvMessage.setText (message);
            tvMessage.setVisibility (View.VISIBLE);
        } else {
            tvMessage.setVisibility (View.GONE);
        }
        setTypefaceToAllViews (activity, tvMessage);
        progressDialog.setCancelable (cancelable);
    }
    
    public static void setTypefaceToAllViews (Activity activity, View view) {
        Typeface tf = SetTypeFace.getTypeface (activity);
        SetTypeFace.applyTypeface (activity, SetTypeFace.getParentView (view), tf);
    }
    
    public static void showLog (int log_type, String tag, String message, boolean show_flag) {
        if (Constants.show_log) {
            if (show_flag) {
                switch (log_type) {
                    case Log.DEBUG:
                        Log.d (tag, message);
                        break;
                    case Log.ERROR:
                        Log.e (tag, message);
                        break;
                    case Log.INFO:
                        Log.i (tag, message);
                        break;
                    case Log.VERBOSE:
                        Log.v (tag, message);
                        break;
                    case Log.WARN:
                        Log.w (tag, message);
                        break;
                    case Log.ASSERT:
                        Log.wtf (tag, message);
                        break;
                }
            }
        }
    }
    
    public static boolean isAppIsInBackground (Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
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
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return isInBackground;
    }
    
    public static void sendRequest (StringRequest strRequest, int timeout_seconds) {
        strRequest.setShouldCache (false);
        AppController.getInstance ().getRequestQueue ().getCache ().clear ();
        int timeout = timeout_seconds * 1000;
        AppController.getInstance ().addToRequestQueue (strRequest);
        strRequest.setRetryPolicy (new DefaultRetryPolicy (timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}