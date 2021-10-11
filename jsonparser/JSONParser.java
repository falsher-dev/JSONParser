package ru.falsher.jsonparser;

import org.omg.CORBA.portable.UnknownException;
import ru.falsher.io.BufferedInputStream;

import java.io.*;

public class JSONParser {

    public static JSONElement parse(InputStream is,boolean closeStream) throws IOException {
        final int[] index = {0,-1};
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.startRecord();
        JSONElement ret = parse(index,bis);
        if (closeStream) bis.close();
        else bis.clear();
        return ret;
    }

    public static JSONElement parse(BufferedInputStream bis) throws IOException {
        return parse(new int[]{0,-1},bis);
    }

    private static JSONElement parse(final int[] index, BufferedInputStream bis) throws IOException {
        int c;
        int type;
        while (true){
            index[1]++;
            if ((c = bis.read()) == ' ' || c == '\t' || c == '\r') continue;
            else if (c == '\n') {
                nextLine(index);
                continue;
            }
            if (c == -1) {
                type = -1;
                break;
            } else if (c == '"') {
                type = 0;
                bis.back();
                index[1]--;
                break;
            }  else if (c == '{') {
                type = 1;
                break;
            } else if (c == '[') {
                type = 2;
                break;
            } else if (c == 't' || c == 'f') {
                type = 3;
                bis.back();
                index[1]--;
                break;
            }  else if (c == 'n') {
                type = 5;
                bis.back();
                index[1]--;
                break;
            } else if ((c >= '0' && c <= '9') || c == '.') {
                type = 4;
                bis.back();
                index[1]--;
                break;
            } else ParseException.throwUnexpectedChar((char) c,index[0],index[1]);
        }
        if (type == -1) ParseException.throwEndOfInput();
        else if (type == 0) return new JSONString(readString(bis,index));
        else if (type == 1 || type == 2) {
            JSONElement ret;
            if (type == 1) ret = new JSONObject();
            else ret = new JSONArray();
            String key = null;
            while (true){
                if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
                if (c == ' ' || c == '\r') continue;
                else if (c == '\n') {
                    nextLine(index);
                    continue;
                }
                else if (c == '}') {
                    if (type == 1) return ret;
                    else ParseException.throwUnexpectedChar('}', index[0], index[1]);
                }
                else if (c == ']') {
                    if (type == 2) return ret;
                    else ParseException.throwUnexpectedChar(']', index[0], index[1]);
                }
                else if (c == ',') continue;
                bis.back();
                index[1]--;
                if (type == 1) {
                    key = readString(bis,index);
                    bis.clear();
                    while (true){
                        if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
                        if (c == ' ' || c == '\t' || c == '\r') continue;
                        else if (c == '\n') {
                            nextLine(index);
                            continue;
                        }
                        else if (c == ':') break;
                        else ParseException.throwExpectedButFound(':',(char)c,index[0],index[1]);
                    }
                }
                JSONElement value = parse(index,bis);
                if (type == 1) ((JSONObject)ret).append(key,value);
                else ((JSONArray)ret).append(value);
            }
        } else if (type == 3){
            while (true) {
                index[1]++;
                if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
                if (c == ' ' || c == '\t' || c == '\r');
                else if (c == '\n') nextLine(index);
                else if (c == 't' || c == 'f') break;
                else ParseException.throwUnexpectedChar((char)c,index[0],index[1]);
            }
            char[] cc = new char[3];
            if (bis.read(cc) == -1) ParseException.throwEndOfInput();
            index[1] += 3;
            if (cc[0] == 'r' && cc[1] == 'u' && cc[2] == 'e') return JSONBoolean.TRUE;
            else if (cc[0] == 'a' && cc[1] == 'l' && cc[2] == 's') {
                c = bis.read();
                index[1] += 1;
                if (c == -1) ParseException.throwEndOfInput();
                else if (c == 'e') return JSONBoolean.FALSE;
                else ParseException.throwUnexpectedChar((char) c, index[0], index[1]);
            } else ParseException.throwUnexpectedChar((char) c, index[0], index[1] - 3);
        }  else if (type == 5){
            while (true) {
                index[1]++;
                if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
                if (c == ' ' || c == '\t' || c == '\r');
                else if (c == '\n') nextLine(index);
                else if (c == 'n') break;
                else ParseException.throwUnexpectedChar((char)c,index[0],index[1]);
            }
            char[] cc = new char[3];
            if (bis.read(cc) == -1) ParseException.throwEndOfInput();
            index[1] += 3;
            if (cc[0] == 'u' && cc[1] == 'l' && cc[2] == 'l') return JSONNull.INSTANCE;
            else ParseException.throwUnexpectedChar((char) c, index[0], index[1] - 3);
        } else {
            while (true) {
                index[1]++;
                if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
                if (c == ' ' || c == '\t' || c == '\r');
                else if (c == '\n') {
                    nextLine(index);
                }
                else if ((c >= '0' && c <= '9') || c == '.') break;
                else ParseException.throwUnexpectedChar((char)c,index[0],index[1]);
            }
            bis.back();
            index[1]--;
            String value = "";
            boolean
                    octal = false,
                    hex = false,
                    decimal = false;

            boolean checkNextEqX = false;
            while (true) {
                index[1]++;
                if ((c = bis.read()) == -1) break;
                if (checkNextEqX) {
                    if (c == 'x') {
                        hex = true;
                        continue;
                    }
                    else if (c == '.') decimal = true;
                    else {
                        octal = true;
                    }
                    checkNextEqX = false;
                }
                if (c == ' ' || c == '\t' || c == '\r' || c == ':' || c == ',' || c == ']' || c == '}') {
                    bis.back();
                    index[1]--;
                    break;
                }
                else if (c == '\n') {
                    nextLine(index);
                    break;
                } else if (!(octal || hex || decimal)) {
                    if (c == '0') checkNextEqX = true;
                    else {
                        decimal = true;
                        value += ((char)c);
                    }
                }
                else if (c == '.' || c >= '0' && c <= '9') {
                    value+=((char)c);
                }
                else if (hex) {
                    if ((c >= 'A' && c <= 'F')) value += (char)(c+32);
                    else if (c >= 'a' && c <= 'f') value += (char)c;
                    else ParseException.throwUnexpectedChar((char) c,index[0],index[1]);
                }
                else ParseException.throwNumberFormatException((char) c,index[0],index[1]);
            }

            final int encoding = hex ? 16 : decimal ? 10 : octal ? 8 : -1;

            if (encoding == -1) throw new UnknownException(null);

            double ret = 0;

            int i = value.indexOf('.') - 1;

            if (i == -2) i = value.length() - 1;

            for (char cc: value.toCharArray()){
                if (cc == '.') continue;
                ret += ((cc <= '9') ? cc - '0' : cc - 'a') * Math.pow(encoding,i);
                i--;
            }

            return new JSONNumber(ret);
        }
        return null;
    }

    private static void nextLine(final int[] index){
        index[0]++;
        index[1] = 0;
    }

    private static String readString(BufferedInputStream bis, final int[] index) throws IOException {
        int c;
        int framingSymbol;
        while (true){
            index[1]++;
            if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
            if (c == ' ' || c == '\t' || c == '\r') continue;
            else if (c == '\n') {
                nextLine(index);
                continue;
            }
            if (c == '"' || c == '\'') {
                framingSymbol = c;
                break;
            }
            else ParseException.throwUnexpectedChar((char) c,index[0],index[1]);
        }

        boolean escapeNext = false;
        StringBuilder sb = new StringBuilder();

        while (true) {
            index[1]++;
            if ((c = bis.read()) == -1) ParseException.throwEndOfInput();
            if (c == '\r') continue;
            else if (c == '\n') {
                nextLine(index);
                if (!escapeNext) ParseException.throwUnterminatedStringLiteral(index[0], index[1]);
                else {
                    sb.appendCodePoint('\n');
                    escapeNext = false;
                }
                continue;
            }
            if (escapeNext){
                if (c == 'u') {
                    char[] cc = new char[4];
                    short codePoint=0;
                    if (bis.read(cc) == -1) ParseException.throwEndOfInput();
                    for (int i = 0; i < 4; i++){
                        if (cc[i] < '0' || (cc[i] > '9' && cc[i] < 'A') || (cc[i] > 'F' && cc[i] < 'a') || cc[i] > 'f') ParseException.throwInvalidUnicodeEscape(cc[i],index[0],index[1]-2);
                        codePoint <<= 4;
                        if (cc[i] >= '0' && cc[i] <= '9') cc[i] -= '0';
                        else if (cc[i] >= 'A' && cc[i] <= 'F') cc[i] -= ('A' - 10);
                        else if (cc[i] >= 'a' && cc[i] <= 'f') cc[i] -= ('a' - 10);
                        codePoint |= (cc[i] & 0xF);
                    }
                    sb.appendCodePoint(codePoint);
                    index[1]+=4;
                }
                else if (c == 't') sb.appendCodePoint('\t');
                else if (c == 'n') sb.appendCodePoint('\n');
                else if (c == 'r') sb.appendCodePoint('\r');
                else if (c == 'f') sb.appendCodePoint('\f');
                else if (c == 'b') sb.appendCodePoint('\b');
                else sb.appendCodePoint(c);
                escapeNext = false;
            } else if (c == framingSymbol) break;
            else if (c == '\\'){
                escapeNext = true;
            } else sb.appendCodePoint(c);
        }
        return sb.toString();
    }
}
