package com.ftpix.sherdogparser.exceptions;

public class NotASherdogURLException extends SherdogParserException {
    public NotASherdogURLException() {
        super("The url isn't connecting to sherdog domain expected format: https://www.sherdog.com/xxxxxxxx");
    }
}
