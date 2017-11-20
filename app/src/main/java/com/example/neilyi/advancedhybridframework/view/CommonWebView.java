package com.example.neilyi.advancedhybridframework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;

import com.example.neilyi.advancedhybridframework.hybride.JSBridgeWebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * CommonWebView
 *
 * Created by NeilYi on 2017/2/23.
 */
public class CommonWebView extends JSBridgeWebView {

    public CommonWebView(Context context) {
        super(context);
        init();
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        addDefaultHttpHeader();

        final WebSettings webSetting = getSettings();
        webSetting.setUserAgentString(getWebUserAgent());
        webSetting.setTextSize(WebSettings.TextSize.NORMAL);
        webSetting.setSaveFormData(true);                           //设置WebView是否保存表单数据,默认true,保存数据
        webSetting.setGeolocationEnabled(true);                     //HTML5的地理位置服务,设置为true,启用地理定位
        webSetting.setLoadWithOverviewMode(true);                   // 设置网页自适应屏幕大小
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);  //  true support js can open new window

        requestFocus();
        setScrollbarFadingEnabled(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        hideFadingEdge();
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public static String getWebUserAgent() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String release = android.os.Build.VERSION.RELEASE;
        String ua = "CMREADBC_Android_" + "(" + manufacturer + ";" + model + ";Android " + release + ";cn;JSBridge=1.0);";
        return ua;
    }

    /**
     * 添加默认请求头信息
     */
    private void addDefaultHttpHeader() {
        addHttpHeader("x-imsi", "");
        addHttpHeader("x-macaddress", "");
    }

    /**
     * 通过反射隐藏滚动阴影边框
     */
    private void hideFadingEdge() {
        try {
            Method method = getClass().getMethod("setOverScrollMode", int.class);
            Field field = getClass().getField("OVER_SCROLL_NEVER");
            if (method != null && field != null) {
                method.invoke(this, field.getInt(View.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUrl(String url) {
        if (this == null) {
            return;
        }
        super.loadUrl(url);
    }
}
