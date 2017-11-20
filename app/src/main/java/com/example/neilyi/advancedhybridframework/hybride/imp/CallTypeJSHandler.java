package com.example.neilyi.advancedhybridframework.hybride.imp;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.neilyi.advancedhybridframework.hybride.Actions;
import com.example.neilyi.advancedhybridframework.hybride.BridgeCallback;
import com.example.neilyi.advancedhybridframework.hybride.HybridHandler;
import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;
import com.example.neilyi.advancedhybridframework.utils.ToastUtil;
import com.example.neilyi.web.view.AdvancedWebView;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Call类型Hybrid处理类
 *
 * Created by liyi on 2017/7/24.
 */
public class CallTypeJSHandler implements HybridHandler {

    private WeakReference<AdvancedWebView> mWebView;
    private WeakReference<WebBaseActivity> mActivity;

    public CallTypeJSHandler(WebBaseActivity activity) {
        if (activity != null && activity instanceof WebBaseActivity) {
            this.mActivity = new WeakReference<>(activity);
        }
    }

    @Override
    public String getHandlerType() {
        return HybridConstans.CALL_TYPE_TASK;
    }

    @Override
    public AdvancedWebView getWebView() {
        if(mWebView != null && mWebView.get() != null) {
            return mWebView.get();
        }
        return null;
    }

    @Override
    public boolean handerCallTask(WebBaseActivity activity, String actionStr, JSONObject jsonData, AdvancedWebView webview) {
        if (TextUtils.isEmpty(actionStr)) {
            return false;
        }

        if (activity != null) {
            this.mActivity = new WeakReference<>(activity);
        }

        if (webview != null) {
            this.mWebView = new WeakReference<>(webview);
        }

        Actions action = null;
        try {
            action = Actions.values()[Actions.valueOf(actionStr).ordinal()];
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (action != null) {
            switch (action) {
                case notifyResultToast:
                    notifyResultToast(jsonData);
                    break;
                default:
                    ToastUtil.showLong("App没有处理事件的Action, action=" + actionStr);
                    break;
            }
            return true;
        }

        return false;
    }

    private void notifyResultToast(JSONObject jsonData) {
        String info = "notifyResultToast!";
        if (jsonData != null) {
            try {
                info = jsonData.getString("responseInfo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ToastUtil.showLong(info);
    }

    private WebBaseActivity getActivityContext() {
        if (mActivity != null && mActivity.get() != null) {
            return mActivity.get();
        }
        return null;
    }

    @Override
    public boolean handerFetchTask(WebBaseActivity activity, String actionStr, JSONObject jsonObject, BridgeCallback callback) {
        return false;
    }

    @Override
    public boolean handerUrlTask(WebBaseActivity activity, String string) {
        return false;
    }
}
