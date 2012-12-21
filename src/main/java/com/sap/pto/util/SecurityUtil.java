package com.sap.pto.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.SimpleByteSource;
import org.jsoup.Jsoup;

public class SecurityUtil {
    public static String textOnly(String html) {
        if (html == null) {
            return null;
        }
        String text = Jsoup.parse(html.replaceAll("\n", "br2n")).text();
        text = text.replaceAll("br2n", "\n");

        return text;
    }

    public static String getPasswordHash(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }

        Sha256Hash sha256Hash = new Sha256Hash(password, getSalt(username.toLowerCase(Locale.ENGLISH)).getBytes());

        return sha256Hash.toHex();
    }

    public static SimpleByteSource getSalt(String username) {
        return new SimpleByteSource("pto_" + username.toLowerCase(Locale.ENGLISH));
    }

}
