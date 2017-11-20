package com.example.neilyi.advancedhybridframework.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.neilyi.advancedhybridframework.hybride.HybridHandler;

import java.util.HashMap;

/**
 * 所有的网页都继承该Activity
 *
 * Created by NeilYi on 2017/7/24.
 */
public class WebBaseActivity extends AppCompatActivity {
    private static final String TAG = "WebBaseActivity";

    /**
     * 维护整个webview交互处理事件的HybridHandler对象；
     */
    private HashMap<String, HybridHandler> mHybridHandlerHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHybridHandlerHashMap != null) {
            mHybridHandlerHashMap.clear();
            mHybridHandlerHashMap = null;
        }
    }

    public HashMap<String, HybridHandler> getHybridHandlerMap() {
        if(mHybridHandlerHashMap == null){
            mHybridHandlerHashMap = new HashMap<>();
        }
        return mHybridHandlerHashMap;
    }

    /**
     * 将 HybridHandler 添加到这个集合
     *
     * @param evenName
     * @param mHybridHandler
     */
    public void addHybridHandler(String evenName, HybridHandler mHybridHandler) {
        if (mHybridHandlerHashMap != null && mHybridHandler != null && !TextUtils.isEmpty(evenName)) {
            mHybridHandlerHashMap.put(evenName, mHybridHandler);
        }
    }
}