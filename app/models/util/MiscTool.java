package models.util;

import models.dbtable.CombineShopBuyer;
import models.dbtable.TaskTables;
import models.excel.BuyerTask;
import models.excel.ShopkeeperTask;
import models.excel.ShopkeeperTaskBook;
import models.excel.ShopkeeperTaskList;
import models.excel.BuyerTaskList;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


import models.BuyerManager;

/**
 * Created by weng on 15-12-10.
 */
public class MiscTool {

    public static byte[] buildDownloadBuyerZip(List<TaskTables> all){
        buildDownloadShuashouZip(all, "刷手.zip");
        try {
            return FileTool.getFileContent("刷手.zip");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            FileTool.delete("刷手.zip");
        }
    }

    public static byte[] buildDownloadShopkeeperZip(List<TaskTables> all){
        buildDownloadShopkeeperZip(all, "商家.zip");
        try {
            return FileTool.getFileContent("商家.zip");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            FileTool.delete("商家.zip");
        }
    }



    public static byte[] buildDownloadTaskZip(List<TaskTables> all){
        buildDownloadShuashouZip(all, "刷手.zip");
        buildDownloadShopkeeperZip(all, "商家.zip");
        String[] ss = new String[3];
        ss[0] = "刷手.zip";
        ss[1] = "商家.zip";
        ss[2] = "账单.txt";
        ZIPTool.compressFiles2Zip(ss, "task.zip");
        try {
            byte[] ret = FileTool.getFileContent("task.zip");
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            FileTool.delete("刷手.zip");
            FileTool.delete("商家.zip");
            FileTool.delete("task.zip");
            FileTool.delete("账单.txt");
        }
    }

    public static void buildDownloadShuashouZip(List<TaskTables> all,String zipname){
        List<models.dbtable.Buyer> ssl = BuyerManager.getALl();
        Map<String,Integer> levelMap = new HashMap();
        Map<String,String> wangwang2shopname = new HashMap<String,String>();
        for(TaskTables taskTables:all){
            wangwang2shopname.put(taskTables.getShopWangwang(),taskTables.getShopName());
        }
        for(models.dbtable.Buyer buyer:ssl){
            levelMap.put(buyer.getWangwang(),buyer.getLevel());
        }
        Map<String,String> levelArrayMap = new HashMap();

        FileTool.deleteDirectory("exceltmp");
        FileTool.createDestDirectoryIfNotExists("exceltmp/");
        Map<Integer,String> idtobuyer = new HashMap<Integer,String>();
        Map<Integer,BuyerTaskList> stlmap = new HashMap<Integer,BuyerTaskList>();
        for(TaskTables task:all){
            idtobuyer.put(task.getBuyerTaskBookId(),task.getBuyerWangwang());
            Integer sbid = task.getBuyerTaskBookId();
            if(!stlmap.containsKey(sbid)){
                BuyerTaskList stl = new BuyerTaskList();
                stl.setFilepath("exceltmp/"+sbid+".xls");
                stl.setTaskbookid(sbid);
                stlmap.put(sbid,stl);
            }
            BuyerTaskList stl = stlmap.get(sbid);
            BuyerTask sst = new BuyerTask();
            sst.setShopname(task.getShopName());
            sst.setGoodnum(task.getGoodsNumber());
            sst.setUnit_price(task.getUnitPrice());
            sst.setAllPrice(task.getAllPrice());
            sst.setKeyword(task.getKeyword());
            String pic1 = task.getPic1();
            String pic2 = task.getPic2();
            String pic3 = task.getPic3();
            if(pic1!=null&&!pic1.equals("")){
                sst.addPic(LocalStoreTool.getImage(task.getTaskbookUuid()+pic1),pic1.split("\\.")[1]);
            }
            if(pic2!=null&&!pic2.equals("")){
                sst.addPic(LocalStoreTool.getImage(task.getTaskbookUuid()+pic2),pic2.split("\\.")[1]);
            }
            if(pic3!=null&&!pic3.equals("")){
                sst.addPic(LocalStoreTool.getImage(task.getTaskbookUuid()+pic3),pic3.split("\\.")[1]);
            }
            sst.setRequirement(task.getTaskRequirement());
            sst.setPhoneCost(task.getPhoneCost());
            stl.addShuashouTask(sst);

        }
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> names_realname = new ArrayList<String>();
        for (Map.Entry<Integer,BuyerTaskList> entry: stlmap.entrySet()) {
            names.add("exceltmp/"+entry.getKey()+".xls");
            BuyerTaskList sstl = (BuyerTaskList)entry.getValue();
            BigDecimal   b1   =   new BigDecimal(sstl.getZongBenJinNum());
            double   f1bj   =   b1.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

            double benjin = +sstl.getZongBenJinNum() + sstl.getZongYongjinNum();
            BigDecimal   b2   =   new BigDecimal(benjin);
            double   f2benjin   =   b2.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

            String real_n =  entry.getKey()+"号-共"+sstl.getZongDanShuNum()+"单-"+f1bj+
                    "+"+sstl.getZongYongjinNum()+"="+f2benjin+"元-"+idtobuyer.get(entry.getKey())+".xls";
            names_realname.add("exceltmp/"+real_n);
            levelArrayMap.put("exceltmp/"+real_n,""+levelMap.get(idtobuyer.get(entry.getKey())));
            try {
                ((BuyerTaskList)entry.getValue()).Deal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<names.size();i++){
            try {
                Files.move(Paths.get(names.get(i)),Paths.get(names_realname.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String,List<String>> levelArrayList = new HashMap();
        for(Map.Entry<String, String> entry : levelArrayMap.entrySet()){
            if(levelArrayList.get(entry.getValue()) == null){
                levelArrayList.put(entry.getValue(),new ArrayList<String>());
            }
            levelArrayList.get(entry.getValue()).add(entry.getKey());
        }
        List<String> lastarray = new ArrayList();
        for(Map.Entry<String, List<String>> entry : levelArrayList.entrySet()){
            List<String> tmpl = entry.getValue();
            ZIPTool.compressFiles2Zip(tmpl.toArray(new String[tmpl.size()]), entry.getKey()+".zip");
            lastarray.add(entry.getKey()+".zip");
        }

        ZIPTool.compressFiles2Zip(lastarray.toArray(new String[lastarray.size()]), zipname);
        for(String s:names_realname){
            FileTool.delete(s);
        }
        for(String s:lastarray){
            FileTool.delete(s);
        }

        Map<String,Map<String,List<Double>>> money = new HashMap<String,Map<String,List<Double>>>();

        for(TaskTables taskTables:all){
            Integer index1 = levelMap.get(taskTables.getBuyerWangwang());
            if(money.get(""+index1) == null){
                Map<String,List<Double>> map1 = new HashMap<String,List<Double>>();
                money.put(""+index1,map1);
            }
            String  sw = taskTables.getShopWangwang();
            if(money.get(""+index1).get(sw) == null){
                money.get(""+index1).put(sw,new ArrayList<Double>());
            }
            money.get(""+index1).get(sw).add(taskTables.getAllPrice());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("分组\t旺旺\t店铺\t总单数\t总金额\n");
        for(Map.Entry<String,Map<String,List<Double>>> entry:money.entrySet()){
            String group = entry.getKey();
            Map<String,List<Double>>  loop2 = entry.getValue();
            for(Map.Entry<String,List<Double>> entry2:loop2.entrySet()){
                sb.append(group);
                sb.append("\t");
                sb.append(entry2.getKey());
                sb.append("\t");
                sb.append(wangwang2shopname.get(entry2.getKey()));
                sb.append("\t");
                sb.append(entry2.getValue().size());
                sb.append("\t");
                double tmpd = 0.0;
                List<Double> tmpdl = entry2.getValue();
                for(Double d:tmpdl){
                    tmpd = tmpd + d;
                }
                sb.append(""+ Misc.formatDoubleForMoney(tmpd));
                sb.append("\n");
            }
        }
        try{
            FileTool.putFileContent("账单.txt",sb.toString().getBytes());
        }catch (Exception e){
            
        }

    }

    public static void buildDownloadShopkeeperZip(List<TaskTables> all,String zipname){
        FileTool.deleteDirectory("exceltmp");
        FileTool.createDestDirectoryIfNotExists("exceltmp/");
        Map<String,ShopkeeperTaskList> stbmap = new HashMap<String,ShopkeeperTaskList>();
        for(TaskTables task:all){
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
                    stl.setPic1(pic);
                }
            }
            pic = stk.getPic3();
            if(pic!=null &&!pic.equals("")){
                if(stl.getPic3() != null && !(stl.getPic3().equals(""))){
                    stl.setPic1(pic);
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

        List<String> names = new ArrayList<String>();
        for(Map.Entry<String,ShopkeeperTaskBook> entry:bookMap.entrySet()){
            ShopkeeperTaskBook stb = entry.getValue();
            stb.generateExcel("exceltmp/"+stb.getTaskbookName());
            names.add("exceltmp/"+stb.getTaskbookName());
        }

        ZIPTool.compressFiles2Zip(names.toArray(new String[names.size()]), zipname);
        for(String s:names){
            FileTool.delete(s);
        }
    }


    /** 生成商家刷手映射表 */
    public static String buildCombineShopBuyerExcel(List<CombineShopBuyer> combineShopBuyerList) throws Exception {
        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = ExcelUtil.getOrCreateSheet(wb, "sheet1");

        //第一列第一行填写店铺名
        ExcelUtil.getOrCreateCell(sheet,0,0).setCellValue(combineShopBuyerList.get(0).getShopName());
        int price = 0;
        for (int index=0; index<combineShopBuyerList.size(); index++) {
            //填写买家旺旺名
            ExcelUtil.getOrCreateCell(sheet,index+1,0).setCellValue(combineShopBuyerList.get(index).getBuyerWangwang());
            //填写金额(数据库存的是分,转化为元)
            ExcelUtil.getOrCreateCell(sheet,index+1,1).setCellValue(AmountUtil.changeF2Y(new Long(combineShopBuyerList.get(index).getPrice())));
            //总金额累加
            price += combineShopBuyerList.get(index).getPrice();
        }

        //生成xls文件
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String dateToday = sdf.format(d);
        String excelName = "combineExcelTmp/" + dateToday + combineShopBuyerList.get(0).getShopName()
                + "+" + combineShopBuyerList.size()
                + "+" + AmountUtil.changeF2Y(String.valueOf(price)) + ".xls";

        try {
            Path p = Paths.get(excelName);
            OutputStream fileOut = Files.newOutputStream(p);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelName;
    }


    /** 生成商家刷手映射表的压缩包 */
    public static byte[] buildDownloadCombineZip(List<String> excelNameList, String zipName) {
        ZIPTool.compressFiles2Zip(excelNameList.toArray(new String[excelNameList.size()]), zipName);
        excelNameList.forEach(FileTool::delete);

        try {
            byte[] ret = FileTool.getFileContent(zipName);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            FileTool.delete(zipName);
        }
    }
}
