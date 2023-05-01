package com.hawolt.socket;

public interface SocketInterceptor {
    void sniffOriginalClient(byte[] b) throws Exception;

    void sniffOriginalServer(byte[] b) throws Exception;

    void sniffSpoofedClient(byte[] b) throws Exception;

    void sniffSpoofedServer(byte[] b) throws Exception;
}
