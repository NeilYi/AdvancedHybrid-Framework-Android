package com.example.neilyi.advancedhybridframework.hybride.imp;

import android.text.TextUtils;

import com.example.neilyi.advancedhybridframework.hybride.Actions;
import com.example.neilyi.advancedhybridframework.hybride.BridgeCallback;
import com.example.neilyi.advancedhybridframework.hybride.HybridHandler;
import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;
import com.example.neilyi.advancedhybridframework.utils.ToastUtil;
import com.example.neilyi.web.view.AdvancedWebView;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;
import org.json.JSONObject;
import java.lang.ref.WeakReference;

/**
 * Fetch类型Hybrid处理类
 *
 * Created by liyi on 2017/7/24.
 */
public class FetchTypeJSHandler implements HybridHandler {

    private WeakReference<WebBaseActivity> mActivity;

    public FetchTypeJSHandler(WebBaseActivity activity) {
        if (activity != null && activity instanceof WebBaseActivity) {
            this.mActivity = new WeakReference<>(activity);
        }
    }

    @Override
    public String getHandlerType() {
        return HybridConstans.FETCH_TYPE_TASK;
    }

    @Override
    public AdvancedWebView getWebView() {
        return null;
    }

    @Override
    public boolean handerCallTask(WebBaseActivity activity, String actionStr, JSONObject jsonObject, AdvancedWebView webview) {
        return false;
    }

    @Override
    public boolean handerFetchTask(WebBaseActivity activity, String actionStr, JSONObject jsonObject, BridgeCallback callback) {
        if (TextUtils.isEmpty(actionStr)) {
            return false;
        }

        if (activity != null) {
            this.mActivity = new WeakReference<>(activity);
        }

        Actions action = null;
        try {
            action = Actions.values()[Actions.valueOf(actionStr).ordinal()];
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (action != null) {
            switch (action) {
                case getClientValue:
                    break;
                default:
                    ToastUtil.showLong("App没有处理事件的Action, action=" + actionStr);
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean handerUrlTask(WebBaseActivity activity, String string) {
        return false;
    }
}
