package com.bjp.drkamalgupta.service;

import com.bjp.drkamalgupta.utils.UserDetailsPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh () {
        super.onTokenRefresh ();
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
        userDetailsPref.putStringPref (getApplicationContext (), UserDetailsPref.FIREBASE_ID, FirebaseInstanceId.getInstance ().getToken ());
    }
}

