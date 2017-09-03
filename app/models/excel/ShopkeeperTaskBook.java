package models.excel;

import models.dbtable.TaskTables;
import util.FileTool;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import util.ExcelUtil;
import util.ImageUtil;

import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.LocalStoreTool;

/**
 * Created by fengya on 15-12-16.
 */
public class ShopkeeperTaskBook {
    private String taskbookUuid;
    private String taskbookName;
    private List<ShopkeeperTaskList> tasklist = new ArrayList<ShopkeeperTaskList>();
    private Map<String,String> picMap = new HashMap<String,String>();
    private Map<String,byte[]> picContentMap = new HashMap<String,byte[]>();
    private Integer idIndex = 0;
    private Map<String,Integer> allline = new HashMap<String,Integer>();

    public String getTaskbookUuid() {
        return taskbookUuid;
    }

    public void setTaskbookUuid(String taskbookUuid) {
        this.taskbookUuid = taskbookUuid;
    }

    public String getTaskbookName() {
        return taskbookName;
    }

    public void setTaskbookName(String taskbookName) {
        this.taskbookName = taskbookName;
    }

    public List<ShopkeeperTaskList> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<ShopkeeperTaskList> tasklist) {
        this.tasklist = tasklist;
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

    public boolean parse(String dirpath){
        List<String> list = FileTool.getFileListInDirectory(dirpath);
        Set<String> imgNames = new HashSet<String>();
        String excelfile = "";
        for(String s:list){
            if(s.endsWith(".xls")) {
                excelfile = s;
                break;
            }
        }
        // scan pic

        taskbookUuid = UUID.randomUUID().toString();
        String[]  exceltmpstrings = excelfile.split(File.separator);
        taskbookName = exceltmpstrings[exceltmpstrings.length-1];

        List<String> picList = FileTool.getFileListInDirectory(dirpath+ File.separator+"图片");
        for(String s:picList){
            if(!ImageUtil.allowPicType(s)){
                continue;
            }
            String[] tmp1 = s.split(File.separator);
            String tmp2 = tmp1[tmp1.length-1].split("\\.")[0];//.split(File.separator)[1].split("\\.");
            picMap.put(tmp2,tmp1[tmp1.length-1]);
            try {
                picContentMap.put(tmp1[tmp1.length-1], FileTool.getFileContent(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            POIFSFileSystem fs = new POIFSFileSystem(Files.newInputStream(Paths.get(excelfile)));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            List<Integer> taskliststart = new ArrayList<Integer>();
            List<Integer> tasklistend = new ArrayList<Integer>();

            // 获取所有的表起始和结束
            for(int i=0;i<(sheet.getLastRowNum()+1);i++){
                if(!ExcelUtil.hasRow(sheet, i)){
                    continue;
                }
                String s = ExcelUtil.getCellString(sheet, i, 0);
                if(s!=null && s.equals("商家姓名")){
                    taskliststart.add(i);
                    if(i>0){
                        tasklistend.add(i-1);
                    }
                }
            }
            tasklistend.add(sheet.getLastRowNum());

            int tmpsubtaskbookid = 0;
            for(int i=0;i<tasklistend.size();i++){
                ShopkeeperTaskList stl = new ShopkeeperTaskList();
                idIndex += stl.parse(sheet,taskliststart.get(i),tasklistend.get(i),taskbookUuid,taskbookName,picMap,idIndex,tmpsubtaskbookid);
                tasklist.add(stl);
                tmpsubtaskbookid++;
            }
            wb.close();
            fs.close();

        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for(ShopkeeperTaskList stl: tasklist){
            sb.append(stl.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    public static void main(String argv[]){
        ShopkeeperTaskBook shopkeeperTaskBook = new ShopkeeperTaskBook();
        shopkeeperTaskBook.parse("/Users/fengya/Downloads/10151216/12.17商家任务书/hodo红豆宾慕专卖店/");
        System.out.print(shopkeeperTaskBook.toString());

    }

    public Map<String, byte[]> getPicContentMap() {
        return picContentMap;
    }

    public void setPicContentMap(Map<String, byte[]> picContentMap) {
        this.picContentMap = picContentMap;
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
