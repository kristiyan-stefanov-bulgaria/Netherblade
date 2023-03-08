package com.hawolt.socket;

import com.hawolt.logger.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created: 07/03/2023 17:32
 * Author: Twitter @hawolt
 **/

public class SocketProxy implements Runnable {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final SocketMessageCallback callback;
    private final int remote, local;
    private final String hostname;

    private ServerSocket socket;
    private Future<?> future;

    public SocketProxy(SocketMessageCallback callback, String hostname, int remote, int local) {
        this.callback = callback;
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

    @SuppressWarnings("all")
    public void run() {
        try {
            this.socket = new ServerSocket(local);
            do {
                Logger.info("Awaiting connection for Socket proxy {} @ {}:{}", local, hostname, remote);
                Socket in = socket.accept();
                Socket out = SSLSocketFactory.getDefault().createSocket(hostname, remote);
                pool.execute(new ApplicationToProxyConnection(callback, in, out));
                pool.execute(new ProxyToTargetConnection(callback, out, in));
            } while (!socket.isClosed());
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
