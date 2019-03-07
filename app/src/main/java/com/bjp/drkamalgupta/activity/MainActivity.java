package com.bjp.drkamalgupta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bjp.drkamalgupta.R;
import com.bjp.drkamalgupta.utils.AppConfigTags;
import com.bjp.drkamalgupta.utils.AppConfigURL;
import com.bjp.drkamalgupta.utils.Constants;
import com.bjp.drkamalgupta.utils.NetworkConnection;
import com.bjp.drkamalgupta.utils.SetTypeFace;
import com.bjp.drkamalgupta.utils.UserDetailsPref;
import com.bjp.drkamalgupta.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    RelativeLayout rlNoInternetAvailable;
    ProgressDialog progressDialog;
    TextView tvRefresh;
    
    UserDetailsPref userDetailsPref;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListener ();
        initApplication ();
    }
    
    private void initView () {
        webView = (WebView) findViewById (R.id.webView);
        rlNoInternetAvailable = (RelativeLayout) findViewById (R.id.rlNoInternetAvailable);
        tvRefresh = (TextView) findViewById (R.id.tvRefresh);
    }
    
    private void initData () {
        progressDialog = new ProgressDialog (this);
        Utils.setTypefaceToAllViews (this, tvRefresh);
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            webView.setVisibility (View.VISIBLE);
            getWebView ();
        } else {
            webView.setVisibility (View.GONE);
            rlNoInternetAvailable.setVisibility (View.VISIBLE);
        }
        userDetailsPref = UserDetailsPref.getInstance ();
    }
    
    private void initListener () {
        tvRefresh.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
                    rlNoInternetAvailable.setVisibility (View.GONE);
                    webView.setVisibility (View.VISIBLE);
                    getWebView ();
                }
            }
        });
    }
    
    private void getWebView () {
        webView.setWebViewClient (new CustomWebViewClient ());
        WebSettings webSetting = webView.getSettings ();
        webSetting.setJavaScriptEnabled (true);
        webSetting.setDomStorageEnabled (true);
        webSetting.setDisplayZoomControls (true);
        webSetting.setAppCacheEnabled (true);
        webSetting.setAppCachePath (getCacheDir ().getPath ());
        webSetting.setCacheMode (WebSettings.LOAD_DEFAULT);
        
        Utils.showProgressDialog (this, progressDialog, getResources ().getString (R.string.progress_dialog_text_loading), true);
        webView.loadUrl (Constants.app_url);
    }
    
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack ()) {
            Utils.showProgressDialog (MainActivity.this, progressDialog, getResources ().getString (R.string.progress_dialog_text_loading), true);
            webView.goBack ();
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }
    
    public void onBackPressed () {
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .content ("Do you really want to exit")
                .positiveColor (getResources ().getColor (R.color.primary_text))
                .contentColor (getResources ().getColor (R.color.primary_text))
                .negativeColor (getResources ().getColor (R.color.primary_text))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .canceledOnTouchOutside (true)
                .cancelable (true)
                .positiveText ("Yes")
                .negativeText ("No")
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish ();
                    }
                }).build ();
        dialog.show ();
    }
    
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            if (url.startsWith ("mailto:")) {
                startActivity (new Intent (Intent.ACTION_SENDTO, Uri.parse (url)));
            } else if (url.startsWith ("tel:")) {
                startActivity (new Intent (Intent.ACTION_DIAL, Uri.parse (url)));
            } else {
                Utils.showProgressDialog (MainActivity.this, progressDialog, getResources ().getString (R.string.progress_dialog_text_loading), true);
                view.loadUrl (url);
            }
            return true;
        }
        
        @Override
        public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
            if (view.canGoBack ()) {
                view.goBack ();
            }
//            Utils.showToast (MainActivity.this, description, false);
        }
        
        public void onPageFinished (WebView view, String url) {
            progressDialog.dismiss ();
        }
    }
    
    private void initApplication () {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager ().getPackageInfo (getPackageName (), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        }
        final PackageInfo finalPInfo = pInfo;
        final String android_id = Settings.Secure.getString (getContentResolver (), Settings.Secure.ANDROID_ID);
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_INIT, true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_INIT,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        if (jsonObj.getInt (AppConfigTags.VERSION_UPDATE) > 0) {
                                            if (jsonObj.getInt (AppConfigTags.VERSION_CRITICAL) == 1) {
                                                MaterialDialog dialog = new MaterialDialog.Builder (MainActivity.this)
                                                        .title ("New Update Available")
                                                        .content (jsonObj.getString (AppConfigTags.UPDATE_MESSAGE))
                                                        .titleColor (getResources ().getColor (R.color.primary_text))
                                                        .positiveColor (getResources ().getColor (R.color.primary_text))
                                                        .contentColor (getResources ().getColor (R.color.primary_text))
                                                        .negativeColor (getResources ().getColor (R.color.primary_text))
                                                        .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                        .canceledOnTouchOutside (false)
                                                        .cancelable (false)
                                                        .positiveText (R.string.dialog_action_update)
                                                        .negativeText (R.string.dialog_action_exit)
                                                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                final String appPackageName = getPackageName ();
                                                                try {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        })
                                                        .onNegative (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                finish ();
                                                                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                            }
                                                        }).build ();
            
                                                dialog.getActionButton (DialogAction.POSITIVE).setOnClickListener (new CustomListener (MainActivity.this, dialog, DialogAction.POSITIVE));
                                                dialog.getActionButton (DialogAction.NEGATIVE).setOnClickListener (new CustomListener (MainActivity.this, dialog, DialogAction.NEGATIVE));
                                                dialog.show ();
                                            } else {
                                                MaterialDialog dialog = new MaterialDialog.Builder (MainActivity.this)
                                                        .title ("New Update Available")
                                                        .content (jsonObj.getString (AppConfigTags.UPDATE_MESSAGE))
                                                        .titleColor (getResources ().getColor (R.color.primary_text))
                                                        .positiveColor (getResources ().getColor (R.color.primary_text))
                                                        .contentColor (getResources ().getColor (R.color.primary_text))
                                                        .negativeColor (getResources ().getColor (R.color.primary_text))
                                                        .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                        .canceledOnTouchOutside (true)
                                                        .cancelable (true)
                                                        .positiveText (R.string.dialog_action_update)
                                                        .negativeText (R.string.dialog_action_ignore)
                                                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                final String appPackageName = getPackageName ();
                                                                try {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        }).build ();
                                                dialog.show ();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<> ();
                    params.put (AppConfigTags.APP_VERSION, String.valueOf (finalPInfo.versionCode));
                    params.put (AppConfigTags.DEVICE_TYPE, "ANDROID");
                    params.put (AppConfigTags.DEVICE_ID, android_id);
                    params.put (AppConfigTags.FIREBASE_ID, userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.FIREBASE_ID));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy (new DefaultRetryPolicy (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);
        }
    }
    
    class CustomListener implements View.OnClickListener {
        private final MaterialDialog dialog;
        Activity activity;
        DialogAction dialogAction;
        
        public CustomListener (Activity activity, MaterialDialog dialog, DialogAction dialogAction) {
            this.dialog = dialog;
            this.activity = activity;
            this.dialogAction = dialogAction;
        }
        
        @Override
        public void onClick (View v) {
            if (dialogAction == DialogAction.POSITIVE) {
                final String appPackageName = getPackageName ();
                try {
                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            if (dialogAction == DialogAction.NEGATIVE) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
    }
}