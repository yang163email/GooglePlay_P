package com.yan.googleplay.base;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.MD5Utils;
import com.yan.googleplay.util.UiUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 楠GG on 2017/5/31.
 */

public abstract class BaseProtocol<T> {
    private static final String TAG = "BaseProtocol";
    private Map<String, String> mParams;

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    public T loadData() throws Exception {
        //读取缓存
        T t = getLocal();
        if(t != null) {
            Log.d(TAG, "loadData: 本地获取");
            return t;
        }
        Log.d(TAG, "loadData: 联网获取");

        return getServer();
    }

    /**联网获取数据*/
    private T getServer() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        String url = getUrl();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String jsonResult = body.string();
            T t = parseJson(jsonResult);
            //保存到缓存
            saveJson(new Gson().toJson(t));
            return t;
        }
        return null;
    }

    /**保存json数据*/
    private void saveJson(String jsonStr) {
        File cacheFile = getCacheFile();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(cacheFile));
            bw.write(jsonStr);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        Log.d(TAG, "saveJson: " + cacheFile.lastModified());
    }

    /**本地缓存获取数据*/
    private T getLocal() {
        //读取文件
        File jsonFile = getCacheFile();

        //判断上一次修改的时间与当前时间差值
        long lastModifiedTime = jsonFile.lastModified();
        long currentTime = System.currentTimeMillis();

        Log.d(TAG, "getLocal: 上次修改时间：" + lastModifiedTime);
        Log.d(TAG, "getLocal: 当前时间:" + currentTime);
        Log.d(TAG, "getLocal: 距上次修改过了:" + (currentTime - lastModifiedTime)/1000 + "秒");
        if(lastModifiedTime != 0) {
            if(currentTime - lastModifiedTime > 60 * 1000) {
                //距离上次修改的时间大于1min
                try {
                    Log.d(TAG, "getLocal: 重新联网获取数据");
                    return getServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //文件流读取
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonFile));
            String jsonStr = br.readLine();
            return parseJson(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        //返回
        return null;
    }

    /**获取缓存文件*/
    private File getCacheFile() {
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sdcard
            dir = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/com.yan.googleplay/gson");
        }else {
            //rom
            dir = new File(UiUtil.getContext().getCacheDir(), "gson");
        }
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File cacheFile = new File(dir, MD5Utils.encode(getUrl()));
        return cacheFile;
    }

    private T parseJson(String jsonResult) {
        //使用泛型解析，从子类获取
        Gson gson = new Gson();
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return gson.fromJson(jsonResult, typeArguments[0]);
    }

    private String getUrl() {
        String url = Constant.HOST + getInterface();
        StringBuffer sb = new StringBuffer(url);
        if(mParams != null && mParams.size() > 0) {
            sb.append("?");
            for(Map.Entry<String, String> param : mParams.entrySet()) {
                sb.append(param.getKey());
                sb.append("=");
                sb.append(param.getValue());
                sb.append("&");
            }
            url = sb.substring(0, sb.length() - 1);
        }
//        Log.d(TAG, "getUrl: " + url);
        return url;
    }

    protected abstract String getInterface();

}
