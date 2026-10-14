package com.panut.centralbanking.appcore.common.response;

public class CommonResult<T> {
    private T optionalBody;
    private boolean success;
    private String optionalMessage = "NOT DEFINED";


    public CommonResult(boolean success) {
        this.success = success;
    }

    public CommonResult(boolean success, String optionalMessage) {
        this.success = success;
        this.optionalMessage = optionalMessage;
    }

    public CommonResult(String optionalMessage, T optionalBody) {
        this.success = true;
        this.optionalMessage = optionalMessage;
        this.optionalBody = optionalBody;
    }

    public static CommonResult defaultSuccess() {
        return new CommonResult(true);
    }

    public static CommonResult defaultSuccess(String message) {
        return new CommonResult(true, message);
    }



    public static CommonResult defaultFailed(String message) {
        return new CommonResult(false, message);
    }

    



    public String getOptionalMessage() {
        return optionalMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getOptionalBody() {
        return optionalBody;
    }
}
