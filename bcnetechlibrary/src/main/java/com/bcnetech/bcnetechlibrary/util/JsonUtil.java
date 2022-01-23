package com.bcnetech.bcnetechlibrary.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wenbin on 16/6/7.
 */
public class JsonUtil {
    public static String getString(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getString(name) : "";
    }

    public static int getInt(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getInt(name) : 0;
    }

    public static boolean getBoolean(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getBoolean(name) : false;
    }

    public static double getDouble(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getDouble(name) : 0;
    }

    public static long getLang(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getLong(name) : 0;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getJSONObject(name) : null;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String name)
            throws Exception {
        return jsonObject.has(name) ? jsonObject.getJSONArray(name) : null;
    }
    /**
     * 将列表转换成为json，使用序列化
     * @param l
     * @return
     */
    public static String list2JsonSerial(List l){
        if(null==l){
            return null;
        }
        try {
            return JSON.toJSONString(l);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static  com.alibaba.fastjson.JSONArray string2JsonArray(String json){
        //将JSON文本转换为JSONArray
        return JSON.parseArray(json);
    }
    /**
     * 将json转换成为java数组
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> List<T> Json2List(String json,Class<T> classOfT){
        if(null==json){
            return null;
        }
        try {
            return JSON.parseArray(json, classOfT);
        } catch (Exception e) {
            Log.e("JsonUtil.Json2T", "msg:" + json + "\r\nclazz:" + classOfT.getName());
            return null;
        }
    }
    /**
     * 将json转换成为java对象
     * @param str
     * @param classOfT
     * @return
     */
    public static <T> T Json2T(String str,Class<T> classOfT){
        if(null== str) {
            return null;
        }
        try {
            return JSON.parseObject(str, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JsonUtil.Json2T", "msg:" + str + "\nclazz:" + classOfT.getName());
            return null;
        }
    }
    /**
     * 将非集合类java对象转换成为json object
     * @param o
     * @return
     */
    public static JSONObject Object2JsonObject(Object o){
        try {
            return (JSONObject) JSON.toJSON(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将对象o转换成json格式数据
     * @param o
     * @return
     */
    public static String Object2Json(Object o){
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将全序列化json转换成为java对象
     * @param json
     * @return
     */
    public static Object JsonSerial2Object(String json){
        try {
            return JSON.parse(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static com.alibaba.fastjson.JSONObject json2JsonObject(String json){
        try {
            return (com.alibaba.fastjson.JSONObject) JSON.parseObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> getMapForJson(String jsonStr){
        JSONObject jsonObject ;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIter= jsonObject.keys();
            String key;
            Object value ;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }
}
