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

    public static final  String CreateBuyer = "CREATE TABLE `Buyer` (`id` bigint auto_increment ,`name`  varchar(64) ,"+
            "`wangwang` varchar(64),`mobilephone` varchar(64),`level` int, PRIMARY KEY (`id`), UNIQUE (`wangwang`)); ";

    public static final  String CreateUser = "CREATE TABLE `User` (`name` varchar(64) NOT NULL DEFAULT '',`salt` varchar(64) NOT NULL DEFAULT '',`password` varchar(64) NOT NULL DEFAULT '',PRIMARY KEY (`name`)) ;";

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
        DatabaseTool.dropTable("default","Buyer");
        DatabaseTool.dropTable("default","TaskHistory");
        DatabaseTool.dropTable("default","NowTask");
        DatabaseTool.dropTable("default","TaskTables");
        DatabaseTool.dropTable("default","LockTable");
        DatabaseTool.dropTable("default","CombineShopBuyer");


        DatabaseTool.dosql("user",CreateUser );
        DatabaseTool.dosql("user","truncate table `User`");
        UserManager.insertUser("weng", "123456");
        UserManager.insertUser("yanjue", "yanjue123");

        DatabaseTool.dosql("default",CreateBuyer);
        DatabaseTool.dosql("default",CreateTaskHistory);
        DatabaseTool.dosql("default",CreateNowTask);
        DatabaseTool.dosql("default",CreateTaskTables);
        DatabaseTool.dosql("default",CreateLockTable);
        DatabaseTool.dosql("default","truncate table `LockTable`");
        LockTableManager.insert("TaskTables", 0);
        DatabaseTool.dosql("default",CreateCombineShopBuyer);
    }

    public static void initBuyer(){
        DatabaseTool.dosql("default","truncate table `Buyer`");
    }

    public static void initTask(){
        DatabaseTool.dosql("default","truncate table `TaskTables`");
        FileTool.deleteDirectory("data/image/");
        FileTool.createDestDirectoryIfNotExists("data/image/");
    }

    public static void initLockverybegin(){
        DatabaseTool.dropTable("default","LockTable");
        DatabaseTool.dosql("default", CreateLockTable);
        LockTableManager.insert("TaskTables", 0);
    }


    public static void initLock(String key){
        models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, key);
        if(entry == null) {
            LockTableManager.insert("TaskTables", 0);
        } else {
            LockTableManager.update("TaskTables", 0);
        }
    }


    public static void initCombineShopBuyer() {
        DatabaseTool.dosql("default","truncate table `CombineShopBuyer`");
    }

}
