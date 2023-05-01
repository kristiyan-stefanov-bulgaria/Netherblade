package com.hawolt.socket;

import com.hawolt.logger.Logger;
import com.hawolt.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.function.Function;

public abstract class DataSocketProxy<T> extends SocketProxy implements SocketDataSpoofer {
    protected Function<byte[], T> transformer;
    protected DataInterceptor<T> interceptor;

    public DataSocketProxy(String hostname, int remote, int local, Function<byte[], T> transformer) {
        super(hostname, remote, local);
        this.transformer = transformer;
        this.setSpoofer(this);
    }

    public void inject(byte[] data) {
        List<Pair<Socket, Socket>> list = getList();
        for (int i = list.size() - 1; i >= 0; i--) {
            Socket socket = list.get(i).getValue();
            try {
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    public void setInterceptor(DataInterceptor<T> interceptor) {
        this.interceptor = interceptor;
    }
}
