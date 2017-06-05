package com.yan.googleplay.protocol;

import com.yan.googleplay.base.BaseProtocol;
import com.yan.googleplay.bean.HomeBean;

import java.util.List;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class GameProtocol extends BaseProtocol<List<HomeBean.AppItem>> {
    @Override
    protected String getInterface() {
        return "game";
    }
}
