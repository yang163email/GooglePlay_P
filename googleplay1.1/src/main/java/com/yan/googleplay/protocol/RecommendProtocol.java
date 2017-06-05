package com.yan.googleplay.protocol;

import com.yan.googleplay.base.BaseProtocol;

import java.util.List;

/**
 * Created by 楠GG on 2017/6/1.
 */

public class RecommendProtocol extends BaseProtocol<List<String>> {
    @Override
    protected String getInterface() {
        return "recommend";
    }
}
