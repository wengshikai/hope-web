package controllers;

import models.dbmanager.GlobalTool;
import play.mvc.*;


public class Init extends Controller {

    public Result index() {
        GlobalTool.initDB();
        return ok("已清空所有数据!");
    }

}