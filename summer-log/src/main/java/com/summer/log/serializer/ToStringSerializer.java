package com.summer.log.serializer;

import java.util.Arrays;

public class ToStringSerializer implements LogSerializer {

    @Override
    public String write(Object[] args) {
        return Arrays.toString(args);
    }

    @Override
    public String write(Object retVal) {
        return String.valueOf(retVal);
    }
}
