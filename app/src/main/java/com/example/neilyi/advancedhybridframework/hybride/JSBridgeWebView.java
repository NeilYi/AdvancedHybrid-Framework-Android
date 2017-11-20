package com.example.neilyi.advancedhybridframework.hybride;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;
import com.example.neilyi.advancedhybridframework.hybride.BridgeCallback;
import com.example.neilyi.advancedhybridframework.hybride.HybridHandler;
import com.example.neilyi.advancedhybridframework.hybride.HybridHandlerManager;
import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;
import com.example.neilyi.advancedhybridframework.hybride.utils.JSBridgeUtil;
import com.example.neilyi.advancedhybridframework.utils.ToastUtil;
import com.example.neilyi.web.view.AdvancedWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * JSBridgeWebView（新架构JSBridge通信协议）
 *
 * Created by NeilYi on 2017/2/23.
 */
public abstract class JSBridgeWebView extends AdvancedWebView {
    private static final String TAG = "JSBridgeWebView";

    public JSBridgeWebView(Context context) {
        super(context);
        initView();
    }

    public JSBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * JS初始化.B+C相关webview的Setting设置
     */
    private void initView() {
        if(android.os.Build.VERSION.SDK_INT >= 11 && android.os.Build.VERSION.SDK_INT <= 18) {
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibilityTraversal");
        }
    }

    /**
     * B+C交互，前端通过IFrame传递url，唤起Native层方法
     */
    @Override
    public boolean JSBridgeUrlLoading(final WebView view, String url) {
        String schemUrl = url;
//        try {
//            schemUrl = URLDecoder.decode(url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            schemUrl = url;
//        }

        if (schemUrl.startsWith(JSBridgeUtil.METHOD_CALL)) { // 如果是call类型
            handleJsCallJavaMethod(schemUrl);
            return true;
        } else if (schemUrl.startsWith(JSBridgeUtil.METHOD_FETCH)) { // 如果是fetch类型
            handleJsFetchJavaMethod(schemUrl);
            return true;
        } else if (schemUrl.startsWith(JSBridgeUtil.LISTEN)) { // 如果是listen类型
            handleJsListenMethod(schemUrl);
            return true;
        } else if (schemUrl.startsWith(JSBridgeUtil.BROADCAST)) { // 如果是broadcast类型
            handleJsBroadcastMethod(schemUrl);
            return true;
        } else if (JSBridgeUtil.checkEnableUrlScheme(schemUrl)) { // 处理特殊Url Scheme协议类型
            handleUrlTypeMethod(schemUrl);
            return true;
        }

        return false;
    }

    /**
     * Call类型，前端直接调起Native层方法，不需要返回值
     */
    private void handleJsCallJavaMethod(String url) {
        if (!checkInvokeOnMainThread()) {
            return;
        }

        String methodName = JSBridgeUtil.getMethodNameFromUrl(url);
        if (TextUtils.isEmpty(methodName)) {
            return;
        }

        JSONObject jsonObj = null;
        String jsonData = JSBridgeUtil.getJsonDataFromeUrl(url);
        try {
            if(!TextUtils.isEmpty(jsonData)) {
                jsonObj = new JSONObject(jsonData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Context context = this.getContext();
        if (context != null && context instanceof WebBaseActivity) {
            WebBaseActivity act = (WebBaseActivity)context;
            HybridHandler hybridHandler = new HybridHandlerManager(act).createHybridHandler(HybridConstans.CALL_TYPE_TASK);
            if (hybridHandler != null) {
                hybridHandler.handerCallTask(act, methodName, jsonObj, this);
            } else {
                ToastUtil.showLong("App没有处理事件的--HybridHandler");
            }
        }
    }

    /**
     * Fetch类型，前端调Native层方法，并把处理结果值回传给前端
     */
    private void handleJsFetchJavaMethod(String url) {
        if (!checkInvokeOnMainThread()) {
            return;
        }

        String methodName = JSBridgeUtil.getMethodNameFromUrl(url);
        if (TextUtils.isEmpty(methodName)) {
            return;
        }

        String jsonData = JSBridgeUtil.getJsonDataFromeUrl(url);
        String retMethod = JSBridgeUtil.getParamStringFromUrl(url, "retMethod");

        BridgeCallback callBack = null;
        if (!TextUtils.isEmpty(retMethod)) {
            callBack = new BridgeCallback(this, retMethod);
        }

        JSONObject jsonObj = null;
        try {
            if(!TextUtils.isEmpty(jsonData)) {
                jsonObj = new JSONObject(jsonData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Context context = this.getContext();
        if (context != null && context instanceof WebBaseActivity) {
            WebBaseActivity act = (WebBaseActivity)context;
            HybridHandler hybridHandler = new HybridHandlerManager(act).createHybridHandler(HybridConstans.FETCH_TYPE_TASK);
            if (hybridHandler != null) {
                hybridHandler.handerFetchTask(act, methodName, jsonObj, callBack);
            } else {
                ToastUtil.showLong("App没有处理事件的--HybridHandler");
            }
        }
    }

    /**
     * UrlType，处理特殊Scheme的地址，不需要返回值
     */
    private void handleUrlTypeMethod(String url) {
        if (!checkInvokeOnMainThread()) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Context context = this.getContext();
        if (context != null && context instanceof WebBaseActivity) {
            WebBaseActivity act = (WebBaseActivity)context;
            HybridHandler hybridHandler = new HybridHandlerManager(act).createHybridHandler(HybridConstans.URL_TASK);
            if (hybridHandler != null) {
                hybridHandler.handerUrlTask(act, url);
            } else {
                ToastUtil.showLong("App没有处理事件的--HybridHandler");
            }
        }
    }

    /**
     * Listen，客户端根据前端传递的url中的key值存储相应jsonData数据
     *
     * cleanType含义：
     * 0或空:追加（按照加入顺序加入）
     * 1:清空原有的数据后，加入
     * 2:只清空，不加入
     */
    private void handleJsListenMethod(String url) {
        String jsonData = JSBridgeUtil.getJsonDataFromeUrl(url);
        String channelId = JSBridgeUtil.getParamStringFromUrl(url, "key");
        String cleanType = JSBridgeUtil.getParamStringFromUrl(url, "cleanType");
        Log.i(TAG, "handleJsListenMethod jsonData=" + jsonData
                + "\tkey=" + channelId + "\tcleanType=" + cleanType);

        if (TextUtils.isEmpty(cleanType) || "0".equals(cleanType)) { //追加（按照加入顺序加入）
            JSBridgeUtil.saveBridgeData(channelId, jsonData);
        } else if ("1".equals(cleanType)) { //清空原有的数据后，加入
            JSBridgeUtil.deleteBridgeData(channelId);
            JSBridgeUtil.saveBridgeData(channelId, jsonData);
        } else if ("2".equals(cleanType)) { //只清空，不加入
            JSBridgeUtil.deleteBridgeData(channelId);
        }
    }

    /**
     * Broadcast，客户端将key值对应的存储的jsonData数据传递给前端
     */
    private void handleJsBroadcastMethod(String url) {
        String channelId = JSBridgeUtil.getParamStringFromUrl(url, "key");
        String retMethod = JSBridgeUtil.getParamStringFromUrl(url, "retMethod");
        String cleanType = JSBridgeUtil.getParamStringFromUrl(url, "cleanType");
        Log.i(TAG, "handleJsBroadcastMethod retMethod=" + retMethod
                + "\tkey=" + channelId + "\tcleanType=" + cleanType);

        ArrayList<Object> mPassData = JSBridgeUtil.getBridgeData(channelId, cleanType);
        if (!TextUtils.isEmpty(retMethod)) {
            BridgeCallback callBack = new BridgeCallback(this, retMethod);
            callBack.sendBroadCastData(mPassData);
        }
    }

    /**
     * 检查是否在主UI线程调用
     */
    private boolean checkInvokeOnMainThread() {
        boolean isOnMainThread = true;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            isOnMainThread = false;
        }
        return isOnMainThread;
    }
}
