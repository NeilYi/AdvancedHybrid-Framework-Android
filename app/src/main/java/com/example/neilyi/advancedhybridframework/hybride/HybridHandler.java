package com.example.neilyi.advancedhybridframework.hybride;

import com.example.neilyi.web.view.AdvancedWebView;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;

import org.json.JSONObject;

/**
 * 处理Call类型交互事件的接口类
 *
 * Created by NeilYi on 2017/7/24.
 */
public interface HybridHandler {
    /**
     * 处理事件对象的名称
     * @return
     */
    String getHandlerType();

    /**
     * 获取WebView对象
     * @return
     */
    AdvancedWebView getWebView();

    /**
     * 对应Call类型的实现类 处理对应的事件任务 返回true 带表处理了， false 则是没有处理
     */
    boolean handerCallTask(WebBaseActivity activity, String actionStr, JSONObject jsonObject, AdvancedWebView webview);

    /**
     * 对应Fetch类型的实现类 处理对应的事件任务 返回true 带表处理了， false 则是没有处理
     */
    boolean handerFetchTask(WebBaseActivity activity, String actionStr, JSONObject jsonObject, BridgeCallback callback);

    /**
     * 对应Url类型的实现类 处理对应的事件任务  返回true 带表处理了， false 则是没有处理
     * @param string
     * @return
     */
    boolean handerUrlTask(WebBaseActivity activity, String string);
}
