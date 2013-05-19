package com.sap.pto.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@SuppressWarnings("nls")
public class Consts {
    public static final String VERSION = "1.0.1";
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
    public static final DateTimeFormatter formatter = DateTimeFormat.forPattern(Consts.DATEFORMAT);
    public static final String SERVERNAME_PROPERTY_KEY = "runtime.servername"; // name of the server url detected during servlet request
    public static final String PAUL = "Paul";
    public static final String HANA = "HANA";
    public static final int PW_MINLENGTH = 4;
    public static final int MAX_PROFILE_IMAGE_WIDTH = 512;
    public static final int MAX_PROFILE_IMAGE_HEIGHT = 512;

}
