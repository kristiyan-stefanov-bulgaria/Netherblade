package com.hawolt.mitm.rtmp;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteSink {
    void drain(OutputStream outputStream) throws IOException;
}
