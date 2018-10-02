package com.ftpix.sherdogparser.exceptions;

public class NotASherdogURLException extends SherdogParserException {
    public NotASherdogURLException() {
        super("The url isn't connecting to sherdog domain expected format: http://www.sherdog.com/xxxxxxxx");
    }
}
