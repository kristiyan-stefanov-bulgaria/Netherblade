package com.hawolt.socket;

import com.hawolt.logger.Logger;
import com.hawolt.util.Pair;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created: 07/03/2023 17:32
 * Author: Twitter @hawolt
 **/

public class SocketProxy implements SocketCallback, Runnable {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final List<Pair<Socket, Socket>> list = new LinkedList<>();
    private final int remote, local;
    protected String hostname;
    private SocketInterceptor interceptor;
    private SocketDataSpoofer spoofer;
    private ServerSocket socket;
    private Future<?> future;

    public SocketProxy(String hostname, int remote, int local) {
        this.hostname = hostname;
        this.remote = remote;
        this.local = local;
    }

    public void start() {
        if (future == null || future.isCancelled()) {
            this.future = this.service.submit(this);
        } else {
            Logger.warn("{} did you mean to re-start this proxy-server?", "{$ORIGIN}");
        }
    }

    public void stop() throws IOException {
        this.socket.close();
        this.future.cancel(true);
    }

    public List<Pair<Socket, Socket>> getList() {
        return list;
    }

    @SuppressWarnings("all")
    public void run() {
        try {
            this.socket = new ServerSocket(local);
            int protocolIndex = hostname.indexOf("://");
            if (protocolIndex != -1) hostname = hostname.substring(protocolIndex + 3);
            do {
                Logger.info("Awaiting connection for Socket proxy {} @ {}:{}", local, hostname, remote);
                Socket in = socket.accept();
                Logger.info("Accepted connection for Socket proxy {} @ {}:{}", local, hostname, remote);
                Socket out = SSLSocketFactory.getDefault().createSocket(hostname, remote);
                pool.execute(new ApplicationToProxyConnection(this, in, out));
                pool.execute(new ServerToProxyConnection(this, out, in));
                list.add(Pair.from(in, out));
            } while (!socket.isClosed());
        } catch (Exception e) {
            Logger.fatal("Failed to setup Socket proxy on port {}, {}", local, e.getMessage());
        }
    }

    @Override
    public SocketDataSpoofer getSpoofer() {
        return spoofer;
    }

    public void setSpoofer(SocketDataSpoofer spoofer) {
        this.spoofer = spoofer;
    }

    @Override
    public SocketInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(SocketInterceptor interceptor) {
        this.interceptor = interceptor;
    }
}
