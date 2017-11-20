package com.example.neilyi.advancedhybridframework.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import com.example.neilyi.advancedhybridframework.view.CommonWebView;
import com.example.neilyi.web.WebFragment;
import com.example.neilyi.web.view.AdvancedWebView;

/**
 * 通用二级页面-Fragment
 *
 * Created by NeilYi on 2016/9/22.
 */
public class CommonWebFragment extends WebFragment {

    private boolean mDisablePullRefresh = false;

    public void setSupprotPullRefresh(boolean supprotPullRefresh) {
        this.mDisablePullRefresh = supprotPullRefresh;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUrl(mUrl, false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        showErrorView();
    }

    @Override
    public void onExternalPageRequest(String url, boolean isHostNameForbiddon) {
        showErrorView();
    }

    @Override
    protected AdvancedWebView initWebView() {
        Activity act = getActivity();
        if (act != null) {
            return new CommonWebView(act);
        }
        return null;
    }

    @Override
    protected void configureWebView(AdvancedWebView webView) {
        if (null == webView) {
            return;
        }

//      webView.addPermittedHostname("");   // 设置禁止的host地址
        webView.setVerticalScrollBarEnabled(false);
        if (mDisablePullRefresh) {          // 是否禁止下拉刷新功能
            setPullRefreshEnable(false, true);  // 禁止下拉刷新功能
        }
    }

    @Override
    protected boolean overrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http") || url.startsWith("https")) {
            loadUrl(url, false);
        } else {
            Activity activity = getActivity();
            if (activity != null && !activity.isFinishing()) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void refresh() {
        onRefresh();
    }
}
