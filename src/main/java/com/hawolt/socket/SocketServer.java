package com.hawolt.socket;

import com.hawolt.logger.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * Created: 19/11/2022 21:19
 * Author: Twitter @hawolt
 **/

public class SocketServer extends WebSocketServer {
    private static SocketServer instance;

    public static void launch() {
        SocketServer.instance = new SocketServer(new InetSocketAddress(8887));
        SocketServer.instance.start();
    }

    public static void forward(String message) {
        instance.broadcast(message);
    }

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Logger.debug("Chromium connected to local WebSocket server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Logger.debug("Chromium disconnected from local WebSocket server");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Logger.debug("Chromium sent us: {}", message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Logger.error(e);
    }

    @Override
    public void onStart() {
        Logger.debug("Started WebSocket server");
    }
}
