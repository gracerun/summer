package com.summer.log.core;

import lombok.Getter;

@Getter
public class RequestInfo {

    String ip;
    String scheme;

    public RequestInfo(String ip, String scheme) {
        super();
        this.ip = ip;
        this.scheme = scheme;
    }

}