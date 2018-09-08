package com.ftpix.sherdogparser;

import com.ftpix.sherdogparser.exceptions.NotASherdogURLException;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class ExceptionTest {
    private static Sherdog sherdog;

    @BeforeClass
    public static void setup() {
        sherdog = new Sherdog.Builder().withTimezone("Asia/Kuala_Lumpur").build();

    }



    @Test
    public void testUrlExceptions() throws ParseException, IOException, SherdogParserException {


        try {
            sherdog.getEvent("somegibbierish://something");
            Assert.fail("This should throw an exception as it is not a proper URL");
        }catch(NotASherdogURLException e){
            //yay
        }


        try {
            sherdog.getEvent("http://something.com");
            Assert.fail("This should throw an exception as it is not a secure connection to sherdog");
        }catch(NotASherdogURLException e){
            //yay
        }


        try {
            sherdog.getEvent("https://something.com");
            Assert.fail("This should throw an exception as it is not a connection to sherdog website");
        }catch(NotASherdogURLException e){
            //yay
        }


        try {
            sherdog.getEvent("https://www.sherrrrrr.com");
            Assert.fail("This should throw an exception as it is not a sherdog.com url");
        }catch(NotASherdogURLException e){
            //yay
        }


    }
}
