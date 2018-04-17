package util;

import models.dbmanager.GlobalTool;

import java.io.IOException;

/**
 * Created by shanmao on 15-12-5.
 */
public class LocalStoreTool {
    private static final String imagedir  = "data/image";
    static {
        FileTool.createDirP(imagedir);
    }
    public  static boolean putImage(String filename,byte[] filecontent){
        try {
            FileTool.putFileContent(imagedir+"/"+filename,filecontent);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public  static byte[] getImage(String filename){
        try {
            return FileTool.getFileContent(imagedir+"/"+filename);
        } catch (IOException e) {
            GlobalTool.logger.error("这张图片不存在" + imagedir+"/"+filename);
            return null;
        }
    }
}
