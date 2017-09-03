package controllers;

import models.*;
import play.mvc.*;


public class Init extends Controller {

    public Result index() {
        GlobalTool.initDB();
        return ok("init ok!");
    }

}