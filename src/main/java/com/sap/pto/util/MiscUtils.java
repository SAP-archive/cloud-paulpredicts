package com.sap.pto.util;

import java.net.URL;

/**
 * Utility class providing commonly needed helper functionality.
 */
public class MiscUtils {
    /**
     * Returns the URL of the resource specified. This method simply wraps the lengthy
     * Thread.currentThread().getContextClassLoader().getResource() call.
     */
    public static URL getResource(String resource) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(resource);

        return url;
    }
}
