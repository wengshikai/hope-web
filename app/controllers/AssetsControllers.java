package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.HTTPTool;
import util.LocalStoreTool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by shanmao on 15-12-21.
 */
public class AssetsControllers  extends Controller {
    @Security.Authenticated(Secured.class)
    public Result getImage(String imagename) {
        String realfilename = imagename;
        try {
            realfilename = URLDecoder.decode(imagename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(!imagename.contains(".")){
            return notFound();
        }
        byte[]  content = LocalStoreTool.getImage(realfilename);
        response().setContentType(HTTPTool.getContentTypeBySuffix(realfilename));
        return ok(content);
    }
}
