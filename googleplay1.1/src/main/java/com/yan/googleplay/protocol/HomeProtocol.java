package com.yan.googleplay.protocol;

import com.google.gson.Gson;
import com.yan.googleplay.bean.HomeBean;
import com.yan.googleplay.util.Constant;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class HomeProtocol {

    public HomeBean loadData(String startIndex) throws Exception {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(Constant.HOME + startIndex).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String jsonResult = body.string();
            Gson gson = new Gson();
            return gson.fromJson(jsonResult, HomeBean.class);
        }
        return null;
    }
}
