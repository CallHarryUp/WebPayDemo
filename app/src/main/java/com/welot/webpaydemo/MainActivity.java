package com.welot.webpaydemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private static final String LOT_URL = "";
    private WebView mainWeb;
    private SharedPreferences sharedPreferences;
    private String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initWebTokenData() {
        sharedPreferences = getSharedPreferences("user-config", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);
        String token = sharedPreferences.getString("token", null);
        if (uid != null && token != null) {
            cookie = "uidToken=" + uid + "@" + token;
        } else {
            cookie = null;
        }
        CookieManager manager = CookieManager.getInstance();
        String managerCookie = manager.getCookie(LOT_URL);
        Log.d("111", "managecookie:" + managerCookie);
        /*//根据版本不同,用不同的方法刷新cookie
        //根据版本不同，用不同的方法刷新cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //manager.removeSessionCookies(null);//参数是一个监听
            manager.flush();
        } else {
            //manager.removeSessionCookie();
            CookieSyncManager.createInstance(this);//刷新cookie.sync()
        }*/
        manager.setAcceptCookie(true);//设置允许cookie
        manager.setCookie(LOT_URL, cookie);
        //cookie = null;
        String managerCookie2 = manager.getCookie(LOT_URL);
        Log.d("111", "managecookie:" + managerCookie2);

    }


    private void initView() {
        mainWeb = ((WebView) findViewById(R.id.pay_web));
        initState();
        mainWeb.loadUrl(LOT_URL);
        mainWeb.setWebChromeClient(new WebChromeClient());
        mainWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("111","url:"+url);
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void initState() {
        mainWeb.canGoBack();
        mainWeb.canGoForward();
        WebSettings settings = mainWeb.getSettings();
        //设置支持JS代码
        settings.setJavaScriptEnabled(true);
        //设置自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //缩放操作
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //获取当前的url
        Log.d("111","url:"+mainWeb.getUrl());

        if (keyCode == KeyEvent.KEYCODE_BACK && mainWeb.getUrl().equals(LOT_URL)) {
            finish();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && mainWeb.canGoBack()) {
            mainWeb.goBack();
            return true;//点击返回键 是返回上一个页面
        }

        if (mainWeb.getUrl().equals(LOT_URL)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainWeb.destroy();
    }

   /* @Override
    protected void onResume() {
        super.onResume();
       // initWebTokenData();//当页面进行显示的时候 重新初始化数据
        //mainWeb.reload();//登录完毕之后 重新刷入当前页面
       // mainWeb.onResume(); //置为活跃状态   执行webview的响应
    }*/
}
