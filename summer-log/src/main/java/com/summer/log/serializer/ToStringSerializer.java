package com.summer.log.serializer;

import java.util.Arrays;

public class ToStringSerializer implements LogSerializer {

    public static final ToStringSerializer instance = new ToStringSerializer();

    @Override
    public String write(Object[] args) {
        return Arrays.toString(args);
    }

    @Override
    public String write(Object retVal) {
        return String.valueOf(retVal);
    }
}
