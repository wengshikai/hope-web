package models.excel;

import lombok.Data;
import models.dbtable.TaskTables;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ExcelUtil;
import util.FileTool;
import util.ImageUtil;
import util.LocalStoreTool;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by shanmao on 15-12-16.
 */
@Data
public class ShopkeeperTaskBook {
    private String taskbookUuid;
    private String taskbookName;
    private List<ShopkeeperTaskList> tasklist = new ArrayList<ShopkeeperTaskList>();
    private Map<String,String> picMap = new HashMap<String,String>();
    private Map<String,byte[]> picContentMap = new HashMap<String,byte[]>();
    private Integer idIndex = 0;
    private Map<String,Integer> allline = new HashMap<String,Integer>();


    //前端打包有些bug,需要手动添加get函数
    public List<ShopkeeperTaskList> getTasklist() {
        return tasklist;
    }

    public String getTaskbookUuid() {
        return taskbookUuid;
    }


    public int getTaskNum(){
        int ret = 0;
        for(ShopkeeperTaskList task:tasklist){
            ret += task.getTaskNum();
        }
        return ret;
    }

    public double getTaskAllPriceSum(){
        double ret = 0;
        for(ShopkeeperTaskList task:tasklist){
            ret += task.getTaskAllPriceSum();
        }
        return util.Misc.formatDoubleForMoney(ret);
    }


    /** 解析商家任务书excel */
    public boolean parse(String dirPath) throws Exception {
        //用于记录报错信息
        String exceptionMessage = "";

        //获取目录下的第一个.xls文件
        List<String> list = FileTool.getFileListInDirectory(dirPath);
        String excelFile = "";
        for(String s:list){
            if(s.endsWith(".xls")) {
                excelFile = s;
                break;
            }
        }

        //随机生成一个uuid
        taskbookUuid = UUID.randomUUID().toString();

        //获取文件名
        try {
            String[] excelTmpStrings = excelFile.split(File.separator);
            taskbookName = excelTmpStrings[excelTmpStrings.length - 1];
        } catch(Exception e) {
            throw new Exception("读取文件名异常,文件夹:" + dirPath);
        }

        //获取图片
        try {
            List<String> picList = FileTool.getFileListInDirectory(dirPath + File.separator + "图片");
            for (String s : picList) {
                if (!ImageUtil.allowPicType(s)) {
                    continue;
                }
                String[] tmp1 = s.split(File.separator);
                String tmp2 = tmp1[tmp1.length - 1].split("\\.")[0];//.split(File.separator)[1].split("\\.");
                picMap.put(tmp2, tmp1[tmp1.length - 1]);
                try {
                    picContentMap.put(tmp1[tmp1.length - 1], FileTool.getFileContent(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new Exception("读取图片异常,文件夹:" + dirPath);
        }

        //解析excel表格
        try {
            POIFSFileSystem fs = new POIFSFileSystem(Files.newInputStream(Paths.get(excelFile)));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            List<Integer> taskListStart = new ArrayList<Integer>();
            List<Integer> taskListEnd = new ArrayList<Integer>();

            // 获取所有的表起始和结束
            for(int i=0;i<(sheet.getLastRowNum()+1);i++){
                if(!ExcelUtil.hasRow(sheet, i)){
                    continue;
                }
                String s = ExcelUtil.getCellString(sheet, i, 0);
                if(s!=null && s.equals("商家姓名")){
                    taskListStart.add(i);
                    if(i>0){
                        taskListEnd.add(i-1);
                    }
                }
            }
            taskListEnd.add(sheet.getLastRowNum());

            //一份excel下会有多个商品
            int tmpSubTaskBookId = 0;
            for(int i=0;i<taskListEnd.size();i++){
                ShopkeeperTaskList stl = new ShopkeeperTaskList();
                try {
                    //解析任务
                    idIndex += stl.parse(sheet, taskListStart.get(i), taskListEnd.get(i), taskbookUuid, taskbookName, picMap, idIndex, tmpSubTaskBookId);
                    tasklist.add(stl);
                } catch(Exception e) {
                    //e.printStackTrace();
                    //如果解析过程中有单元格解析失败,那么把报错信息添加到全局变量中
                    exceptionMessage = exceptionMessage + e.getMessage() + ";";
                }
                tmpSubTaskBookId++;
            }
            wb.close();
            fs.close();
        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }

        //如果有解析失败的报错,那么直接抛出
        if(!exceptionMessage.equals("")) {
            throw new Exception(exceptionMessage);
        }

        return true;
    }


    public void generateExcel(String excelName){
        allline.put("allline",0);
        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = ExcelUtil.getOrCreateSheet(wb, "sheet1");
        int index = 0;
        for(int i=1;i<tasklist.size();i++){
            for(int j=i;j>0;j--){
                if(tasklist.get(j).getTasklist().get(0).getId()<tasklist.get(j-1).getTasklist().get(0).getId()){
                    ShopkeeperTaskList a = tasklist.get(j);
                    ShopkeeperTaskList b = tasklist.get(j-1);
                    tasklist.set(j,b);
                    tasklist.set(j-1,a);
                }
            }
        }
        for(ShopkeeperTaskList stl:tasklist){
            index +=stl.generateExcel(sheet,index,allline,picContentMap);
        }

        try {
            Path p = Paths.get(excelName);
            OutputStream fileOut = Files.newOutputStream(p);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void dodoer(){
        for(int i=1;i<tasklist.size();i++){
            for(int j=i;j>0;j--){
                if(tasklist.get(j).getTasklist().get(0).getId()<tasklist.get(j-1).getTasklist().get(0).getId()){
                    ShopkeeperTaskList a = tasklist.get(j);
                    ShopkeeperTaskList b = tasklist.get(j-1);
                    tasklist.set(j,b);
                    tasklist.set(j-1,a);
                }
            }
        }
    }

    public void addPic(String k,byte[] v){
        picContentMap.put(k,v);
    }

    public void initByTask(ShopkeeperTaskList task){
        taskbookUuid = task.getTasklist().get(0).getTaskbookUuid();
        taskbookName = task.getTasklist().get(0).getTaskbookName();
        String pic = task.getPic1();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
        pic = task.getPic2();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
        pic = task.getPic3();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
    }

    public void addShopkeeperTaskList(ShopkeeperTaskList stl){
        tasklist.add(stl);
        String pic = stl.getPic1();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
        pic = stl.getPic2();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
        pic = stl.getPic3();
        if(pic!=null &&!pic.equals("")){
            picContentMap.put(pic, LocalStoreTool.getImage(taskbookUuid + pic));
        }
    }

    public static Map<String,ShopkeeperTaskBook> buildBookFromTask(List<TaskTables> ttlist){
        Map<String,ShopkeeperTaskList> stbmap = new HashMap<String,ShopkeeperTaskList>();

        for(TaskTables task:ttlist){
            String uuid = task.getTaskbookUuid()+task.getSubTaskbookId();
            ShopkeeperTask stk = new ShopkeeperTask();
            stk.initByTables(task);
            if(!stbmap.containsKey(uuid)){
                ShopkeeperTaskList stl = new ShopkeeperTaskList();
                stl.initByTask(stk);
                stbmap.put(uuid,stl);
            }
            ShopkeeperTaskList stl = stbmap.get(uuid);
            String pic = stk.getPic1();
            if(pic!=null &&!pic.equals("")){
                if(stl.getPic1() != null && !(stl.getPic1().equals(""))){
                    stl.setPic1(pic);
                }
            }
            pic = stk.getPic2();
            if(pic!=null &&!pic.equals("")){
                if(stl.getPic2() != null && !(stl.getPic2().equals(""))){
                    stl.setPic2(pic);
                }
            }
            pic = stk.getPic3();
            if(pic!=null &&!pic.equals("")){
                if(stl.getPic3() != null && !(stl.getPic3().equals(""))){
                    stl.setPic3(pic);
                }
            }
            stl.addShopkeeperTask(stk);
        }

        Map<String,ShopkeeperTaskBook> bookMap = new HashMap<String,ShopkeeperTaskBook>();
        for(Map.Entry<String,ShopkeeperTaskList> entry:stbmap.entrySet()){
            String uuid = entry.getValue().getTasklist().get(0).getTaskbookUuid();
            if(!bookMap.containsKey(uuid)){
                ShopkeeperTaskBook stb = new ShopkeeperTaskBook();
                stb.initByTask(entry.getValue());
                bookMap.put(uuid,stb);
            }
            ShopkeeperTaskBook stb = bookMap.get(uuid);
            stb.addShopkeeperTaskList(entry.getValue());
        }
        return bookMap;
    }
}
