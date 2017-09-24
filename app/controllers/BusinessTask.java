package controllers;

import hopeviews.task.html.addshopkeepertask;
import hopeviews.task.html.allnowbuyertask;
import hopeviews.task.html.allnowtask;
import models.BuyerManager;
import models.GlobalTool;
import models.LockTableManager;
import models.TaskTablesManager;
import models.dbtable.TaskTables;
import models.excel.BuyerTaskList;
import models.excel.ShopkeeperTask;
import models.excel.ShopkeeperTaskBook;
import models.excel.ShopkeeperTaskList;
import models.util.DatabaseTool;
import models.util.MiscTool;
import models.util.TaskHelper;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.FileTool;
import util.LocalStoreTool;
import util.ZIPTool;
import views.html.showoneshopkeeperbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




/**
 * Created by shanmao on 15-11-25.
 */
public class BusinessTask  extends Controller {
    public Result addshopkeepertask() {
        return ok(addshopkeepertask.render());
    }

    public Result doaddshopkeepertask() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zip = body.getFile("shopkeeperzip");
        if (zip != null) {
            try {
                FileTool.deleteDirectory("data/zip/");
                FileTool.createDestDirectoryIfNotExists("data/zip/");
                ZIPTool.unZipToFolder(zip.getFile().getAbsolutePath(),"data/zip/");
                List<String> dirlist = FileTool.getFileListInDirectory("data/zip/");
                String dirpath = "";
                for(String s:dirlist){
                    if(s.contains("__")){
                        continue;
                    }
                    dirpath = s;
                }
                if(dirpath.equals("")){
                    flash("error", "file error!");
                    return redirect(
                            routes.BusinessTask.addshopkeepertask()
                    );
                }

                int doret  = doAddOneTaskBook(dirpath);
                if(doret == 1){
                    flash("error", "重复添加");
                    return redirect(routes.BusinessTask.addshopkeepertask());
                }
                if(doret==2){
                    flash("error", "插入出错，请删除后重新插入！");
                    return redirect(routes.BusinessTask.addshopkeepertask());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            flash("succ", "成功添加");
            return redirect(
                    routes.BusinessTask.addshopkeepertask()
            );
        } else {
            flash("error", "Missing file");
            return redirect(
                    routes.BusinessTask.addshopkeepertask()
            );
        }
    }

    /** 批量添加商家任务书 */
    public Result dobatchaddshopkeepertask() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zip = body.getFile("shopkeeperzip");
        if (zip != null) {
            try {
                //解压zip包
                FileTool.deleteDirectory("data/zip/");
                FileTool.createDestDirectoryIfNotExists("data/zip/");
                ZIPTool.unZipToFolder(zip.getFile().getAbsolutePath(),"data/zip/");

                //读取文件夹(要求必须唯一)
                List<String> dirList = FileTool.getFileListInDirectory("data/zip/");
                String dirPath = "";
                for(String s:dirList){
                    if(s.contains("__")){
                        continue;
                    }
                    dirPath = s;
                }
                if(dirPath.equals("")){
                    flash("batch_error", "压缩包内没有读取到文件夹!");
                    return redirect(
                            routes.BusinessTask.addshopkeepertask()
                    );
                }

                //遍历文件夹中的子目录
                List<String> newDirList = FileTool.getFileListInDirectoryWithoutDot(dirPath);
                for(String subPath:newDirList){
                    //添加每个商家任务书中的所有任务
                    int rsAddOneBook = doAddOneTaskBook(subPath);
                    if(rsAddOneBook==1){
                        flash("batch_error", "重复添加");
                        return redirect(routes.BusinessTask.addshopkeepertask());
                    }
                    if(rsAddOneBook==2){
                        flash("batch_error", "插入出错，请删除后重新插入！文件名:" + subPath);
                        return redirect(routes.BusinessTask.addshopkeepertask());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            flash("batch_succ", "成功添加!");
            return redirect(
                    routes.BusinessTask.addshopkeepertask()
            );
        } else {
            flash("batch_error", "文件内容为空!");
            return redirect(
                    routes.BusinessTask.addshopkeepertask()
            );
        }
    }

    /** 将一个商家任务书中的所有任务添加到数据库 */
    private int doAddOneTaskBook(String dirPath){
        ShopkeeperTaskBook shopkeeperTaskBook = new ShopkeeperTaskBook();
        //解析商家任务书
        shopkeeperTaskBook.parse(dirPath);

        //判断是否重复插入
        List<TaskTables> test= TaskTablesManager.getShopkeeperBookByTaskbookName(shopkeeperTaskBook.getTaskbookName());
        if(test != null && test.size() != 0){
            return 1;
        }

        //保存图片
        Map<String,byte[]> imgContentMap = shopkeeperTaskBook.getPicContentMap();
        for(Map.Entry<String,byte[]> entry:imgContentMap.entrySet()){
            LocalStoreTool.putImage(shopkeeperTaskBook.getTaskbookUuid()+entry.getKey(),entry.getValue());
        }

        //插入数据
        List<ShopkeeperTaskList> tasklists = shopkeeperTaskBook.getTasklist();
        for(ShopkeeperTaskList tasklist:tasklists){
            List<ShopkeeperTask> tasks = tasklist.getTasklist();
            for(ShopkeeperTask task:tasks){
                if(!TaskTablesManager.insert(task.getTaskbookUuid(),task.getTaskbookName(),task.getId(),
                        task.getKeyword(),task.getTaskRequirement(),task.getUnitPrice(),task.getGoodsNumber(),
                        task.getAllPrice(),task.getPic1(),task.getPic2(),task.getPic3(),task.getShopkeeperName(),
                        task.getShopName(),task.getShopWangwang(),task.getItemLink(),task.getPcCost(),task.getPhoneCost(),task.getSubTaskBookId())){
                    GlobalTool.loger.error("插入错误:" + tasklist);
                    return 2;
                }
            }
        }
        return 0;
    }




    @Security.Authenticated(Secured.class)
    public Result allnowtask() {
        int tasknum  = 0;
        double allprice  = 0;
        List<TaskTables> all = TaskTablesManager.getALl();
        Map<String,ShopkeeperTaskBook> taskbookMap =  TaskHelper.getShopkeeperTaskBook(all);
        for(Map.Entry<String,ShopkeeperTaskBook> iter:taskbookMap.entrySet()){
            tasknum += iter.getValue().getTaskNum();
            allprice += iter.getValue().getTaskAllPriceSum();
        }
        BigDecimal b2   =   new BigDecimal(allprice);
        allprice   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

        List<String> ssl = TaskTablesManager.getALlTaskBookName();
        int min_num = TaskTablesManager.getNeedBuyerNum();
        int now_num = TaskTablesManager.getMaxBuyerTaskBookId();
        return ok(allnowtask.render(tasknum,allprice,taskbookMap,ssl, min_num, now_num));
    }


    /** 获取所有刷手任务书 */
    @Security.Authenticated(Secured.class)
    public Result allnowbuyertask() {
        int tasknum  = 0;
        double allprice  = 0;
        List<TaskTables> all = TaskTablesManager.getALl();
        List<String> ssl = new ArrayList<String>();
        Map<String,BuyerTaskList> taskbookMap =  TaskHelper.getBuyerTaskBook(all);
        for(TaskTables tt:all){
            if(tt.getBuyerTaskBookId() == 0){
                return ok(allnowbuyertask.render(0,allprice,taskbookMap,ssl));
            }
        }
        for(Map.Entry<String,BuyerTaskList> iter:taskbookMap.entrySet()){
            tasknum += iter.getValue().getStlist().size();
            allprice += iter.getValue().getZongBenJinNum();
            ssl.add(iter.getKey());
        }
        BigDecimal b2   =   new BigDecimal(allprice);
        allprice   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

        return ok(allnowbuyertask.render(tasknum,allprice,taskbookMap,ssl));
    }


    public static class buyernumForm {
       public int  buyernum;
    }


    /** 分配任务 */
    @Security.Authenticated(Secured.class)
    public Result caculateTaskList() {
        Form<buyernumForm> form = Form.form(buyernumForm.class).bindFromRequest();
        int  i = TaskTablesManager.getNeedBuyerNum();
        int j = form.get().buyernum;
        if(j>i){
            i=j;
        }
        int min_num = TaskTablesManager.getNeedBuyerNum();
        if(i<min_num){
            i = min_num;
        }

        if(BuyerManager.getBuyerCount()<i){
            flash("error", "刷手数目不够！");
            return redirect(
                    routes.BusinessTask.allnowtask()
            );
        }

        //分配任务
        TaskTablesManager.updateNew(i, BuyerManager.getALl());

        //锁定上传功能
        models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, "TaskTables");
        if(entry == null) {
            LockTableManager.insert("TaskTables", 1);
        } else {
            LockTableManager.update("TaskTables", 1);
        }

        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }


    @Security.Authenticated(Secured.class)
    public Result clear() {
        //解锁上传功能
        models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, "TaskTables");
        if(entry == null) {
            LockTableManager.insert("TaskTables", 0);
        } else {
            LockTableManager.update("TaskTables", 0);
        }

        //清空任务表
        GlobalTool.initTask();
        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }

    @Security.Authenticated(Secured.class)
    public Result unLockTaskTables() {
        GlobalTool.initLock("TaskTables");
        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }

    @Security.Authenticated(Secured.class)
    public Result initLockverybegin() {
        GlobalTool.initLockverybegin();
        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }


    /** 下载所有分配后的表格压缩包 */
    @Security.Authenticated(Secured.class)
    public Result getAllTaskList() {

        List<TaskTables> all = TaskTablesManager.getALl();

        byte[] ret = MiscTool.buildDownloadTaskZip(all);

        response().setHeader("Content-Disposition", "attachment;filename=task.zip");
        return ok(ret);
    }

    public static class taskbookNameForm {
        public String taskbookName;
    }

    @Security.Authenticated(Secured.class)
    public Result deleteByTaskbookName() {
        //解锁上传功能
        models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, "TaskTables");
        if(entry == null) {
            LockTableManager.insert("TaskTables", 0);
        } else {
            LockTableManager.update("TaskTables", 0);
        }
        Form<taskbookNameForm> form = Form.form(taskbookNameForm.class).bindFromRequest();
        String bookName = form.get().taskbookName;
        TaskTablesManager.deleteByTaskbookName(bookName);

        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }

    @Security.Authenticated(Secured.class)
    public Result getshopkeeperbookbyshopkeeperbookname() {
        Form<taskbookNameForm> form = Form.form(taskbookNameForm.class).bindFromRequest();
        String bookName = form.get().taskbookName;
        List<TaskTables> entrys = TaskTablesManager.getShopkeeperBookByTaskbookName(bookName);
        if(entrys == null){
            return notFound();
        }

        Map<String,ShopkeeperTaskBook> bookmap = ShopkeeperTaskBook.buildBookFromTask(entrys);

        ShopkeeperTaskBook b = null;

        for(Map.Entry<String,ShopkeeperTaskBook> entry:bookmap.entrySet()){
            b = entry.getValue();
        }
        b.dodoer();
        return ok(showoneshopkeeperbook.render(b));
    }





}
