package com.example.neilyi.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.neilyi.web.view.AdvancedWebView;
import com.example.neilyi.web.view.WebpageErrorView;
import com.example.neilyi.web.view.ptr.PtrDefaultHandler;
import com.example.neilyi.web.view.ptr.PtrFrameLayout;
import com.example.neilyi.web.view.ptr.PtrHandler;
import com.example.neilyi.web.view.ptr.PullToRefreshFrameLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 所有的网页都通过继承这个页面来做
 *
 * Created by NeilYi on 2016/9/20.
 */
public abstract class WebFragment extends BaseWebFragment {

    private final int TIME_OUT_DELAY = 45 * 1000;

    protected Activity mActivity;
    protected ViewGroup mRootView;
    protected AdvancedWebView mWebView;
    protected WebpageErrorView mErrorView;
    protected PullToRefreshFrameLayout mPtrFrame;

    protected String mUrl;
    protected String mTitle;
    protected boolean mIsLoading;
    protected boolean mIsErroePage;
    protected boolean mIsErrorViewShow;               //自定义错误界面是否展示
    protected boolean mIsPullRefreshing;
    protected boolean mHasEverSucceeded = false;
    protected long mLastErrorTime;

    protected View.OnClickListener mErrorRefreshClickListener;
    protected OnTitleBarTextListener mOnTitleBarTextListener;

    /**
     * 根据加载页面的标题设置自己TitleBar的标题监听
     */
    public void setOnTitleBarTextListener(final OnTitleBarTextListener listener) {
        mOnTitleBarTextListener = listener;
    }

    /**
     * createClickListener
     */
    private void createClickListener() {
        /**
         * 自定义错误界面点击刷新
         */
        mErrorRefreshClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mErrorView != null && mErrorView.getVisibility() == View.VISIBLE) {
                    mErrorView.setVisibility(View.GONE);
                }

                if (mWebView != null && mWebView.getVisibility() == View.GONE) {
                    mWebView.setVisibility(View.VISIBLE);
                }

                onRefreshCurrentPage();
            }
        };
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (hasError()) {
                return;
            }

            if (!TextUtils.isEmpty(url) && url.contains("http") && !url.contains("/l/s.jsp")) {
                mUrl = url;
            }

            startTimeoutTimer();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (hasError()) {
                return;
            }

            // 隐藏加载错误的界面
            if (mIsErrorViewShow && !mIsLoading) {
                hideErrorView();
            }

            // 页面标题获取处理
            if (mWebView != null) {
                try {
                    mTitle = mWebView.getTitle();
                    if (!TextUtils.isEmpty(mTitle) && !"about:blank".equals(mTitle) && !mIsErrorViewShow) {
                        if (null != mOnTitleBarTextListener) {
                            mOnTitleBarTextListener.onTitleBarText(mTitle);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            // 页面加载完成处理操作
            if (!mIsErroePage) {
                mHasEverSucceeded = true;
            }

            if (!TextUtils.isEmpty(url)) {
                mUrl = url;
            }

            refreshComplete();
            stopTimeoutTimer();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            setLastError();

            if (!TextUtils.isEmpty(failingUrl)) {
                mUrl = failingUrl;
            }
            mIsLoading = false;
            mIsErroePage = true;
            mHasEverSucceeded = false;

            refreshComplete();
            stopProgressLoad();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            mIsErroePage = true;
            mHasEverSucceeded = false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Avoid error page clicking refresh button, redirected url double load leading to page's
            // back history stack plus one(That is:page can not go back)
            if (!TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(url)
                    && (mUrl.contains(url) || url.contains(mUrl))) {
                return false;
            }

            if (overrideUrlLoading(view, url)) {
                return true;
            }
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (!mIsErroePage) {
                mHasEverSucceeded = true;
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (!TextUtils.isEmpty(url) && !url.contains("about:blank")
                    && (url.startsWith("http") || url.startsWith("https"))) {
                mUrl = url;
            }
            mIsLoading = false;
            stopTimeoutTimer();
        }
    };

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            super.onProgressChanged(view, progress);
            onProgressBarChanged(progress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitle = title;
            if (!TextUtils.isEmpty(title) && !"about:blank".equals(title)) {
                try {
                    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
                    if (null != pattern) {
                        Matcher m = pattern.matcher(title);
                        if (null != m && m.find() && !mIsErrorViewShow) {
                            if (null != mOnTitleBarTextListener) {
                                mOnTitleBarTextListener.onTitleBarText(title);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }
        }

        @SuppressWarnings("all")
        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            if (null != quotaUpdater) {
                quotaUpdater.updateQuota(requiredStorage * 2);
            }
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            if (quotaUpdater != null) {
                quotaUpdater.updateQuota(estimatedSize * 2);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLoading = false;
        mIsErroePage = false;
        mIsPullRefreshing = false;
        mIsStartedProgress = false;
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = (ViewGroup) inflater.inflate(R.layout.base_webview_page, container, false);
        initView(mRootView, inflater);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startProgressLoad();
    }

    /**
     * 初始化控件View
     */
    private void initView(final ViewGroup rootView, final LayoutInflater inflater) {
        if (null == mActivity || null == rootView || null == inflater) {
            return;
        }

        createClickListener();
        mErrorView = (WebpageErrorView) inflater.inflate(R.layout.web_error_page, null);
        mErrorView.setRefreshBtnClickListener(mErrorRefreshClickListener);

        initialPtrView();
        initialWebView();
        addViewToRootView(rootView);
    }

    protected void addViewToRootView(final ViewGroup rootView) {
        if (null != mWebView && null != mPtrFrame && null != mErrorView) {
            mPtrFrame.addViewForPtrFrameLayout(mWebView);
            rootView.addView(mPtrFrame);
            rootView.addView(mErrorView);
        }
    }

    /**
     * 初始化WebView
     */
    private void initialWebView() {
        if (null == mActivity) {
            return;
        }

        disableAccessibility();

        mWebView = initWebView();
        if (null == mWebView) {
            mWebView = new AdvancedWebView(mActivity);
        }

//            mWebView.addPermittedHostname(DEFAULT_FILTER_PATTERN);
        mWebView.setListener(mActivity, WebFragment.this);
        mWebView.setExtWebViewClient(mWebViewClient);
        mWebView.setExtWebChromeClient(mWebChromeClient);
        mWebView.setBackgroundColor(mActivity.getResources().getColor(R.color.background_color_oct));
        mWebView.setLayoutParams(new PtrFrameLayout.LayoutParams(PtrFrameLayout.LayoutParams.MATCH_PARENT,
                PtrFrameLayout.LayoutParams.MATCH_PARENT));
        configureWebView(mWebView);
    }

    /**
     * 初始化下拉刷新控件View
     */
    protected void initialPtrView() {
        if (null == mActivity) {
            return;
        }

        mPtrFrame = new PullToRefreshFrameLayout(mActivity);
        mPtrFrame.setPinContent(true);
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(300);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.setLayoutParams(new PtrFrameLayout.LayoutParams(PtrFrameLayout.LayoutParams.MATCH_PARENT,
                PtrFrameLayout.LayoutParams.MATCH_PARENT));
        setPullRefreshEnable(true, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        onWebViewResume();
    }

    protected void onWebViewResume() {
        if (null != mWebView) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        onWebViewPause();
        super.onPause();
    }

    protected void onWebViewPause() {
        if (null != mWebView) {
            mWebView.onResume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        onWebActivityResult(requestCode, resultCode, intent);
    }

    public void onWebActivityResult(int requestCode, int resultCode, Intent intent) {
        if (null != mWebView) {
            mWebView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        if(null != mActivity) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(intent);
        }
    }

    private void releaseWebView() {
        if (null != mWebView) {
            mWebView.onDestroy();
            mWebView = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        releaseWebView();

        if (null != mPtrFrame) {
            mPtrFrame.releaseResource();
            mPtrFrame = null;
        }

        if (null != mErrorView) {
            mErrorView.releaseResource();
            mErrorView = null;
        }

        if (null != mRootView) {
            mRootView.removeAllViews();
            mRootView = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (null != mActivity) {
            mActivity = null;
        }

        if (null != mTimeoutHandler) {
            mTimeoutHandler.removeCallbacksAndMessages(null);
            mTimeoutHandler = null;
        }

        if (null != mOnTitleBarTextListener) {
            mOnTitleBarTextListener = null;
        }

        if (null != mErrorRefreshClickListener) {
            mErrorRefreshClickListener = null;
        }
        super.onDestroy();
    }

    /**
     * 页面加载URL展示
     */
    public void loadUrl(final String url, boolean clearCache) {
        if (null == mWebView || TextUtils.isEmpty(url)) {
            return;
        }

        if (!TextUtils.isEmpty(url) && url.startsWith("javascript")) {
            mWebView.loadJavaScript(url);
            return;
        }

        mUrl = url;
        mIsLoading = true;
        mIsErroePage = false;
        startProgressLoad();

        mWebView.loadUrl(mUrl, clearCache);
    }

    /**
     * 刷新当前页面（下拉刷新/错误界面点击刷新按钮）
     */
    protected void onRefreshCurrentPage() {
        onRefresh();
    }

    /**
     * 刷新当前页面
     */
    protected void onRefresh() {
        loadUrl(mUrl, true);
    }

    /**
     * 停止下拉刷新
     */
    protected void refreshComplete() {
        if (null != mPtrFrame) {
            mPtrFrame.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPtrFrame != null) {
                        mPtrFrame.refreshComplete();
                    }
                }
            }, 500);
        }
        mIsPullRefreshing = false;
    }

    private volatile boolean mIsStartedProgress = false;
    private final int MAX_PROGRESS = 100;
    private final int BREAK_POINT_PROCESS = 95;
    private int mProgress;

    /**
     * Progress进度条
     */
    public void onProgressBarChanged(int progress) {
        mProgress = progress;
        if (progress < MAX_PROGRESS && mProgress != progress) {
            if (!mIsStartedProgress) {
                startProgressLoad();
            }
        } else if (progress >= MAX_PROGRESS) {
            stopProgressLoad();
        }
    }

    /**
     * 开始加载页面进度条
     */
    public void startProgressLoad() {
        if(mIsStartedProgress){
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mPtrFrame) {
                    mPtrFrame.showAutoRefresh(true);
                }
                mIsStartedProgress = true;
            }
        }, 500);
    }

    /**
     * 停止页面进度条
     */
    public void stopProgressLoad() {
        mIsStartedProgress = false;

        if (null != mPtrFrame) {
            mPtrFrame.refreshComplete();
        }
    }

    /**
     * 显示错误界面
     */
    public void showErrorView(String errMsg) {
        showErrorView();
    }

    /**
     * 显示错误界面
     */
    public void showErrorView() {
        if (null != mErrorView) {
            mIsErrorViewShow = true;
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.startImgAnim();
        }

        if (null != mWebView && mWebView.getVisibility() == View.VISIBLE) {
            //用javascript隐藏系统定义的404页面信息
            String data = "";
            mWebView.loadJavaScript("javascript:document.body.innerHTML=\"" + data + "\"");
            mWebView.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏错误界面
     */
    public void hideErrorView() {
        if (null != mErrorView) {
            mIsErrorViewShow = false;
            mErrorView.stopImgAnim();
            if (mErrorView.getVisibility() == View.VISIBLE) {
                mErrorView.setVisibility(View.GONE);
            }
        }

        if (null != mWebView && mWebView.getVisibility() == View.GONE) {
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    public synchronized void startTimeoutTimer() {
        if (mTimeoutHandler != null) {
            mTimeoutHandler.sendEmptyMessageDelayed(0, TIME_OUT_DELAY);
        }
    }

    public synchronized void stopTimeoutTimer() {
        if(mTimeoutHandler != null) {
            mTimeoutHandler.removeMessages(0);
        }
    }

    protected void setLastError() {
        mLastErrorTime = System.currentTimeMillis();
    }

    protected boolean hasError() {
        return (mLastErrorTime + 500) >= System.currentTimeMillis();
    }

    public boolean isHasEverSucceeded() {
        return mHasEverSucceeded;
    }

    /**
     * 加载本地Html页面
     */
    public void loadHtml(String baseUrl, String data) {
        if (null == mWebView) {
            return;
        }

        mWebView.loadHtml(baseUrl, data);
    }

    /**
     * 调用页面JS方法
     */
    public void loadJavaScript(final String javascript) {
        if (null == mWebView) {
            return;
        }

        mWebView.loadJavaScript(javascript);
    }

    /**
     * 调用页面JS方法用于其他线程调用
     */
    public void loadJavaScriptByPost(final String javascript) {
        if (null == mWebView) {
            return;
        }
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadJavaScript(javascript);
            }
        });
    }

    /**
     * 判断页面是否能够后退
     *
     * true:页面不能后退，退出界面
     * false:页面能后退，不退出界面，WebView后退
     */
    public boolean onBackPressed() {
        if (null == mWebView) {
            return true;
        }

        mIsLoading = true;
        startProgressLoad();

        return mWebView.onBackPressed();
    }

    public AdvancedWebView getWebView() {
        return mWebView;
    }

    public void setUrl(final String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    private Handler mTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTimeoutHandler == null) {
                return;
            }

            if (mWebView != null) {
                mWebView.stopLoading();

                if (mWebViewClient != null) {
                    mWebViewClient.onReceivedError(mWebView, 0, null, null);
                    showErrorView();
                }
            }

            stopProgressLoad();
            mTimeoutHandler.removeMessages(0);
        }
    };

    /**
     * 设置页面是否支持下拉刷新
     */
    public void setPullRefreshEnable(final boolean enable, final boolean disableWhenHoriMove) {
        if (null == mPtrFrame) {
            return;
        }

        if (enable) {
            mPtrFrame.setEnabled(true);
            mPtrFrame.setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    if (null != mWebView) {
                        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mWebView, header);
                    } else {
                        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                    }
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    mIsPullRefreshing = true;
                    onPullRefreshStart();
                    onRefreshCurrentPage();
                }
            });
        } else {
            mPtrFrame.setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return false;
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                }
            });
        }

        mPtrFrame.disableWhenHorizontalMove(disableWhenHoriMove);
    }
}
