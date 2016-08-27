package com.ftpix.sherdogparser;


import java.io.File;

/**
 * Created by gz on 20-Aug-16.
 * Default values forthe parsers
 */
public class Constants {
    public final static String SHERDOG_TIME_ZONE = "America/New_York";
    public final static int PARSING_TIMEOUT = 60000;
    public static final String FIGHTER_PICTURE_CACHE_FOLDER = "cache/";

    static {
        File f = new File(FIGHTER_PICTURE_CACHE_FOLDER);

        if (f.exists()) f.mkdirs();
    }
}
