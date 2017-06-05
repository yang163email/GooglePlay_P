package com.yan.googleplay.protocol;

import com.yan.googleplay.base.BaseProtocol;
import com.yan.googleplay.bean.CategoryVo;

import java.util.List;

/**
 * Created by 楠GG on 2017/6/1.
 */

public class CategoryProtocol extends BaseProtocol<List<CategoryVo>> {
    @Override
    protected String getInterface() {
        return "category";
    }
}
