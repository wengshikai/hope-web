package controllers;

import com.google.common.collect.Lists;
import lombok.Data;
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
import views.html.task.addshopkeepertask;
import views.html.task.allnowbuyertask;
import views.html.task.allnowtask;
import views.html.task.showoneshopkeeperbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
        int taskNum  = 0;
        double allPrice  = 0;
        //获取所有的任务
        List<TaskTables> all = TaskTablesManager.getALl();
        //将任务按照商家任务书归类
        Map<String,ShopkeeperTaskBook> taskBookMap =  TaskHelper.getShopkeeperTaskBook(all);
        for(Map.Entry<String,ShopkeeperTaskBook> task:taskBookMap.entrySet()){
            taskNum += task.getValue().getTaskNum(); //计算任务总数
            allPrice += task.getValue().getTaskAllPriceSum(); //计算任务总价
        }
        BigDecimal b2   =   new BigDecimal(allPrice);
        allPrice   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

        //获取所有商家任务书名称列表
        List<String> ssl = TaskTablesManager.getALlTaskBookName();
        int min_num = TaskTablesManager.getNeedBuyerNum();
        int allBuyerCount = BuyerManager.getBuyerCount().intValue();


        Map<Integer, Integer> buyerCountByTeam = new HashMap<>();
        //查询所有的小组名称
        List<Integer> teamList = BuyerManager.getALlTeams();
        if (teamList != null) {
            //查询每个小组的人数
            for (int team : teamList) {
                int buyerCount = BuyerManager.getBuyerCountByTeam(team).intValue();
                buyerCountByTeam.put(team, buyerCount);
            }
        }

        return ok(allnowtask.render(taskNum,allPrice,taskBookMap,ssl, min_num, allBuyerCount, buyerCountByTeam));
    }


    /** 获取所有刷手任务书 */
    @Security.Authenticated(Secured.class)
    public Result allnowbuyertask() {
        int taskNum  = 0;
        double allPrice  = 0;
        List<TaskTables> all = TaskTablesManager.getALl();
        List<String> ssl = new ArrayList<String>();
        Map<String,BuyerTaskList> taskbookMap =  TaskHelper.getBuyerTaskBook(all);
        for(TaskTables tt:all){
            if(tt.getBuyerTaskBookId() == 0){
                return ok(allnowbuyertask.render(0,allPrice,taskbookMap,ssl));
            }
        }
        for(Map.Entry<String,BuyerTaskList> iter:taskbookMap.entrySet()){
            taskNum += iter.getValue().getStlist().size();
            allPrice += iter.getValue().getZongBenJinNum();
            ssl.add(iter.getKey());
        }
        BigDecimal b2   =   new BigDecimal(allPrice);
        allPrice   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

        return ok(allnowbuyertask.render(taskNum,allPrice,taskbookMap,ssl));
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


    /** 分配任务(新版) */
    @Security.Authenticated(Secured.class)
    public Result dispatchTasks() {
        //解析入参
        Form<String> form = Form.form(String.class).bindFromRequest();
        List<TeamDispatchRule> teamDispatchRuleList = Lists.newArrayList();
        for (String key : form.data().keySet()) {
            Integer team = Integer.parseInt(key);
            Integer taskNum = Integer.parseInt(form.data().get(key));
            TeamDispatchRule teamDispatchRule = new TeamDispatchRule();
            teamDispatchRule.setTeam(team);
            teamDispatchRule.setTaskCount(taskNum);
            teamDispatchRuleList.add(teamDispatchRule);
        }

        //获取每个小组的刷手人数,计算平均每个刷手的任务数
        for (TeamDispatchRule teamDispatchRule: teamDispatchRuleList) {
            int buyerCount = BuyerManager.getBuyerCountByTeam(teamDispatchRule.getTeam()).intValue();
            teamDispatchRule.setBuyerCount(buyerCount);
            double taskCountPerBuyer = teamDispatchRule.getTaskCount() * 1.0 / buyerCount;
            teamDispatchRule.setTaskCountPerBuyer(taskCountPerBuyer);
        }

        //将每个组按刷手平均任务数，从大到小进行排序
        int sizeTeam = teamDispatchRuleList.size();
        for(int i = 0 ; i < sizeTeam-1; i ++)
        {
            for(int j = 0 ;j < sizeTeam-1-i ; j++)
            {
                //交换两个对象的位置
                if(teamDispatchRuleList.get(j).getTaskCountPerBuyer() < teamDispatchRuleList.get(j+1).getTaskCountPerBuyer())
                {
                    TeamDispatchRule temp = teamDispatchRuleList.get(j);
                    teamDispatchRuleList.set(j, teamDispatchRuleList.get(j+1));
                    teamDispatchRuleList.set(j+1 , temp);
                }
            }
        }

        //将刷手按照任务数从大到小进行排序,同一个组内的刷手按照序号从小到大进行排序
        List<models.entity.Buyer> allBuyerList = Lists.newArrayList();
        for (TeamDispatchRule teamDispatchRule: teamDispatchRuleList) {
            List<models.entity.Buyer> teamBuyerLists= BuyerManager.getALlByTeam(teamDispatchRule.getTeam());
            if (teamBuyerLists == null) {
                return ok("查询刷手列表出错!");
            }
            allBuyerList.addAll(teamBuyerLists);
        }


        //获取所有的商家任务书列表
        List<String> taskBookNameList = TaskTablesManager.getALlTaskBookName();
        if (taskBookNameList == null) {
            return ok("查询商家任务书出错!");
        }

        //获取每个商家任务书的任务数量
        List<TaskBookCount> shopTaskCountList = Lists.newArrayList();
        for (String taskBookName : taskBookNameList) {
            TaskBookCount shopTaskCount = new TaskBookCount();
            Long taskCount = TaskTablesManager.getTaskCountByTaskBookName(taskBookName);
            shopTaskCount.setTaskBookName(taskBookName);
            shopTaskCount.setTaskCount(taskCount);
            shopTaskCountList.add(shopTaskCount);
        }

        //把任务书按照任务数量从大到小排序
        int sizeShop = shopTaskCountList.size();
        for(int i = 0 ; i < sizeShop-1; i ++)
        {
            for(int j = 0 ;j < sizeShop-1-i ; j++)
            {
                //交换两个对象的位置
                if(shopTaskCountList.get(j).getTaskCount() < shopTaskCountList.get(j+1).getTaskCount())
                {
                    TaskBookCount temp = shopTaskCountList.get(j);
                    shopTaskCountList.set(j, shopTaskCountList.get(j+1));
                    shopTaskCountList.set(j+1 , temp);
                }
            }
        }

        //获取所有刷手的数量
        int buyerCount = BuyerManager.getBuyerCount().intValue();
        //先把任务总量计数器置为0
        long taskCount = 0;
        int lengthIndex = 0;
        int buyerIndex = 0;

        //依次获取任务书名称(按照任务数量从大到小)
        for (TaskBookCount taskBookCount: shopTaskCountList) {
            //获取店铺的所有任务
            List<TaskTables>  taskTablesList = TaskTablesManager.getTasksByTaskBookName(taskBookCount.getTaskBookName());
            if(taskTablesList == null) {
                return ok("查询任务列表出错!");
            }

            //每个任务分配刷手
            for (TaskTables taskTables: taskTablesList) {
                //按顺序找到有分配余量的刷手
                foundBuyer: for (; lengthIndex  < 20; lengthIndex++ ) {
                    for (buyerIndex %= buyerCount; buyerIndex < buyerCount; buyerIndex++) {
                        models.entity.Buyer nowBuyer = allBuyerList.get(buyerIndex);
                        int nowTeam = nowBuyer.getTeam();
                        TeamDispatchRule nowTeamDispatchRule = null;

                        //找到当前店铺对应的小组配置信息
                        for (TeamDispatchRule teamDispatchRule: teamDispatchRuleList) {
                            if(teamDispatchRule.team == nowTeam) {
                                nowTeamDispatchRule = teamDispatchRule;
                            }
                        }

                        //如果这个小组的任务分配额度还有余量,那么选中这个刷手
                        if (nowTeamDispatchRule.getTaskCountNow() <= nowTeamDispatchRule.getTaskCount()) {
                            //小组的已分配任务数+1
                            nowTeamDispatchRule.setTaskCountNow(nowTeamDispatchRule.getTaskCountNow() + 1);
                            //直接跳出寻找刷手的循环
                            break foundBuyer;
                        }
                    }
                }

                if(lengthIndex > 20) {
                    //如果lengthIndex循环到超过20,那一定是哪里出问题了
                    return ok("分配任务出错,请联系系统管理员!");
                }

                //查找这个刷手的详细信息
                models.entity.Buyer dispatchBuyer = allBuyerList.get(buyerIndex);

                //更新任务对应的刷手信息
                TaskTablesManager.setBuyerAndTaskBookId(taskTables.getTaskId(), dispatchBuyer.getWangwang(), dispatchBuyer.getTeam(), buyerIndex + 1);

                //任务总量计数器+1
                buyerIndex++;
                taskCount ++;
            }
        }

        //循环完毕后，判断任务总量是否正确
        if (taskCount != TaskTablesManager.getAllTaskCount()) {
            return ok("分配任务出错,请联系系统管理员!");
        }

        //锁定上传功能
        LockTableManager.update("TaskTables", 1);

        flash("dispatch_success", "分配完成！");
        return ok("success");
    }


    @Data
    public class TeamDispatchRule {
        //小组编号
        public int team;
        //需要分配的总任务数
        public int taskCount;
        //小组中的刷手数量
        public int buyerCount;
        //平均每个刷手的订单数量
        public double taskCountPerBuyer;
        //已分配任务数
        public int taskCountNow = 0;
    }


    @Data
    public class TaskBookCount {
        //商家任务书名称
        public String taskBookName;
        //任务数
        public long taskCount;
    }


    /** 清除任务 */
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
