package com.hawolt.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created: 30/07/2022 23:56
 * Author: Twitter @hawolt
 **/

public class ReflectHttp {
    public static void enable(String... methods) throws NoSuchFieldException, IllegalAccessException {
        Field declaredFieldMethods = HttpURLConnection.class.getDeclaredField("methods");
        Field declaredFieldModifiers = Field.class.getDeclaredField("modifiers");
        declaredFieldModifiers.setAccessible(true);
        declaredFieldModifiers.setInt(declaredFieldMethods, declaredFieldMethods.getModifiers() & ~Modifier.FINAL);
        declaredFieldMethods.setAccessible(true);
        String[] previous = (String[]) declaredFieldMethods.get(null);
        Set<String> current = new LinkedHashSet<>(Arrays.asList(previous));
        current.addAll(Arrays.asList(methods));
        String[] patched = current.toArray(new String[0]);
        declaredFieldMethods.set(null, patched);
    }
}
