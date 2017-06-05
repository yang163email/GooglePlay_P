package com.yan.googleplay.protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yan.googleplay.bean.HomeBean;
import com.yan.googleplay.util.Constant;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class AppProtocol {
    public List<HomeBean.AppItem> loadData(String startIndex) throws Exception {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(Constant.APP + startIndex).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String jsonResult = body.string();
            Gson gson = new Gson();
            return gson.fromJson(jsonResult, new TypeToken<List<HomeBean.AppItem>>(){}.getType());
        }
        return null;
    }
}
