package models;

import models.util.DatabaseTool;
import play.Configuration;
import play.Logger;
import play.Play;
import util.FileTool;

/**
 * Created by weng on 15-9-29.
 */
public class GlobalTool {
    public static final Logger.ALogger loger =  Logger.of("GLOBAL");
    public static String urlprefix =  "";
    public static final  String CreateUserId = "CREATE TABLE `UserId` " +
            "(`tablename` varchar(64) NOT NULL DEFAULT '',`id` BIGINT NOT NULL DEFAULT 0,PRIMARY KEY (`tablename`)) ; ";

    public static final  String CreateBuyer = "CREATE TABLE `Buyer` (`id` bigint auto_increment ,`name`  varchar(64) ,"+
            "`wangwang` varchar(64),`mobilephone` varchar(64),`level` int, PRIMARY KEY (`id`)) ; ";

    public static final  String CreateUser = "CREATE TABLE `User` (`name` varchar(64) NOT NULL DEFAULT '',`salt` varchar(64) NOT NULL DEFAULT '',`password` varchar(64) NOT NULL DEFAULT '',PRIMARY KEY (`name`)) ;";

    public static final  String CreateShop = "CREATE TABLE `Shop` (`shopId` bigint auto_increment ," +
            "`shopkeeperId`  bigint ,`shopName`  varchar(64) ,`shopWangwang`  varchar(64) ," +
            "PRIMARY KEY (`shopId`)) ; ";

    public static final  String CreateTaskHistory = "CREATE TABLE `TaskHistory` (`id` varchar(64) NOT NULL DEFAULT '',`shuashou` varchar(64) NOT NULL DEFAULT ''," +
            "`dianpu`  varchar(64) NOT NULL DEFAULT '', `timestamp` INT NOT NULL DEFAULT 0 ,PRIMARY KEY (`id`)) ; ";
    public static final  String CreateNowTask = "CREATE TABLE `NowTask` (" +
            "`realid` varchar(128) NOT NULL DEFAULT ''," +
            "`taskbookid` varchar(64) ," +
            "`id` int ," +
            "`keyword` varchar(64) ," +
            "`taskrequirement` varchar(64) ," +
            "`unit_price` double ," +
            "`goods_number` int," +
            "`all_price` double ," +
            "`pics` varchar(512)," +
            "`shangjia_name` varchar(64)," +
            "`shop_name` varchar(64)," +
            "`shop_wangwang` varchar(64)," +
            "`item_link` varchar(640)," +
            "`pc_cost` double ," +
            "`phone_cost` double ," +
            "`shuashou_wangwang` varchar(64)," +
            "`shuashoutaskbookid` int ," +
            "PRIMARY KEY (`realid`)) ; ";

    public static final  String CreateTaskTables = "CREATE TABLE `TaskTables` (" +
            "`taskid` bigint auto_increment ," +
            "`taskbookUuid` varchar(64) ," +
            "`taskbookName` varchar(64) ," +
            "`id` int ," +
            "`keyword` varchar(64) ," +
            "`taskRequirement` varchar(64) ," +
            "`unitPrice` double ," +
            "`goodsNumber` int," +
            "`allPrice` double ," +
            "`pic1` varchar(512)," +
            "`pic2` varchar(512)," +
            "`pic3` varchar(512)," +
            "`shopkeeperName` varchar(64)," +
            "`shopName` varchar(64)," +
            "`shopWangwang` varchar(64)," +
            "`itemLink` varchar(640)," +
            "`pcCost` double ," +
            "`phoneCost` double ," +
            "`buyerWangwang` varchar(64)," +
            "`buyerTaskBookId` int ," +
            "`subTaskbookId` int ," +
            "PRIMARY KEY (`taskid`)); ";

    public static final  String CreateLockTable = "CREATE TABLE `LockTable` (" +
            "`key` varchar(64) ," +
            "`value` int," +
            "PRIMARY KEY (`key`)); ";

    public static final  String CreateCombineShopBuyer = "CREATE TABLE `CombineShopBuyer` (" +
            "`id` bigint auto_increment ," +
            "`shopName` varchar(64)," +
            "`buyerWangwang` varchar(64)," +
            "`price` int ," +
            "PRIMARY KEY (`id`), " +
            "UNIQUE (`shopName`, `buyerWangwang`)); ";

    static {
        Configuration config = Play.application().configuration();
        urlprefix = config.getString("play.http.context", "");
    }

    public static void initDB() {
        DatabaseTool.dropTable("user", "User");
        DatabaseTool.dropTable("default","UserId");
        DatabaseTool.dropTable("default","Buyer");
        DatabaseTool.dropTable("default","Shop");
        DatabaseTool.dropTable("default","TaskHistory");
        DatabaseTool.dropTable("default","NowTask");
        DatabaseTool.dropTable("default","TaskTables");
        DatabaseTool.dropTable("default","LockTable");
        DatabaseTool.dropTable("default","CombineShopBuyer");


        DatabaseTool.dosql("user",CreateUser );
        UserManager.insertUser("weng", "123456");
        UserManager.insertUser("yanjue", "yanjue123");


        DatabaseTool.dosql("default",CreateUserId);
        UserIdManager.insertId("ShangjiaUser", 0);
        UserIdManager.insertId("ShuashouUser",0);

        DatabaseTool.dosql("default",CreateTaskTables);
        DatabaseTool.dosql("default",CreateBuyer);
        DatabaseTool.dosql("default",CreateShop);
        DatabaseTool.dosql("default",CreateTaskHistory);
        DatabaseTool.dosql("default",CreateNowTask);
        DatabaseTool.dosql("default",CreateLockTable);
        DatabaseTool.dosql("default",CreateCombineShopBuyer);
        LockTableManager.insert("TaskTables", 1);
    }

    public static void initBuyer(){
        //DatabaseTool.dropTable("default","Buyer");
        //DatabaseTool.dosql("default",CreateBuyer);
        DatabaseTool.dosql("default","truncate table `Buyer`");

    }

    public static void initTask(){
//        DatabaseTool.dropTable("default","TaskTables");
//        DatabaseTool.dosql("default",CreateTaskTables);
        DatabaseTool.dosql("default","truncate table `TaskTables`");
        FileTool.deleteDirectory("data/image/");
        FileTool.createDestDirectoryIfNotExists("data/image/");
    }

    public static void initLockverybegin(){
        DatabaseTool.dropTable("default","LockTable");
        DatabaseTool.dosql("default", CreateLockTable);
        LockTableManager.insert("TaskTables", 1);
    }

    public static void initLock(){
        LockTableManager.update("TaskTables", 1);
    }


    public static void initCombineShopBuyer() {
        DatabaseTool.dosql("default","truncate table `CombineShopBuyer`");
    }

}
