package util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by weng on 15-10-25.
 */

public class Security {
    public static String getMD5(String content) throws NoSuchAlgorithmException{
        return getMD5(content.getBytes());
    }

    public static String getMD5(byte[] content) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(content, 0, content.length);
        byte[] thedigest = md.digest();
        return toHex(thedigest);
    }

    private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }

        return sb.toString();
    }
}
