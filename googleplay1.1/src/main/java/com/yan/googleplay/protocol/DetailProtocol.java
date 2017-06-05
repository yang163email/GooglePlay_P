package com.yan.googleplay.protocol;

import com.yan.googleplay.base.BaseProtocol;
import com.yan.googleplay.bean.DetailBean;

/**
 * Created by 楠GG on 2017/6/2.
 */

public class DetailProtocol extends BaseProtocol<DetailBean> {
    @Override
    protected String getInterface() {
        return "detail";
    }
}
