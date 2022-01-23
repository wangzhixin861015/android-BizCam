package com.bcnetech.bcnetechlibrary.util.uuid;

/**
 * Created by wenbin on 16/6/6.
 */
public abstract class UUIDUtils {
    public UUIDUtils() {
    }

    public static String createId() {
        return UUIDGenerator.generateHex();
    }
}
