package com.example.neilyi.web.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Advanced WebView component for Android
 *
 * Created by NeilYi on 2017/11/16.
 */
public class AdvancedWebView extends WebView {
    private static final String TAG = "AdvancedWebview";

    public interface AdvancedWebviewListener {
        void onPageStarted(String url, Bitmap favicon);
        void onPageFinished(String url);
        void onPageError(int errorCode, String description, String failingUrl);
        void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent);
        void onExternalPageRequest(String url, boolean isHostNameForbiddon);
    }

    /**
     * Invoke download system setting
     */
    public static final String PACKAGE_NAME_DOWNLOAD_MANAGER = "com.android.providers.downloads";
    /**
     * Upload file picker request code
     */
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected String mUploadableFileTypes = "*/*";

    protected static final String CHARSET_DEFAULT = "UTF-8";
    protected static final String LANGUAGE_DEFAULT_ISO3 = "eng";
    protected static final String DATABASES_SUB_FOLDER = "/databases";

    /**
     * Alternative browsers that have their own rendering engine and may be installed on this device
     */
    protected static final String[] ALTERNATIVE_BROWSERS = new String[] {
            "org.mozilla.firefox",
            "com.android.chrome",
            "com.opera.browser",
            "org.mozilla.firefox_beta",
            "com.chrome.beta",
            "com.opera.browser.beta" };

    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;
    protected AdvancedWebviewListener mWebListener;
    protected WebViewClient mCustomWebViewClient;
    protected WebChromeClient mCustomWebChromeClient;

    protected long mLastError;
    protected String mLanguageIso3;
    protected boolean mGeolocationEnabled;

    protected final Map<String, String> mHttpHeaders = new HashMap<String, String>();
    protected final List<String> mPermittedHostnames = new LinkedList<String>();

    public AdvancedWebView(Context context) {
        super(context);
        try {
            initial(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdvancedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            initial(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdvancedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            initial(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SetListener, you are supposed to call this method first
     */
    public void setListener(final Activity activity, final AdvancedWebviewListener listener) {
        setListener(activity, listener, REQUEST_CODE_FILE_PICKER);
    }

    /**
     * SetListener, you are supposed to call this method first
     */
    public void setListener(final Activity activity, final AdvancedWebviewListener listener,
                            final int requestCodeFilePicker) {
        if (activity != null) {
            mActivity = new WeakReference<Activity>(activity);
        } else {
            mActivity = null;
        }

        setListener(listener, requestCodeFilePicker);
    }

    /**
     * SetListener, you are supposed to call this method first
     */
    public void setListener(final Fragment fragment, final AdvancedWebviewListener listener) {
        setListener(fragment, listener, REQUEST_CODE_FILE_PICKER);
    }

    /**
     * SetListener, you are supposed to call this method first
     */
    public void setListener(final Fragment fragment, final AdvancedWebviewListener listener,
                            final int requestCodeFilePicker) {
        if (fragment != null) {
            mFragment = new WeakReference<Fragment>(fragment);
        } else {
            mFragment = null;
        }

        setListener(listener, requestCodeFilePicker);
    }

    /**
     * SetListener, you are supposed to call this method first
     */
    protected void setListener(final AdvancedWebviewListener listener, final int requestCodeFilePicker) {
        mWebListener = listener;
        mRequestCodeFilePicker = requestCodeFilePicker;
    }

    public void setExtWebViewClient(final WebViewClient client) {
        mCustomWebViewClient = client;
    }

    public void setExtWebChromeClient(final WebChromeClient client) {
        mCustomWebChromeClient = client;
    }

    /**
     * Set WebView Geolocation Switch
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void setGeolocationEnabled(final boolean enabled) {
        if (enabled) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setGeolocationEnabled(true);
            setGeolocationDatabasePath();
        }

        mGeolocationEnabled = enabled;
    }

    @SuppressLint("NewApi")
    protected void setGeolocationDatabasePath() {
        final Activity activity;

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11
                && mFragment.get().getActivity() != null) {
            activity = mFragment.get().getActivity();
        } else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get();
        } else {
            return;
        }

        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    /**
     * setUploadableFileTypes
     */
    public void setUploadableFileTypes(final String mimeType) {
        mUploadableFileTypes = mimeType;
    }

    /**
     * Adds an additional HTTP header for every HTTP `GET` request
     *
     * @param name the name of the HTTP header
     * @param value the value of the HTTP header
     */
    public void addHttpHeader(final String name, final String value) {
        mHttpHeaders.put(name, value);
    }

    /**
     * Removes one of the HTTP headers that have previously been added via `addHttpHeader()`
     *
     * @param name the name of the HTTP header
     */
    public void removeHttpHeader(final String name) {
        mHttpHeaders.remove(name);
    }

    /**
     * addPermittedHostname
     *
     * @param hostname allowed hostname
     */
    public void addPermittedHostname(String hostname) {
        mPermittedHostnames.add(hostname);
    }

    /**
     * addPermittedHostnames
     *
     * @param collection allowed collection hostnames
     */
    public void addPermittedHostnames(Collection<? extends String> collection) {
        mPermittedHostnames.addAll(collection);
    }

    /**
     * getPermittedHostnames
     */
    public List<String> getPermittedHostnames() {
        return mPermittedHostnames;
    }

    /**
     * removePermittedHostname
     */
    public void removePermittedHostname(String hostname) {
        mPermittedHostnames.remove(hostname);
    }

    /**
     * clearPermittedHostnames
     */
    public void clearPermittedHostnames() {
        mPermittedHostnames.clear();
    }

    /**
     * setCookiesEnabled
     */
    @SuppressWarnings("static-method")
    public void setCookiesEnabled(final boolean enabled) {
        CookieManager.getInstance().setAcceptCookie(enabled);
    }

    /**
     * Sets whether the {@link WebView} should allow third party cookies to be set.
     */
    public void setThirdPartyCookiesEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled);
        }
    }

    /**
     * Whether allow unsafe site(http) access safe site(https).
     */
    public void setMixedContentAllowed(final boolean allowed) {
        setMixedContentAllowed(getSettings(), allowed);
    }

    /**
     * setDesktopMode
     */
    public void setDesktopMode(final boolean enabled) {
        final WebSettings webSettings = getSettings();

        if (null == webSettings) {
            return;
        }

        final String newUserAgent;
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        } else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
        }

        webSettings.setUserAgentString(newUserAgent);
        webSettings.setUseWideViewPort(enabled);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setSupportZoom(enabled);
        webSettings.setBuiltInZoomControls(enabled);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onResume() {
        onResumeWithouTimer();
        resumeTimers();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onResumeWithouTimer() {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                super.onResume();
            } else {
                WebView.class.getMethod("onResume").invoke(this);//resume flash
            }

//            getSettings().setJavaScriptEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPause() {
        pauseTimers();
        onPauseWithoutTimer();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    public void onPauseWithoutTimer() {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                super.onPause();
            } else {
                WebView.class.getMethod("onPause").invoke(this);//stop flash
            }

//            getSettings().setJavaScriptEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public synchronized void onDestroy() {
        // 当我们对Activity进行finish的时候，webview持有的页面并不会立即释放，
        // 如果页面中有在执行js等其他操作，仅仅进行finish是完全不够的。
        try {
            super.stopLoading();
            super.clearFormData();
            super.clearMatches();
            super.destroyDrawingCache();
            super.clearDisappearingChildren();
            super.clearHistory();
            super.clearAnimation();
            super.setWebChromeClient(null);
            super.setWebViewClient(null);
            super.setDownloadListener(null);

            if (Build.VERSION.SDK_INT < 19) {
                super.freeMemory();
            }
        } catch (Exception e) {
        }

        // try to remove this view from its parent first
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception e) {
        }

        // then try to remove all child views from this view
        try {
            removeAllViews();
        } catch (Exception e) {
        }

        super.destroy();
        releaseResource();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    /**
     * 返回键/退出判断
     *
     * Created by liyi on 2016/9/20.
     */
    public boolean onBackPressed() {
        return checkWebViewGoback();
    }

    /**
     * 页面后退检查，若存在连续地址相同页面，直接跳转到地址相同的上一个页面，解决特定场景返回多次的问题.
     */
    private boolean checkWebViewGoback() {
        if (null == this) {
            return true;
        }

        boolean canReturn = false;

        WebBackForwardList history = copyBackForwardList();
        if (null != history) {
            int index = -1;
            String currentUrl = getUrl();
            while (canGoBackOrForward(index)) {
                int currentIndex = history.getCurrentIndex();
                String lastUrl = history.getItemAtIndex(currentIndex + index).getUrl();
                if (TextUtils.isEmpty(lastUrl) || !lastUrl.equals(currentUrl)) {
                    break;
                }

                index--;
            }

            if (canGoBackOrForward(index)) {
                if (getSettings() != null) {
                    getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                }

                goBackOrForward(index--);
                canReturn = false;
            } else {
                canReturn = true;
            }
        }

        return canReturn;
    }

    @Override
    public void goBack() {
        if (getSettings() != null) {
            getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        super.goBack();
    }

    /**
     * B plus C callback: load "javascript:xxxx"
     */
    public void loadJavaScript(String javascript) {
        if (TextUtils.isEmpty(javascript)) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 19) { //Build.VERSION_CODES.KITKAT
            String newJS = clipJacaScriptUrlString(javascript);
            if (!newJS.startsWith("javascript:")) {
                try{
                    super.evaluateJavascript(newJS, null);
                }catch (Exception | Error e){
                    super.loadUrl(javascript);
                    e.printStackTrace();
                }
            } else {
                super.loadUrl(javascript);
            }
        } else {
            super.loadUrl(javascript);
        }
    }

    /**
     * B plus C callback: load "javascript:xxxx"
     */
    public void loadJavaScript(String javascript, final ValueCallback<String> resultCallback) {
        if (TextUtils.isEmpty(javascript)) {
            return;
        }

        if(Build.VERSION.SDK_INT >= 19) { //Build.VERSION_CODES.KITKAT
            String newJS = clipJacaScriptUrlString(javascript);
            if (!newJS.startsWith("javascript:")) {
                try{
                    super.evaluateJavascript(newJS, resultCallback);
                }catch (Exception | Error e){
                    super.loadUrl(javascript);
                    e.printStackTrace();
                }
            } else {
                super.loadUrl(javascript);
            }
        } else {
            super.loadUrl(javascript);
        }
    }

    /**
     * SubString url "javaScript:" for evaluateJavascript method
     */
    private String clipJacaScriptUrlString (String javascript) {
        String newJavaScriptStrin = javascript;

        if (!TextUtils.isEmpty(javascript)) {
            int javaScriptStringIndex = javascript.indexOf(":");
            if (javaScriptStringIndex > 0) {
                try {
                    int clipIndex = javaScriptStringIndex + 1;
                    if (clipIndex > 0 && clipIndex <= javascript.length()) {
                        newJavaScriptStrin = javascript.substring(clipIndex, javascript.length());
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        return newJavaScriptStrin;
    }

    /**
     * Loads and displays the provided HTML
     */
    public void loadHtml(final String html) {
        loadHtml(null, html);
    }

    /**
     * Loads and displays the provided HTML
     */
    public void loadHtml(final String baseUrl, final String html) {
        loadHtml(baseUrl, html, null);
    }

    /**
     * Loads and displays the provided HTML
     */
    public void loadHtml(final String baseUrl, final String html, final String historyUrl) {
        loadHtml(baseUrl, html, "utf-8", historyUrl);
    }

    /**
     * Loads and displays the provided HTML
     */
    public void loadHtml(final String baseUrl, final String html, final String encoding,
                         final String historyUrl) {
        loadDataWithBaseURL(baseUrl, html, "text/html", encoding, historyUrl);
    }

    public void loadUrl(String url, final boolean clearCache) {
        final WebSettings webSetting = getSettings();
        if (null != webSetting) {
            if (!clearCache) {
                webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                super.loadUrl("javascript:clearSessionStorage()");
                webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        }

        loadUrl(url);
    }

    @Override
    public void loadUrl(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (url.startsWith("javascript:")) {
            loadJavaScript(url);
            return;
        }

        if (!checkURLEncodedValid(url)) {
            if (null != mWebListener) {
                mWebListener.onPageError(-1, "", url);
            }
            return;
        }

        try {
            if (Build.VERSION.SDK_INT > 8 && null != mHttpHeaders && mHttpHeaders.size() > 0) {
                super.loadUrl(url, mHttpHeaders);
            } else {
                super.loadUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Init Custom WebView
     */
    @SuppressWarnings("all")
    protected void initial(final Context context) {
        if (null == context) {
            // do not run the code from this method
            return;
        }

        if (context instanceof Activity) {
            mActivity = new WeakReference<Activity>((Activity)context);
        }

        mLanguageIso3 = getLanguageIso3();

        setFocusable(true);
        setSaveEnabled(true);
        setFocusableInTouchMode(true);
        setVerticalScrollBarEnabled(false);

        final WebSettings webSettings = getSettings();

        /**打开本地缓存提供JS调用**/
        try {
            if (Build.VERSION.SDK_INT == 15 || Build.VERSION.SDK_INT == 14) {
                // 14:Build.VERSION_CODES.ICE_CREAM_SANDWICH; 15:Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                webSettings.setDatabaseEnabled(false);
            } else {
                webSettings.setDatabaseEnabled(true);
            }

            final String filesDir = context.getFilesDir().getPath();
            if (filesDir != null && filesDir.length() > 0) {
                String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER;
                if (Build.VERSION.SDK_INT != 14 && Build.VERSION.SDK_INT != 15
                        && Build.VERSION.SDK_INT < 19 && !TextUtils.isEmpty(databaseDir)) {
                    webSettings.setDatabasePath(databaseDir);
                }
            }

            webSettings.setDomStorageEnabled(true);
            // Set cache size to 8 mb by default. should be more than enough
            webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
            // This next one is crazy. It's the DEFAULT location for your app's cache
            // But it didn't work for me without this line.
            // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
            String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath();
            if (!TextUtils.isEmpty(appCachePath)) {
                webSettings.setAppCachePath(appCachePath);
            }
            webSettings.setAppCacheEnabled(true);

            //Webview settings
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(false);
            setAllowAccessFromFileUrls(webSettings, false);
            webSettings.setSupportZoom(false);
            webSettings.setSavePassword(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            if (Build.VERSION.SDK_INT < 18) {
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            }

            setMixedContentAllowed(webSettings, true);
            setThirdPartyCookiesEnabled(true);

            // 设置4.2(Build.VERSION_CODES.JELLY_BEAN_MR1)以后版本支持autoPlay，非用户手势促发
            if (Build.VERSION.SDK_INT >= 17) {
                webSettings.setMediaPlaybackRequiresUserGesture(false);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }

        //setWebViewClient
        super.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!hasError()) {
                    if (mWebListener != null) {
                        mWebListener.onPageStarted(url, favicon);
                    }
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!hasError()) {
                    if (mWebListener != null) {
                        mWebListener.onPageFinished(url);
                    }
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                setLastError();

                if (mWebListener != null) {
                    mWebListener.onPageError(errorCode, description, failingUrl);
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }

                // JSBride interaction
                if (JSBridgeUrlLoading(view, url)) {
                    return true;
                }

                // if the hostname may not be accessed
                if (!isHostnameAllowed(url)) {
                    if (mWebListener != null) {
                        mWebListener.onExternalPageRequest(url, true);
                    }
                    return true;
                }

                // if there is a user-specified handler available
                if (mCustomWebViewClient != null) {
                    // if the user-specified handler asks to override the request
                    if (mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onLoadResource(view, url);
                } else {
                    super.onLoadResource(view, url);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedSslError(view, handler, error);
                } else {
                    super.onReceivedSslError(view, handler, error);
                }
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("all")
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (Build.VERSION.SDK_INT >= 11) {
                    if (mCustomWebViewClient != null) {
                        return mCustomWebViewClient.shouldInterceptRequest(view, url);
                    } else {
                        return super.shouldInterceptRequest(view, url);
                    }
                } else {
                    return null;
                }
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("all")
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mCustomWebViewClient != null) {
                        return mCustomWebViewClient.shouldInterceptRequest(view, request);
                    } else {
                        return super.shouldInterceptRequest(view, request);
                    }
                } else {
                    return null;
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
                } else {
                    super.doUpdateVisitedHistory(view, url, isReload);
                }
            }
        });

        super.setWebChromeClient(new WebChromeClient() {

            // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10))
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null);
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15))
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18))
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileInput(uploadMsg, null, false);
            }

            // file upload callback (Android 5.0 (API level 21) -- current)
            @SuppressWarnings("all")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= 21) {
                    final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;
                    openFileInput(null, filePathCallback, allowMultiple);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onProgressChanged(view, newProgress);
                } else {
                    super.onProgressChanged(view, newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReceivedTitle(view, title);
                } else {
                    super.onReceivedTitle(view, title);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (mGeolocationEnabled) {
                    callback.invoke(origin, true, false);
                } else {
                    if (mCustomWebChromeClient != null) {
                        mCustomWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
                    } else {
                        super.onGeolocationPermissionsShowPrompt(origin, callback);
                    }
                }
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onGeolocationPermissionsHidePrompt();
                } else {
                    super.onGeolocationPermissionsHidePrompt();
                }
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("all")
            public void onPermissionRequest(PermissionRequest request) {
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (mCustomWebChromeClient != null) {
                            mCustomWebChromeClient.onPermissionRequest(request);
                        } else {
                            super.onPermissionRequest(request);
                        }
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("NewApi")
            @SuppressWarnings("all")
            public void onPermissionRequestCanceled(PermissionRequest request) {
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (mCustomWebChromeClient != null) {
                            mCustomWebChromeClient.onPermissionRequestCanceled(request);
                        } else {
                            super.onPermissionRequestCanceled(request);
                        }
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                }
            }

            @SuppressWarnings("all")
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                /**
                 * true:屏蔽B页面log信息  false:打印B页面log信息
                 */
                return true;
            }

            @SuppressWarnings("all")
            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                } else {
                    super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                }
            }

            @SuppressWarnings("all")
            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                } else {
                    super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                }
            }

        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition,
                                        final String mimeType, final long contentLength) {
                final String suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                if (mWebListener != null) {
                    mWebListener.onDownloadRequested(url, suggestedFilename, mimeType,
                            contentLength, contentDisposition, userAgent);
                } else {
                    if (getContext() != null && getContext() instanceof Activity) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        ((Activity) getContext()).startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * fix 4.1系统加载网页时出现IllegalArgumentException
     *
     * Created by liyi on 2017/01/10.
     */
    @SuppressWarnings("all")
    protected boolean checkURLEncodedValid(final String url) {
        boolean isUrlValid = true;

//        if (Build.VERSION.SDK_INT != 16 && Build.VERSION.SDK_INT != 17) {
//            return isUrlValid;
//        }
//
//        try {
//            URLEncodedUtils.parse(new URI(url), null);
//        } catch (Exception e) {
//            isUrlValid = false;
//        }

        return isUrlValid;
    }

    /**
     * Returns whether file uploads can be used on the current device
     *
     * @return whether file uploads can be used
     */
    public static boolean isFileUploadAvailable() {
        return isFileUploadAvailable(false);
    }

    /**
     * Returns whether file uploads can be used on the current device
     *
     * On Android 4.4.3/4.4.4, file uploads may be possible but will come with a wrong MIME type
     */
    public static boolean isFileUploadAvailable(final boolean needsCorrectMimeType) {
        if (Build.VERSION.SDK_INT == 19) {
            final String platformVersion = (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;

            return !needsCorrectMimeType && (platformVersion.startsWith("4.4.3") || platformVersion.startsWith("4.4.4"));
        } else {
            return true;
        }
    }

    /**
     * Handles a download by loading the file from `fromUrl`
     * and saving it to `toFilename` on the external storage
     */
    @SuppressLint("NewApi")
    public static boolean handleDownload(final Context context, final String fromUrl, final String toFilename) {
        if (Build.VERSION.SDK_INT < 9) {
//            throw new RuntimeException("Method requires API level 9 or above");
            return false;
        }

        try {
            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fromUrl));
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, toFilename);

            final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            try {
                dm.enqueue(request);
            } catch (SecurityException e) {
                if (Build.VERSION.SDK_INT >= 11) {
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                }
                dm.enqueue(request);
            }

            return true;
        } catch (IllegalArgumentException e) {
            // show the settings screen where the user can enable the download manager app again
            openAppSettings(context, AdvancedWebView.PACKAGE_NAME_DOWNLOAD_MANAGER);

            return false;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    @SuppressLint("NewApi")
    private static boolean openAppSettings(final Context context, final String packageName) {
        if (Build.VERSION.SDK_INT < 9) {
            return false;
        }

        try {
            final Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断请求URL的Hostname是否被允许
     */
    protected boolean isHostnameAllowed(final String url) {
        if (null == mPermittedHostnames || mPermittedHostnames.size() == 0
                || TextUtils.isEmpty(url)) {
            // all hostnames are allowed
            return true;
        }

        try {
            final String actualHost = Uri.parse(url).getHost();
            for (String expectedHost : mPermittedHostnames) {
                // if the two hostnames match or if the actual host is a subdomain of the expected host
                if (actualHost.equals(expectedHost) || actualHost.endsWith("."+expectedHost)
                        || actualHost.contains(expectedHost)) {
                    // the actual hostname of the URL to be checked is allowed
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        // the actual hostname of the URL to be checked is not allowed since there were no matches
        return false;
    }

    /**
     * 设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为
     */
    protected void setMixedContentAllowed(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (null != webSettings) {
                webSettings.setMixedContentMode(allowed ? WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        : WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }
        }
    }

    /**
     * setAllowAccessFromFileUrls
     */
    protected static void setAllowAccessFromFileUrls(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(allowed);
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    /**
     * Get system default language local ISO
     */
    protected static String getLanguageIso3() {
        try {
            return Locale.getDefault().getISO3Language().toLowerCase(Locale.US);
        }
        catch (MissingResourceException e) {
            return LANGUAGE_DEFAULT_ISO3;
        }
    }

    /**
     * OpenFileInput
     */
    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst,
                                 final ValueCallback<Uri[]> fileUploadCallbackSecond,
                                 final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mUploadableFileTypes);

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment.get().startActivityForResult(Intent.createChooser(i,
                    getFileUploadPromptLabel()), mRequestCodeFilePicker);
        } else if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(Intent.createChooser(i,
                    getFileUploadPromptLabel()), mRequestCodeFilePicker);
        }
    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     *
     * @return the label for the file upload prompts as a string
     */
    protected String getFileUploadPromptLabel() {
        try {
            if (mLanguageIso3.equals("zho")) return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            else if (mLanguageIso3.equals("spa")) return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            else if (mLanguageIso3.equals("hin")) return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            else if (mLanguageIso3.equals("ben")) return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            else if (mLanguageIso3.equals("ara")) return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            else if (mLanguageIso3.equals("por")) return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            else if (mLanguageIso3.equals("rus")) return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            else if (mLanguageIso3.equals("jpn")) return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            else if (mLanguageIso3.equals("pan")) return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            else if (mLanguageIso3.equals("deu")) return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            else if (mLanguageIso3.equals("jav")) return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            else if (mLanguageIso3.equals("msa")) return decodeBase64("UGlsaWggc2F0dSBmYWls");
            else if (mLanguageIso3.equals("tel")) return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            else if (mLanguageIso3.equals("vie")) return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            else if (mLanguageIso3.equals("kor")) return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            else if (mLanguageIso3.equals("fra")) return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            else if (mLanguageIso3.equals("mar")) return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            else if (mLanguageIso3.equals("tam")) return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            else if (mLanguageIso3.equals("urd")) return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            else if (mLanguageIso3.equals("fas")) return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            else if (mLanguageIso3.equals("tur")) return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            else if (mLanguageIso3.equals("ita")) return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            else if (mLanguageIso3.equals("tha")) return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            else if (mLanguageIso3.equals("guj")) return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
        } catch (Exception ignored) { }

        // return English translation by default
        return "Choose a file";
    }

    protected static String decodeBase64(final String base64) throws IllegalArgumentException,
            UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, CHARSET_DEFAULT);
    }

    protected boolean hasError() {
        return (mLastError + 500) >= System.currentTimeMillis();
    }

    protected void setLastError() {
        mLastError = System.currentTimeMillis();
    }

    public void onPageError(int errorCode, String description, String failingUrl) {
        if (mWebListener != null) {
            mWebListener.onPageError(errorCode, description, failingUrl);
        }
    }

    public void releaseResource() {
        if (null != mHttpHeaders) {
            mHttpHeaders.clear();
        }

        if (null != mPermittedHostnames) {
            mPermittedHostnames.clear();
        }
    }

    /**
     * override WebviewClient callback shouldOverrideUrlLoading for JSBridgeWebView
     * */
    public boolean JSBridgeUrlLoading(final WebView view, String url) {
        return false;
    }

    /**
     * Wrapper for methods related to alternative browsers
     * */
    public static class Browsers {

        /**
         * Package name of an alternative browser
         */
        private static String mAlternativePackage;

        /**
         * Returns whether there is an alternative browser
         */
        public static boolean hasAlternative(final Context context) {
            return getAlternative(context) != null;
        }

        /**
         * Returns the package name of an alternative browser
         */
        public static String getAlternative(final Context context) {
            if (context == null) {
                return null;
            }

            if (mAlternativePackage != null) {
                return mAlternativePackage;
            }

            final List<String> alternativeBrowsers = Arrays.asList(ALTERNATIVE_BROWSERS);
            final List<ApplicationInfo> apps = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo app : apps) {
                if (!app.enabled) {
                    continue;
                }

                if (alternativeBrowsers.contains(app.packageName)) {
                    mAlternativePackage = app.packageName;

                    return app.packageName;
                }
            }

            return null;
        }

        /**
         * Opens the given URL in an alternative browser
         */
        public static void openUrl(final Activity context, final String url) {
            openUrl(context, url, false);
        }

        /**
         * Opens the given URL in an alternative browser
         */
        public static void openUrl(final Activity context, final String url, final boolean withoutTransition) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage(getAlternative(context));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            if (withoutTransition) {
                context.overridePendingTransition(0, 0);
            }
        }

    }
}
