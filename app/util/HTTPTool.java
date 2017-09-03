package util;

/**
 * Created by fengya on 15-12-21.
 */
public class HTTPTool {
    /**
     * 根据后缀名返回content-type
     * @param filename
     * @return
     */
    public static String getContentTypeBySuffix(String filename){
        String ret = null;
        if(filename == null){
            return null;
        }
        int index = filename.lastIndexOf(".");
        if(index <= 0 || ((index + 1) == filename.length())){ //没有.,index<0,只有后缀(如.txt),index==0,没有后缀(如ff.),index + 1 == filename.length()
            return null;
        }

        String suffix=filename.substring(index+1);

        switch (suffix.toLowerCase()){
            case "apk":
                ret = "application/vnd.android.package-archive";
                break;
            case "pdf":
                ret = "application/pdf";
                break;
            case "exe":
                ret = "application/octet-stream";
                break;
            case "zip":
                ret = "application/zip";
                break;
            case "doc":
                ret = "application/msword";
                break;
            case "xls":
                ret = "application/vnd.ms-excel";
                break;
            case "ppt":
                ret = "application/vnd.ms-powerpoint";
                break;
            case "gif":
                ret ="image/gif";
                break;
            case "png":
                ret ="image/png";
                break;
            case "jpeg":
            case "jpg":
                ret = "image/jpeg";
                break;
            case "webp":
                ret ="image/webp";
                break;
            case "mp3":
                ret = "audio/mpeg";
                break;
            case ".wav":
                ret ="audio/x-wav";
                break;
            case "mpeg":
            case "mpg":
            case "mpe":
                ret ="video/mpeg";
                break;
            case "mov":
                ret = "video/quicktime";
                break;
            case "avi":
                ret = "video/x-msvideo";
                break;
            case "txt":
                ret = "text/plain";
                break;
            case "htm":
            case "html":
                ret = "text/html";
                break;
            default:
                ret = "application/force-download";
                break;
        }
        return ret;
    }
}
