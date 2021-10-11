package ru.falsher.jsonparser;

import java.io.IOException;

public class ParseException extends IOException {

    public ParseException() {}

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public static void throwEndOfInput() throws ParseException {
        throw new ParseException("Unexpected end of input of the json data");
    }

    public static void throwUnexpectedChar(char c, int row, int column) throws ParseException {
        throw new ParseException("Unexpected character '"+c+"' at line "+row+" column "+column+" of the json data");
    }

    public static void throwUnterminatedStringLiteral(int row, int column) throws ParseException {
        throw new ParseException("Unterminated String Literal at line "+row+" column "+column+" of the json data");
    }

    public static void throwInvalidUnicodeEscape(char c,int row, int column) throws ParseException {
        throw new ParseException("Invalid Unicode escape sequence at line "+row+" column "+column+" of the json data. Char '"+c+"'");
    }

    public static void throwExpectedButFound(char expected, char found,int row, int column) throws ParseException {
        throw new ParseException("'"+expected+"' expected but '"+found+"' found at line "+row+" column "+column+" of the json data");
    }

    public static void throwNumberFormatException(char c,int row, int column) throws ParseException {
        throw new ParseException("NumberFormatException: Unexpected character '"+c+"' at line "+row+" column "+column+" of the json data");
    }

}
