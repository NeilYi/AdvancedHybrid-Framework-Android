package com.example.neilyi.advancedhybridframework.hybride.imp;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.example.neilyi.advancedhybridframework.hybride.BridgeCallback;
import com.example.neilyi.advancedhybridframework.hybride.HybridHandler;
import com.example.neilyi.advancedhybridframework.hybride.utils.HybridConstans;
import com.example.neilyi.web.view.AdvancedWebView;
import com.example.neilyi.advancedhybridframework.base.WebBaseActivity;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Url类型Hybrid处理类
 *
 * Created by NeilYi on 2017/7/24.
 */
public class UrlTypeHandler implements HybridHandler {
    @Override
    public String getHandlerType() {
        return HybridConstans.URL_TASK;
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
        return false;
    }

    @Override
    public boolean handerUrlTask(WebBaseActivity activity, String url) {
        if (null == activity || activity.isFinishing() || TextUtils.isEmpty(url)) {
            return false;
        }

        try {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.startsWith("smsto:")) {
                Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                it.putExtra("sms_body", "");
                activity.startActivity(it);
                return true;
            } else if (url.startsWith("mailto:")) {
                Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                activity.startActivity(it);
                return true;
            } else if (url.indexOf("sms:") != -1) {
                String strSMSTel = url.substring(url.indexOf("sms:") + 4);
                Uri uriSMSTel = Uri.parse("smsto:" + strSMSTel);
                Intent iSMS = new Intent(Intent.ACTION_SENDTO, uriSMSTel);
                activity.startActivity(iSMS);
                return true;
            } else if (url.startsWith("mqqwpa:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.startsWith("cmpay:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.startsWith("lingxi:") || url.startsWith("migulive:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.contains("weixin://wap/pay")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.startsWith("alipays://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } else if (url.startsWith("intent://")) {
                Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    // forbid launching activities without BROWSABLE category
                    intent.addCategory("android.intent.category.BROWSABLE");
                    // forbid explicit call
                    intent.setComponent(null);
                    // forbid intent with selector intent
                    intent.setSelector(null);
                    // start the activity by the intent
                    activity.startActivityIfNeeded(intent, -1);
                } catch (URISyntaxException exception) {
                    exception.printStackTrace();
                }
                return true;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return false;
    }
}
