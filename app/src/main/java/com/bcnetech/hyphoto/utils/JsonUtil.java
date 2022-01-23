package com.bcnetech.hyphoto.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bcnetech.bcnetechlibrary.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by wb on 2016/5/3.
 */
public class JsonUtil {
    private static final String CHARSET = "UTF-8";

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

    public static JSONObject json2JsonObject(String json){
        try {
            return (JSONObject) JSON.parseObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject bytes2JsonObject(byte[] bytes){
        try {
            return (JSONObject) JSON.parseObject(new String(bytes,CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T bytes2T(byte[] bytes,Class<T> classOfT){
        try {
            return JSON.parseObject(new String(bytes,CHARSET),classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将集合类java对象装换成为json array
     * @param collection
     * @return
     */
    public static JSONArray Collection2JsonArray(Collection collection){
        try {
            return (JSONArray) JSON.toJSON(collection);
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
     * 更加方便观察，但会增加存储空�?
     * @param o
     * @return
     */
    public static String Object2JsonPrettyFormat(Object o){
        try {
            return JSON.toJSONString(o, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象转换成json格式数据，使用全序列化方�?
     * @param o
     * @return
     */
    public static String Object2JsonSerial(Object o){
        try {
            return JSON.toJSONString(o, SerializerFeature.WriteClassName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将列表转换成为json，使用全序列化方�?
     * @param l
     * @return
     */
    public static String list2JsonSerial(List l){
        if(null==l){
            return null;
        }
        try {
            return JSON.toJSONString(l,SerializerFeature.WriteClassName);
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

    /**
     * 将全序列化json转换成为list对象
     * @param json
     * @return
     */
    public static <T> List JsonSerial2List(String json){
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将全序列化json转换为jsonArray对象
     * @param json
     * @return
     */
    public static JSONArray JsonSerial2JsonArray(String json){
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json转换成为jsonArray对象
     * @param str
     * @return
     */
    public static JSONArray Json2JsonArray(String str){
        if(StringUtil.isBlank(str)){
            return null;
        }
        try {
            return JSON.parseArray(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json转换成为java对象
     * @param str
     * @param classOfT
     * @return
     */
    public static <T> T Json2T(String str, Class<T> classOfT){
        if(null==str){
            return null;
        }
        try {
            return JSON.parseObject(str,classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JsonUtil.Json2T", "msg:"+str+"\nclazz:"+classOfT.getName());
            return null;
        }
    }

    /**
     * 将json转换成为java数组
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> List<T> Json2List(String json, Class<T> classOfT){
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
    /***
     * �?JSONObject 对象转换�?class 对象
     * @param json
     * @param pojo
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "deprecation", "unused", "unchecked" })
    public  static <T> T fromJsonToJava(JSONObject json,Class<T> pojo) throws Exception {
        // 首先得到pojo�?��义的字段
        Field[] fields = pojo.getDeclaredFields();
        // 根据传入的Class动�?生成pojo对象
        T obj = pojo.newInstance();
        for(Field field: fields){
            // 设置字段可访问（必须，否则报错）
            field.setAccessible(true);
            // 得到字段的属性名
            String name = field.getName();
            // 这一段的作用是如果字段在JSONObject中不存在会抛出异常，如果出异常，则跳过�?
            try{
                json.get(name);
            }catch(Exception ex){
                continue;
            }
            if(json.get(name) != null && !"".equals(json.getString(name))){
                // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中�?
                if(field.getType().equals(Long.class) || field.getType().equals(long.class)){
                    field.set(obj, Long.parseLong(json.getString(name)));
                }else if(field.getType().equals(String.class)){
                    field.set(obj, json.getString(name));
                } else if(field.getType().equals(Double.class) || field.getType().equals(double.class)){
                    field.set(obj, Double.parseDouble(json.getString(name)));
                } else if(field.getType().equals(Integer.class) || field.getType().equals(int.class)){
                    field.set(obj, Integer.parseInt(json.getString(name)));
                } else if(field.getType().equals(java.util.Date.class)){
                    field.set(obj, Date.parse(json.getString(name)));
                }else if(field.getType().equals(java.util.List.class)){
//                	System.out.println("==list=="+json.getString(name));
                    field.set(obj, Json2T(json.getString(name), List.class));
                }else if(field.getType().equals(Byte.class) || field.getType().equals(Byte.class)){
                    field.set(obj, Byte.parseByte(json.getString(name)));
                }else if(field.getType().equals(byte.class) || field.getType().equals(byte.class)){
                    field.set(obj, Byte.parseByte(json.getString(name)));
                }

                else{
                    continue;
                }
            }
        }
        return obj;
    }
}
