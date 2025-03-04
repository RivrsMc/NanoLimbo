package ua.nanit.limbo.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static String maximizeLength(String msg, int maxLength) {
        if (msg.length() > maxLength) {
            return msg.substring(0, maxLength);
        } else {
            return msg;
        }
    }

}
