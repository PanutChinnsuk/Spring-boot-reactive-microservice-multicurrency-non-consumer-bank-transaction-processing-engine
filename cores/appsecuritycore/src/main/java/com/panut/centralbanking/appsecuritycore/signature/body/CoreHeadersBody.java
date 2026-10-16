package com.panut.centralbanking.appsecuritycore.signature.body;

import org.springframework.http.HttpHeaders;

public class CoreHeadersBody {
    private boolean hasHeader;
    private boolean userCallRequest;
    private String expectedHeader;

    public CoreHeadersBody(boolean hasHeader,
                           boolean userCallRequest,
                           String expectedHeader) {
        this.hasHeader = hasHeader;
        this.userCallRequest = userCallRequest;
        this.expectedHeader = expectedHeader;
    }


    public String getExpectedHeader() {
        return expectedHeader;
    }


    public boolean headerExists() {
        return hasHeader;
    }

    public boolean isUserCallRequest() {
        return userCallRequest;
    }
}
