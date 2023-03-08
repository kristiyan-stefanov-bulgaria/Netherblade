package com.hawolt.socket;

/**
 * Created: 07/03/2023 17:52
 * Author: Twitter @hawolt
 **/

public interface SocketMessageCallback {
    byte[] onOutgoing(byte[] b);

    byte[] onIngoing(byte[] b);
}
