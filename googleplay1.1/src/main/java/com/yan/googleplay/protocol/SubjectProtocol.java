package com.yan.googleplay.protocol;

import com.yan.googleplay.base.BaseProtocol;
import com.yan.googleplay.bean.SubjectBean;

import java.util.List;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class SubjectProtocol extends BaseProtocol<List<SubjectBean>> {
    @Override
    protected String getInterface() {
        return "subject";
    }
}
