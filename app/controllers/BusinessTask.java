package controllers;

import hopeviews.task.html.addshopkeepertask;
import hopeviews.task.html.allnowbuyertask;
import hopeviews.task.html.allnowtask;
import models.dbmanager.BuyerManager;
import models.dbmanager.GlobalTool;
import models.dbmanager.LockTableManager;
import models.dbmanager.TaskTablesManager;
import models.entity.TaskTables;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




/**
 * Created by shanmao on 15-11-25.
 */
public class BusinessTask  extends Controller {

    /** 渲染添加任务书页面 */
    public Result addShopkeeperTask() {
        return ok(addshopkeepertask.render());
    }


    /** 添加单个商家任务书 */
    public Result doAddShopKeeperTask() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zip = body.getFile("shopKeeperZip");
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
                    return redirect(routes.BusinessTask.addShopkeeperTask());
                }

                int doret  = doAddOneTaskBook(dirpath);
                if(doret == 1){
                    flash("error", "重复添加");
                    return redirect(routes.BusinessTask.addShopkeeperTask());
                }
                if(doret==2){
                    flash("error", "插入出错，请删除后重新插入！");
                    return redirect(routes.BusinessTask.addShopkeeperTask());
                }
            } catch (Exception e) {
                flash("error", "文件解析失败！");
                return redirect(routes.BusinessTask.addShopkeeperTask());
            }

            flash("succ", "成功添加");
            return redirect(routes.BusinessTask.addShopkeeperTask());
        } else {
            flash("error", "Missing file");
            return redirect(routes.BusinessTask.addShopkeeperTask());
        }
    }


    /** 批量添加商家任务书 */
    public Result doBatchAddShopKeeperTask() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zip = body.getFile("shopKeeperZip");

        if (zip == null) {
            flash("batch_error", "文件内容为空!");
            return redirect(routes.BusinessTask.addShopkeeperTask());
        }

        try {
            //解压zip包
            FileTool.deleteDirectory("data/zip/");
            FileTool.createDestDirectoryIfNotExists("data/zip/");
            ZIPTool.unZipToFolder(zip.getFile().getAbsolutePath(),"data/zip/");
        } catch (Exception e) {
            flash("batch_error", "解压zip文件失败!");
            return redirect(routes.BusinessTask.addShopkeeperTask());
        }

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
            return redirect(routes.BusinessTask.addShopkeeperTask());
        }

        //用于记录报错信息
        String exceptionMessage = "";
        //遍历文件夹中的子目录
        List<String> newDirList = FileTool.getFileListInDirectoryWithoutDot(dirPath);
        for(String subPath:newDirList){
            try {
                //添加每个商家任务书中的所有任务
                int rsAddOneBook = doAddOneTaskBook(subPath);
                if (rsAddOneBook == 1) {
                    throw new Exception("重复添加!目录:" + subPath + "\n");
                }
                if (rsAddOneBook == 2) {
                    throw new Exception("插入数据库出错!目录:" + subPath + "\n");
                }
            } catch (Exception e) {
                //如果异常,流程不中断,继续解析下一个文件
                exceptionMessage += e.getMessage();
            }
        }

        if(exceptionMessage.equals("")) {
            flash("batch_succ", "成功添加!");
            return redirect(routes.BusinessTask.addShopkeeperTask());
        } else {
            GlobalTool.logger.error(exceptionMessage);
            if (exceptionMessage.length() < 1024) { //flash储存的字符串有长度限制,先临时这么做
                flash("batch_error", "添加失败!" + exceptionMessage);
            } else {
                flash("batch_error", "添加失败!" + exceptionMessage.substring(0, 1023));
            }
            return redirect(routes.BusinessTask.addShopkeeperTask());
        }
    }


    /** 将一个商家任务书中的所有任务添加到数据库 */
    private int doAddOneTaskBook(String dirPath) throws Exception {
        ShopkeeperTaskBook shopkeeperTaskBook = new ShopkeeperTaskBook();

        //从文件夹名称中获取商家编号
        int shopId = 0;
        try {
            shopId = Integer.parseInt(dirPath.split("\\s+")[1]);
        } catch (Exception e) {
            GlobalTool.logger.error("获取商家编号失败", e);
        }

        //解析商家任务书
        try {
            shopkeeperTaskBook.parse(dirPath);
        } catch (Exception e) {
            throw new Exception("解析文件出错!目录:" + dirPath + "; 详情:" + e.getMessage() + "\n");
        }

        //判断是否重复插入
        List<TaskTables> test= TaskTablesManager.getShopkeeperBookByTaskBookName(shopkeeperTaskBook.getTaskBookName());
        if(test != null && test.size() != 0){
            return 1;
        }

        //保存图片
        Map<String,byte[]> imgContentMap = shopkeeperTaskBook.getPicContentMap();
        for(Map.Entry<String,byte[]> entry:imgContentMap.entrySet()){
            LocalStoreTool.putImage(shopkeeperTaskBook.getTaskBookUuid()+entry.getKey(),entry.getValue());
        }

        //插入数据
        List<ShopkeeperTaskList> taskLists = shopkeeperTaskBook.getTasklist();
        for(ShopkeeperTaskList taskList:taskLists){
            List<ShopkeeperTask> tasks = taskList.getTasklist();
            for(ShopkeeperTask task:tasks){
                if(!TaskTablesManager.insert(shopId, task.getTaskBookUuid(),task.getTaskBookName(),task.getId(),
                        task.getKeyword(),task.getTaskRequirement(),task.getUnitPrice(),task.getGoodsNumber(),
                        task.getAllPrice(),task.getPic1(),task.getPic2(),task.getPic3(),task.getShopkeeperName(),
                        task.getShopName(),task.getShopWangwang(),task.getItemLink(),task.getPcCost(),task.getPhoneCost(),task.getSubTaskBookId())){
                    GlobalTool.logger.error("插入错误:" + taskList);
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
        models.entity.LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, "TaskTables");
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
        models.entity.LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, "TaskTables");
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

    public static class taskBookNameForm {
        public String taskBookName;
    }

    @Security.Authenticated(Secured.class)
    public Result deleteByTaskBookName() {
        //解锁上传功能
        models.entity.LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, "TaskTables");
        if(entry == null) {
            LockTableManager.insert("TaskTables", 0);
        } else {
            LockTableManager.update("TaskTables", 0);
        }
        Form<taskBookNameForm> form = Form.form(taskBookNameForm.class).bindFromRequest();
        String bookName = form.get().taskBookName;
        TaskTablesManager.deleteByTaskBookName(bookName);

        return redirect(
                routes.BusinessTask.allnowtask()
        );
    }

    @Security.Authenticated(Secured.class)
    public Result getshopkeeperbookbyshopkeeperbookname() {
        Form<taskBookNameForm> form = Form.form(taskBookNameForm.class).bindFromRequest();
        String bookName = form.get().taskBookName;
        List<TaskTables> entrys = TaskTablesManager.getShopkeeperBookByTaskBookName(bookName);
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
