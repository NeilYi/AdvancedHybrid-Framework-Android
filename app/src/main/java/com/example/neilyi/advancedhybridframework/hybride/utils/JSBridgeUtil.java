package com.example.neilyi.advancedhybridframework.hybride.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * JSBridge工具类
 *
 * Created by NeilYi on 2017/2/24.
 */
public class JSBridgeUtil {
    public final static String OVERRIDE_SCHEMA = "cmread://";
    public final static String METHOD_CALL = OVERRIDE_SCHEMA + "call/";
    public final static String METHOD_FETCH = OVERRIDE_SCHEMA + "fetch/";
    public final static String LISTEN = OVERRIDE_SCHEMA + "listen/channel";
    public final static String BROADCAST = OVERRIDE_SCHEMA + "broadcast/channel";

    private final static String EMPTY_STR = "";
    private final static String SPLIT_MARK = "/";

    public static HashMap<String, ArrayList<Object>> mBridgeDataMap = new HashMap();

    /**
     * 获取URL里面的方法名
     */
    public static String getMethodNameFromUrl(final String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                Uri uri = Uri.parse(url);
                String path = uri.getPath();
                if (!TextUtils.isEmpty(path)) {
                    String[] splitMethodName = path.split(SPLIT_MARK);
                    if (splitMethodName != null && splitMethodName.length > 1) {
                        return splitMethodName[1];
                    }
                }
            } catch (Exception e) {
                String temp = url.replace(OVERRIDE_SCHEMA, EMPTY_STR);
                String[] spitStr = temp.split(SPLIT_MARK);
                if (spitStr != null && spitStr.length > 1) {
                    String methodNameTemp = spitStr[1];
                    if (!TextUtils.isEmpty(methodNameTemp)) {
                        if (methodNameTemp.contains("?")) {
                            String methodName = temp.substring(0, temp.indexOf("?"));
                            return methodName;
                        } else {
                            return methodNameTemp;
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 获取URL里面的数据参数
     */
    public static String getJsonDataFromeUrl(final String url) {
        String returnString = "";

        if (TextUtils.isEmpty(url)) {
            return returnString;
        }

        if (url.contains("jsonData=")) {
            String jsonData;
            int startIndex = url.indexOf("jsonData=");
            int endIndex = url.indexOf("&", startIndex);

            if (endIndex == -1) {
                jsonData = url.substring(startIndex + "jsonData=".length());
            } else {
                jsonData = url.substring(startIndex + "jsonData=".length(), endIndex);
            }

            if (!TextUtils.isEmpty(jsonData)) {
                returnString = jsonData.replace("+", "-").replace("/", "_");
            }
        }

        //Decode jsonData string with UrlBase64
        String decodeStr;
        try {
            byte[] byteData = Base64.decode(returnString, Base64.URL_SAFE);
            decodeStr = new String(byteData, "UTF-8");
        } catch (Exception | Error e) {
            decodeStr = returnString;
        }

        return decodeStr;
    }

    /**
     * 获取URL里面的param参数
     */
    public static String getParamStringFromUrl(final String url, final String paramName) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(paramName)) {
            try {
                Uri uri = Uri.parse(url);
                String paramString = uri.getQueryParameter(paramName);
                if (!TextUtils.isEmpty(paramString)) {
                    return paramString;
                }
            } catch (Exception e) {
                String paramKey = paramName + "=";
                if (url.contains(paramKey)) {
                    String retMethod;
                    int startIndex = url.indexOf(paramKey);
                    int endIndex = url.indexOf("&", startIndex);

                    if (endIndex == -1) {
                        retMethod = url.substring(startIndex + paramKey.length());
                    } else {
                        retMethod = url.substring(startIndex + paramKey.length(), endIndex);
                    }

                    if (!TextUtils.isEmpty(retMethod)) {
                        return retMethod;
                    }
                }
            }
        }
        return "";
    }

    /**
     * 存储前端Listen类型参数
     */
    public static void saveBridgeData(final String key, final Object data) {
        if (mBridgeDataMap == null) {
            mBridgeDataMap = new HashMap<String, ArrayList<Object>>();
        }

        if (TextUtils.isEmpty(key) || data == null) {
            return;
        }

        ArrayList<Object> dataList = mBridgeDataMap.get(key);
        if (dataList == null) {
            dataList = new ArrayList<Object>();
            dataList.add(data);
        } else {
            dataList.add(data);
        }

        mBridgeDataMap.put(key, dataList);
    }

    /**
     * 获取前端让Native层存储的参数
     *
     * @param cleanType 1:获取数据后清空数据 0或空:获取数据后不清空
     */
    public static ArrayList<Object> getBridgeData(final String key, final String cleanType) {
        if (mBridgeDataMap == null) {
            mBridgeDataMap = new HashMap<String, ArrayList<Object>>();
        }

        if (TextUtils.isEmpty(key)) {
            return null;
        }

        ArrayList<Object> dataList = mBridgeDataMap.get(key);
        if (dataList != null && dataList.size() > 0) {
            if ("1".equals(cleanType)) {
                deleteBridgeData(key);
            }
        }

        return dataList;
    }

    /**
     * 删除前端让Native层存储的参数
     */
    public static void deleteBridgeData(final String key) {
        if (mBridgeDataMap == null) {
            mBridgeDataMap = new HashMap<String, ArrayList<Object>>();
        }

        if (TextUtils.isEmpty(key)) {
            return;
        }

        try {
            Iterator iterator = mBridgeDataMap.keySet().iterator();
            while (iterator.hasNext()) {
                String keyValue = (String) iterator.next();
                if (key.equals(keyValue)) {
                    iterator.remove();        //添加该行代码
                    mBridgeDataMap.remove(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测 URL SCHEME 地址头是否可处理
     */
    public static boolean checkEnableUrlScheme(final String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        boolean enable = false;
        if (url.startsWith("tel:")
                || url.startsWith("smsto:")
                || url.startsWith("cmpay:")
                || url.startsWith("mqqwpa:")
                || url.startsWith("mailto:")
                || url.startsWith("lingxi:")
                || url.startsWith("migulive:")
                || url.startsWith("intent://")
                || url.startsWith("alipays://")
                || url.contains("weixin://wap/pay")
                || url.indexOf("sms:") != -1) {
            enable = true;
        }
        return enable;
    }
}
