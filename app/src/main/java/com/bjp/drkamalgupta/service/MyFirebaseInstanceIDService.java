package com.bjp.drkamalgupta.service;

import com.bjp.drkamalgupta.utils.AppDetailsPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh () {
        super.onTokenRefresh ();
        AppDetailsPref appDetailsPref = AppDetailsPref.getInstance ();
        appDetailsPref.putStringPref (getApplicationContext (), AppDetailsPref.FIREBASE_ID, FirebaseInstanceId.getInstance ().getToken ());
    }
}

