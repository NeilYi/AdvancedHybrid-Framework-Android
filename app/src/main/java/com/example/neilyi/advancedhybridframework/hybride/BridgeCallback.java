package com.example.neilyi.advancedhybridframework.hybride;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;
import com.example.neilyi.web.view.AdvancedWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Native调用B页面方法回调
 *
 * Created by NeilYi on 2017/2/27.
 */
public class BridgeCallback {
    private static final String TAG = "BridgeCallback";
    private static final String CALLBACK_JS_FORMAT = "javascript:b2cFun.%s(%s)";
    private static final String CALLBACK_BROADCAST_FORMAT = "javascript:b2cFun.%s(%s);";

    private String mRetMethodName;
    private WeakReference<AdvancedWebView> mWebViewRef;

    public BridgeCallback (AdvancedWebView view, String retMethodName) {
        mRetMethodName = retMethodName;
        mWebViewRef = new WeakReference<>(view);
    }

    /**
     * Native调用B页面方法
     *
     * @param resultObj
     */
    public void sendJsData(Object resultObj) {
        if (resultObj != null) {
            sendJsData(HybridConstans.BRIDGE_CALLBACK_SUCCESS, "数据获取成功", resultObj);
        } else {
            sendJsData(HybridConstans.BRIDGE_CALLBACK_FAIL, "数据获取失败", resultObj);
        }
    }

    /**
     * Native调用B页面方法
     *
     * @param code
     * @param msg
     * @param resultObj
     */
    public void sendJsData(String code, String msg, Object resultObj) {
        LinkedHashMap<String, Object> resultMessage = new LinkedHashMap<>();
        resultMessage.put("code", code);
        resultMessage.put("message", msg);

        if (resultObj != null && resultObj instanceof String) {
            String resultString = (String)resultObj;
            if (!TextUtils.isEmpty(resultString) && !"null".equalsIgnoreCase(resultString)) {
                //result不为空，先判断字符串是否为JSONObject或JSONArray类型
                try {
                    JSONObject obj = new JSONObject(resultString);
                    resultMessage.put("result", obj);
                } catch (JSONException e1) {
                    try {
                        JSONArray obj = new JSONArray(resultString);
                        resultMessage.put("result", obj);
                    } catch (JSONException e2) {
                        resultMessage.put("result", resultString);
                    }
                }
            } else {
                resultMessage.put("result", null);
            }
        } else { // result为非字符串类型
            if (resultObj != null) {
                resultMessage.put("result", resultObj);
            } else {
                resultMessage.put("result", null);
            }
        }

        JSONObject jsonObj = null;
        try {
            jsonObj= new JSONObject(resultMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendJsData(jsonObj);
    }

    /**
     * Native调用B页面方法
     *
     * @param jsonObj the jsonObj data
     * {
     * "code":"0",
     * "message":"msg",
     * "result":object
     * }
     */
    private void sendJsData(JSONObject jsonObj) {
        if (jsonObj == null) {
            return;
        }

        if (mWebViewRef == null || mWebViewRef.get() == null) {
            return;
        }

        String execJs = String.format(Locale.ENGLISH, CALLBACK_JS_FORMAT, mRetMethodName, jsonObj.toString());
        if (checkInvokeOnMainThread()) {
            mWebViewRef.get().loadJavaScript(execJs);
        } else if (mJSCallMainThreadHandler != null) {
            Message msgInvoke = Message.obtain();
            msgInvoke.obj = execJs;
            mJSCallMainThreadHandler.sendMessage(msgInvoke);
        }
    }

    /**
     * Native调用B页面BroadCast方法
     */
    public void sendBroadCastData(ArrayList<Object> resultList) {
        if (mWebViewRef == null || mWebViewRef.get() == null) {
            return;
        }

        String broadcastData;
        JSONArray jsonArray = new JSONArray();
        if (resultList != null && resultList.size() > 0) {
            int count = resultList.size();
            for (int i = 0; i < count; i++) {
                Object object = resultList.get(i);
                if (object != null && object instanceof String) {
                    try {
                        JSONObject obj = new JSONObject((String)object);
                        jsonArray.put(obj);
                    } catch (JSONException e1) {
                        try {
                            JSONArray obj = new JSONArray((String)object);
                            jsonArray.put(obj);
                        } catch (JSONException e2) {
                            jsonArray.put(object);
                        }
                    }
                } else {
                    jsonArray.put(resultList.get(i));
                }
            }
        }
        broadcastData = jsonArray.toString();

        String execJs = String.format(Locale.ENGLISH, CALLBACK_BROADCAST_FORMAT, mRetMethodName, broadcastData);
        if (checkInvokeOnMainThread()) {
            mWebViewRef.get().loadJavaScript(execJs);
        } else if (mJSCallMainThreadHandler != null) {
            Message msgInvoke = Message.obtain();
            msgInvoke.obj = execJs;
            mJSCallMainThreadHandler.sendMessage(msgInvoke);
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

    private Handler mJSCallMainThreadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String execJs = "";
            if (msg != null && msg.obj != null) {
                execJs = msg.obj.toString();
            }

            if (!TextUtils.isEmpty(execJs) && mWebViewRef !=null && mWebViewRef.get() != null) {
                mWebViewRef.get().loadJavaScript(execJs);
            }
        }
    };
}
