package com.example.neilyi.web;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.example.neilyi.web.view.AdvancedWebView;

import java.lang.reflect.Method;

/**
 * BaseWebFragment
 *
 * Created by NeilYi on 2016/9/21.
 */
public abstract class BaseWebFragment extends android.support.v4.app.Fragment implements AdvancedWebView.AdvancedWebviewListener {
    private static final String TAG = "BaseWebFragment";

    protected boolean b_isViewDestroyed;

    /**
     * 页面标题加载接口回调
     */
    public interface OnTitleBarTextListener {
        void onTitleBarText(String title);
    }

    /**
     * verify the url is valide
     */
    public static boolean isUrlValide(final String url) {
        return true;
    }

    /**
     * 关闭辅助功能，针对4.2.1和4.2.2 崩溃问题
     * java.lang.NullPointerException
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper$1.onInit(AccessibilityInjector.java:753)
     * at android.webkit.CallbackProxy.handleMessage(CallbackProxy.java:321)
     */
    protected void disableAccessibility() {
        if (Build.VERSION.SDK_INT == 17/*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/) {
            Context context;
            if ((context = getContext()) != null) {
                try {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (!am.isEnabled()) {
                        return;
                    }
                    Method set = am.getClass().getDeclaredMethod("setState", int.class);
                    set.setAccessible(true);
                    set.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 下拉刷新开始
     */
    protected void onPullRefreshStart() {}
    /**
     * 创建自定义的WebView
     */
    protected abstract AdvancedWebView initWebView();
    /**
     * 对WebView进行设置，比如是否支持下拉刷新，WebSetting设置等
     */
    protected abstract void configureWebView(AdvancedWebView webView);
    /**
     * 页面超链接点击跳转
     */
    protected abstract boolean overrideUrlLoading(WebView view, String url);
}
