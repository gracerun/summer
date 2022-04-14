package com.summer.log.core;

import lombok.Getter;

@Getter
public class RequestInfo {

    String remoteHost;
    String requestType;

    public RequestInfo(String remoteHost, String requestType) {
        super();
        this.remoteHost = remoteHost;
        this.requestType = requestType;
    }

}