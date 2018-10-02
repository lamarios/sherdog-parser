package com.ftpix.sherdogparser;


import java.io.File;
import java.util.function.Function;

/**
 * Created by gz on 20-Aug-16.
 * Default values forthe parsers
 */
public class Constants {
    public final static String SHERDOG_TIME_ZONE = "America/New_York";
    public final static int PARSING_TIMEOUT = 60000;
    public static final PictureProcessor DEFAULT_PICTURE_PROCESSOR = (u, f) -> u;
    public static final String BASE_URL = "http://www.sherdog.com";

}
