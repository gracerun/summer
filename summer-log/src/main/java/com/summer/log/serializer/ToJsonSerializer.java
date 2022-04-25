package com.summer.log.serializer;

import com.alibaba.fastjson.JSON;

public class ToJsonSerializer implements LogSerializer {

    @Override
    public String write(Object[] args) {
        return JSON.toJSONString(args);
    }

    @Override
    public String write(Object retVal) {
        return JSON.toJSONString(retVal);
    }
}
