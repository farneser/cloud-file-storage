package com.farneser.cloudfilestorage.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class UrlUtils {
    public static String getValidUrlPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%21", "!")
                .replaceAll("%23", "#")
                .replaceAll("%3F", "?");
    }
}
