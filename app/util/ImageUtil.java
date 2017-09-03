package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by fengya on 15-11-25.
 */
public class ImageUtil {
    public static int[] getImageSize(byte[] content) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(content);
        int  w = 0;
        int h = 0;
        BufferedImage image = ImageIO.read(in);
        w = image.getWidth();
        h = image.getHeight();
        in.close();
        int[] ret = new int[2];
        ret[0] = w;
        ret[1] = h;
        return ret;
    }

    public static boolean allowPicType(String file){
        String lowcase = file.toLowerCase();
        if(lowcase.endsWith(".png") || lowcase.endsWith(".jpg") || lowcase.endsWith(".jpeg")){
            return true;
        }
        return false;
    }
}
