package com.farneser.cloudfilestorage.utils;

public abstract class UserUtils {
    public static String getUserBucket(long id) {
        return "user-" + id + "-files";
    }
}
