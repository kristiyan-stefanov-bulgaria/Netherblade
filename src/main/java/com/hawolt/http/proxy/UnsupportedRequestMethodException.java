package com.hawolt.http.proxy;

/**
 * Created: 30/07/2022 16:25
 * Author: Twitter @hawolt
 **/

public class UnsupportedRequestMethodException extends RuntimeException {

    public UnsupportedRequestMethodException(String method) {
        super("Method: " + method + ", not supported.");
    }
}
