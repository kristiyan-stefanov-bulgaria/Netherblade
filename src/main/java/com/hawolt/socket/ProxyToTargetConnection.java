package com.hawolt.socket;

import com.hawolt.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created: 07/03/2023 17:46
 * Author: Twitter @hawolt
 **/

public class ProxyToTargetConnection extends SocketConnection {

    public ProxyToTargetConnection(SocketMessageCallback callback, Socket in, Socket out) {
        super(callback, in, out);
    }

    @Override
    public void run() {
        try (InputStream input = in.getInputStream()) {
            OutputStream stream = out.getOutputStream();
            int code;
            while (in.isConnected() && out.isConnected() && (code = input.read()) != -1) {
                byte[] b = callback.onOutgoing(read(input, code, input.available()));
                if (b == null) continue;
                stream.write(b);
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
