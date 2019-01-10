package com.newware.sampathlicadvisor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    WebView webView;
    //SwipeRefreshLayout swipe;
    String url = "http://www.bangalorelicinsurance.com";
    private ProgressDialog progDailog;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.wvWeb);
        activity = this;
        progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
        progDailog.setCancelable(true);
        load(url);
    }

    private void load(String url)
    {
        //swipe.setRefreshing(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // for enabling website menus and other dynamic
        webSettings.setAppCacheEnabled(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        //webSettings.setDisplayZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient()
        {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                //progDailog.setIcon();
                progDailog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (URLUtil.isNetworkUrl(url))
                {
                    return false;
                }
                if (appInstalledOrNot(url))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                if (url.startsWith("whatsapp://") || url.startsWith("facebook://") || url.startsWith("whatsapp://"))
                {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if (Uri.parse(url).getScheme().equals("market"))
                {
                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e)
                    {
                        // Google Play app is not installed, you may want to open the app store link
                        Uri uri = Uri.parse(url);
                        view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                        return false;
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "App mot installed", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }

            @Override
            public void onPageFinished(WebView view, final String url)
            {
                progDailog.dismiss();
                //super.onPageFinished(view,url);
            }
        });

        //webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
        webView.loadUrl(url);
        //swipe.setRefreshing(false);
    }

    private boolean appInstalledOrNot(String uri)
    {
        PackageManager pm = getPackageManager();
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e)
        {
        }

        return false;
    }

    public void onBackPressed()
    {
        if (webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            final View view = layoutInflater.inflate(R.layout.sample, null);
            builder.setView(view);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            }).setCancelable(false);
            AlertDialog dialog = builder.show();
            // Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.alert);
            TextView messageView = dialog.findViewById(android.R.id.message);
            if (messageView != null)
            {
                messageView.setGravity(Gravity.CENTER);
            }
        }
    }

    public void exit(View view)
    {
        finish();
    }
}
