package com.example.neilyi.advancedhybridframework.hybride;

import android.app.Activity;

import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;
import com.example.neilyi.advancedhybridframework.hybride.imp.CallTypeJSHandler;
import com.example.neilyi.advancedhybridframework.hybride.imp.FetchTypeJSHandler;
import com.example.neilyi.advancedhybridframework.hybride.imp.UrlTypeHandler;
import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;

import java.lang.ref.WeakReference;

/**
 * HybridHandler 对象工厂类
 *
 * Created by NeilYi on 2017/7/24.
 */
public class HybridHandlerManager {
    private WeakReference<Activity> mActivity;

    public HybridHandlerManager(Activity activity) {
        if (activity != null) {
            this.mActivity = new WeakReference<>(activity);
        }
    }

    public HybridHandler createHybridHandler(String handlerType) {
        // 先从 集合中取 如果没有去创建对象
        if (this.mActivity == null || this.mActivity.get() == null) {
            return null;
        }

        if (this.mActivity.get() instanceof WebBaseActivity) {
            WebBaseActivity activity = (WebBaseActivity) this.mActivity.get();
            HybridHandler mHybridHandler = activity.getHybridHandlerMap().get(handlerType);
            if (mHybridHandler != null) {
                return mHybridHandler;
            }

            //创建 Call消息处理对象
            if (handlerType.equals(HybridConstans.CALL_TYPE_TASK)) {
                CallTypeJSHandler callHandler = new CallTypeJSHandler(activity);
                activity.addHybridHandler(callHandler.getHandlerType(), callHandler);
                return callHandler;
            }

            //创建 Fetch消息处理对象
            if (handlerType.equals(HybridConstans.FETCH_TYPE_TASK)) {
                FetchTypeJSHandler fetchHandler = new FetchTypeJSHandler(activity);
                activity.addHybridHandler(fetchHandler.getHandlerType(), fetchHandler);
                return fetchHandler;
            }

            //创建 url处理对象
            if (handlerType.equals(HybridConstans.URL_TASK)) {
                UrlTypeHandler urlHnadler = new UrlTypeHandler();
                activity.addHybridHandler(urlHnadler.getHandlerType(), urlHnadler);
                return urlHnadler;
            }
        }

        return null;
    }
}
