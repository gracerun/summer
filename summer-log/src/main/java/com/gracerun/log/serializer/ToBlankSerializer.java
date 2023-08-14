package com.gracerun.log.serializer;

public class ToBlankSerializer implements LogSerializer {

    @Override
    public String write(Object[] args) {
        return "";
    }

    @Override
    public String write(Object retVal) {
        return "";
    }
}
