package com.hawolt.socket;

/**
 * Created: 07/03/2023 17:52
 * Author: Twitter @hawolt
 **/

public interface SocketDataSpoofer {
    byte[] onServerData(byte[] b);

    byte[] onApplicationData(byte[] b);
}
