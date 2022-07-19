package com.projects.recommend.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

public class Util {
    // Encrypt the user password before saving to database
    public static String encryptPassword(String userId, String password) throws IOException {
        return DigestUtils.md5Hex(userId + DigestUtils.md5Hex(password)).toLowerCase();
    }
}

