package com.hawolt.mitm;

/**
 * Created: 22/11/2022 05:33
 * Author: Twitter @hawolt
 **/

public class Unsafe {
    @SuppressWarnings(value = "all")
    public static <T> T cast(Object o) {
        return (T) o;
    }
}
