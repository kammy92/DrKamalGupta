package com.bjp.drkamalgupta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bjp.drkamalgupta.R;
import com.bjp.drkamalgupta.utils.Constants;
import com.bjp.drkamalgupta.utils.NetworkConnection;
import com.bjp.drkamalgupta.utils.SetTypeFace;
import com.bjp.drkamalgupta.utils.Utils;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    RelativeLayout rlNoInternetAvailable;
    ProgressDialog progressDialog;
    TextView tvRefresh;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListener ();
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
}
