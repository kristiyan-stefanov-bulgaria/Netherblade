package com.hawolt.socket;

public interface SocketCallback {
    SocketDataSpoofer getSpoofer();

    SocketInterceptor getInterceptor();
}
