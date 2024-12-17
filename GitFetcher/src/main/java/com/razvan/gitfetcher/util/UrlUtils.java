package com.razvan.gitfetcher.util;

public class UrlUtils {
    public static final String URL_SEPARATOR = "/";

    public static String buildUrl(String... parts) {
        return String.join(URL_SEPARATOR, parts);
    }
}
