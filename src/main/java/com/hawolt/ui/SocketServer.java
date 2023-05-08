package com.hawolt.ui;

import com.hawolt.logger.Logger;
import kotlin.collections.ArrayDeque;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created: 19/11/2022 21:19
 * Author: Twitter @hawolt
 **/

public class SocketServer extends WebSocketServer {
    private static SocketServer instance;
    private static boolean isOpen;

    private static List<String> messageQueue = new ArrayList<>();

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    public static void launch() {
        SocketServer.instance = new SocketServer(new InetSocketAddress(8887));
        SocketServer.instance.start();
    }

    public static void forward(String message) {
        if (isOpen) {
            instance.broadcast(message);
        } else {
            messageQueue.add(message);
            Logger.debug("Trying to send message when the connection isn't open");
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        isOpen = true;

        for (String message : messageQueue) {
            instance.broadcast(message);
        }

        Logger.debug("Chromium connected to local WebSocket server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Logger.debug("Chromium disconnected from local WebSocket server");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Netherblade.redirect(message);
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
