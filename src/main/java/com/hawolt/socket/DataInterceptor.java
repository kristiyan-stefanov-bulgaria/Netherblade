package com.hawolt.socket;

public interface DataInterceptor<T> {
    void onOriginalInterceptClient(T t) throws Exception;

    void onOriginalInterceptServer(T t) throws Exception;

    void onSpoofedInterceptClient(T T) throws Exception;

    void onSpoofedInterceptServer(T T) throws Exception;
}
