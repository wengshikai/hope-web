package models.dbmanager;

import models.entity.LockTable;
import models.util.DatabaseTool;
import play.Configuration;
import play.Logger;
import play.Play;
import util.FileTool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shanmao on 15-9-29.
 */
public class GlobalTool {
    public static final Logger.ALogger logger =  Logger.of("GLOBAL:" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
    public static String urlPrefix =  "";

    public static final  String CreateUser =
            "CREATE TABLE `User` (" +
            "`name` varchar(64) NOT NULL DEFAULT ''," +
            "`salt` varchar(64) NOT NULL DEFAULT ''," +
            "`password` varchar(64) NOT NULL DEFAULT ''," +
            "PRIMARY KEY (`name`));";

    public static final  String CreateBuyer =
            "CREATE TABLE `Buyer` (" +
            "`id` bigint auto_increment," +
            "`name` varchar(64)," +
            "`wangwang` varchar(64)," +
            "`mobilephone` varchar(64)," +
            "`team` int," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE KEY (`wangwang`));";

    public static final  String CreateTaskTables =
            "CREATE TABLE `TaskTables` (" +
            "`taskId` bigint auto_increment ," +
            "`taskBookUuid` varchar(64) ," +
            "`taskBookName` varchar(64) ," +
            "`id` int ," +
            "`keyword` varchar(64)," +
            "`taskRequirement` varchar(64)," +
            "`unitPrice` double ," +
            "`goodsNumber` int," +
            "`allPrice` double ," +
            "`pic1` varchar(512)," +
            "`pic2` varchar(512)," +
            "`pic3` varchar(512)," +
            "`shopId` int ," +
            "`shopkeeperName` varchar(64)," +
            "`shopName` varchar(64)," +
            "`shopWangwang` varchar(64)," +
            "`itemLink` varchar(640)," +
            "`pcCost` double ," +
            "`phoneCost` double ," +
            "`buyerWangwang` varchar(64)," +
            "`buyerTeam` int," +
            "`buyerTaskBookId` int ," +
            "`subTaskBookId` int ," +
            "`batchNo` int default 0," +
            "PRIMARY KEY (`taskId`));";

    public static final  String CreateLockTable =
            "CREATE TABLE `LockTable` (" +
            "`key` varchar(64) ," +
            "`value` int," +
            "PRIMARY KEY (`key`)); ";

    public static final  String CreateCombineShopBuyer =
            "CREATE TABLE `CombineShopBuyer` (" +
            "`id` bigint auto_increment ," +
            "`shopName` varchar(64)," +
            "`buyerWangwang` varchar(64)," +
            "`price` int ," +
            "PRIMARY KEY (`id`), " +
            "UNIQUE KEY (`shopName`, `buyerWangwang`)); ";

    static {
        Configuration config = Play.application().configuration();
        urlPrefix = config.getString("play.http.context", "");
    }


    public static void initDB() {
        //删除所有的表
        DatabaseTool.dropTable("user", "User");
        DatabaseTool.dropTable("default","Buyer");
        DatabaseTool.dropTable("default","LockTable");
        DatabaseTool.dropTable("default","TaskTables");
        DatabaseTool.dropTable("default","CombineShopBuyer");

        //清除所有的持久化对象,并回滚所有未提交的事务
        DatabaseTool.userEm.clear();
        if (DatabaseTool.userEm.getTransaction().isActive()) {
            DatabaseTool.userEm.getTransaction().rollback();
        }

        DatabaseTool.defaultEm.clear();
        if (DatabaseTool.defaultEm.getTransaction().isActive()) {
            DatabaseTool.defaultEm.getTransaction().rollback();
        }

        //创建所有的表
        DatabaseTool.doSql("user",CreateUser );
        UserManager.insertUser("yanjue", "yanjue2927");

        DatabaseTool.doSql("default",CreateBuyer);
        DatabaseTool.doSql("default",CreateTaskTables);
        DatabaseTool.doSql("default",CreateLockTable);
        LockTableManager.insert("TaskTables", 0);
        DatabaseTool.doSql("default",CreateCombineShopBuyer);
    }


    public static void initBuyer(){
        DatabaseTool.doSql("default","truncate table `Buyer`");
    }


    public static void initTask(){
        DatabaseTool.doSql("default","truncate table `TaskTables`");
        FileTool.deleteDirectory("data/image/");
        FileTool.createDestDirectoryIfNotExists("data/image/");
    }


    public static void initLock(String key){
        LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, key);
        if(entry == null) {
            LockTableManager.insert("TaskTables", 0);
        } else {
            LockTableManager.update("TaskTables", 0);
        }
    }


    public static void initCombineShopBuyer() {
        DatabaseTool.doSql("default","truncate table `CombineShopBuyer`");
    }

}
