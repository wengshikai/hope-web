package util;

import util.FileTool;

import java.io.IOException;

/**
 * Created by weng on 15-12-5.
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
            return null;
        }
    }
}
