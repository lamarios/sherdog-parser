package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.models.Fighter;

import java.io.IOException;

/**
 * Function to process the fighter pictures, the parameter is  going to be the sherdog picture URL.
 * With this function you can  for example upload the picture somewhere else (example S3) and return the S3 URL.
 *
 *
 */
@FunctionalInterface
public interface PictureProcessor {

    /**
     * Process a http url of a picture (most likely the fighter picture) and return another string. (other url, file path if you want to save the file on disk etc...
     * @param url the picture URL
     * @param fighter the fighter to process, you should not need to modify the fighter data here.
     * @return the new value that will be set to {@link Fighter#picture}
     * @throws IOException if anything goes wrong
     */
    String process(String url, Fighter fighter) throws IOException;
}
