package com.hawolt.proxy;


import com.hawolt.http.Method;

/**
 * Created: 30/07/2022 16:25
 * Author: Twitter @hawolt
 **/

public class UnsupportedRequestMethodException extends RuntimeException {

    public UnsupportedRequestMethodException(Method method) {
        super("Method: " + method.name() + ", not supported.");
    }
}
