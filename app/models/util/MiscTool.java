package models.util;

import com.google.common.collect.Lists;
import models.dbmanager.BuyerManager;
import models.dbtable.Buyer;
import models.dbtable.CombineShopBuyer;
import models.dbtable.TaskTables;
import models.excel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
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

/**
 * Created by shanmao on 15-12-10.
 */
public class MiscTool {

    public static byte[] buildDownloadTaskZip(List<TaskTables> all){
        FileTool.deleteDirectory("exceltmp");
        FileTool.createDestDirectoryIfNotExists("exceltmp/");
        buildDownloadShuashouZip(all, "刷手.zip");
        buildTeamCollectionZip(all, "小组汇总.zip");
        buildDownloadShopkeeperZip(all, "商家.zip");
        String[] ss = new String[4];
        ss[0] = "刷手.zip";
        ss[1] = "小组汇总.zip";
        ss[2] = "商家.zip";
        ss[3] = "账单.txt";
        ZIPTool.compressFiles2Zip(ss, "task.zip");
        try {
            byte[] ret = FileTool.getFileContent("task.zip");
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            FileTool.delete("刷手.zip");
            FileTool.delete("小组汇总.zip");
            FileTool.delete("商家.zip");
            FileTool.delete("账单.txt");
            FileTool.delete("task.zip");
        }
    }


    /** 生成所有刷手任务书(按刷手维度分表) */
    public static void buildDownloadShuashouZip(List<TaskTables> all,String zipname){
        List<models.dbtable.Buyer> ssl = BuyerManager.getALl();
        Map<String,Integer> levelMap = new HashMap();
        Map<String,String> wangwang2shopName = new HashMap<String,String>();
        Map<String,Integer> wangwang2shopId = new HashMap<String,Integer>();
        for(TaskTables taskTables:all){
            wangwang2shopName.put(taskTables.getShopWangwang(),taskTables.getShopName());
            wangwang2shopId.put(taskTables.getShopWangwang(),taskTables.getShopId());
        }
        for(models.dbtable.Buyer buyer:ssl){
            levelMap.put(buyer.getWangwang(),buyer.getLevel());
        }
        Map<String,String> levelArrayMap = new HashMap();

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
                sst.addPic(LocalStoreTool.getImage(task.getTaskBookUuid()+pic1),pic1.split("\\.")[1]);
            }
            if(pic2!=null&&!pic2.equals("")){
                sst.addPic(LocalStoreTool.getImage(task.getTaskBookUuid()+pic2),pic2.split("\\.")[1]);
            }
            if(pic3!=null&&!pic3.equals("")){
                sst.addPic(LocalStoreTool.getImage(task.getTaskBookUuid()+pic3),pic3.split("\\.")[1]);
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
                //生成任务书excel文件
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


        /** 生成汇总账单 */
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
        sb.append("分组\t旺旺\t店铺\t商家编号\t总单数\t总金额\n");
        for(Map.Entry<String,Map<String,List<Double>>> entry:money.entrySet()){
            String group = entry.getKey();
            Map<String,List<Double>>  loop2 = entry.getValue();
            for(Map.Entry<String,List<Double>> entry2:loop2.entrySet()){
                sb.append(group);
                sb.append("\t");
                sb.append(entry2.getKey());
                sb.append("\t");
                sb.append(wangwang2shopName.get(entry2.getKey()));
                sb.append("\t");
                sb.append(wangwang2shopId.get(entry2.getKey()));
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
            e.printStackTrace();
        }
    }


    /** 生成小组汇总表的压缩包 */
    public static void buildTeamCollectionZip(List<TaskTables> allTaskTables, String zipName) {
        List<String> fileNames = Lists.newArrayList();
        buildTeamCollectionExcel(allTaskTables, "A", fileNames);
        buildTeamCollectionExcel(allTaskTables, "B", fileNames);
        //打包
        MiscTool.buildDownloadZip(fileNames, zipName);
    }

    /** 生成小组汇总表  type=1:填写价格; type=2:不填写价格*/
    public static void buildTeamCollectionExcel(List<TaskTables> allTaskTables, String type, List<String> fileNames) {
        //获取所有的刷手分组
        List<Integer> buyerTeams =  BuyerManager.getALlTeams();
        for (Integer team: buyerTeams) {
            //获取每个小组内的刷手列表
            List<Buyer> buyerLists = BuyerManager.getALlByTeam(team);

            Workbook wb = new XSSFWorkbook();
            Sheet sheet1 = ExcelUtil.getOrCreateSheet(wb, "sheet1");
            Sheet sheet2 = ExcelUtil.getOrCreateSheet(wb, "sheet2");

            //第一行填写标题,第二行填写任务书编号
            ExcelUtil.getOrCreateCell(sheet1,0,0).setCellValue("旺旺昵称");
            ExcelUtil.getOrCreateCell(sheet1,1,0).setCellValue("任务书编号");
            //这几个单元格的背景调整为黄色
            ExcelUtil.setRegionColor(sheet1, IndexedColors.YELLOW.getIndex(), 0, 0, 0, 1);

            //第一行填写刷手旺旺名
            for (int index=0; index<buyerLists.size(); index++) {
                ExcelUtil.getOrCreateCell(sheet1, 0, index+1).setCellValue(buyerLists.get(index).getWangwang());
            }

            //从第行开始填写商家信息
            Map<String,Integer> shopIds = new HashMap<>();
            Integer shopIndex = 0;
            for(TaskTables taskTables: allTaskTables) {
                for (int buyerIndex = 0; buyerIndex < buyerLists.size(); buyerIndex ++) {
                    Buyer buyer = buyerLists.get(buyerIndex);
                    if (taskTables.getBuyerWangwang().equals(buyer.getWangwang())) {
                        //如果这个商家id没有被加到索引中，那么添加一下
                        if(!shopIds.containsKey(taskTables.getShopName())) {
                            shopIds.put(taskTables.getShopName(), 0);  //value代表店铺对应的刷手数
                            //sheet1第一列从第三行开始填写店铺名称
                            ExcelUtil.getOrCreateCell(sheet1,shopIndex+2,0).setCellValue(taskTables.getShopName());

                            //sheet2填写店铺信息
                            ExcelUtil.getOrCreateCell(sheet2,0,shopIndex*2).setCellType(Cell.CELL_TYPE_FORMULA);
                            ExcelUtil.getOrCreateCell(sheet2,0,shopIndex*2).setCellFormula("Sheet1!A" + (shopIndex+3));
                            //背景调整为黄色
                            ExcelUtil.setRegionColor(sheet2, IndexedColors.YELLOW.getIndex(), shopIndex*2, 0, shopIndex*2, 0);
                            shopIndex++;
                        }
                        //sheet1第二行填写店铺任务书编号,会重复填写,临时方案,待优化
                        ExcelUtil.getOrCreateCell(sheet1,1,buyerIndex+1).setCellValue(taskTables.getBuyerTaskBookId());
                        if(type.equals("A")) {
                            //sheet1填写商家-刷手对应单元格的价格
                            ExcelUtil.getOrCreateCell(sheet1, shopIndex+1, buyerIndex + 1).setCellValue(taskTables.getAllPrice());
                        } else if(type.equals("B")) {
                            //sheet1填写1
                            ExcelUtil.getOrCreateCell(sheet1, shopIndex+1, buyerIndex + 1).setCellValue(1);
                        }
                        //店铺对应的刷手数+1
                        shopIds.put(taskTables.getShopName(), shopIds.get(taskTables.getShopName()) + 1);
                        //sheet2填写刷手旺旺名和价格
                        ExcelUtil.getOrCreateCell(sheet2, shopIds.get(taskTables.getShopName()), shopIndex*2-2).setCellType(Cell.CELL_TYPE_FORMULA);
                        ExcelUtil.getOrCreateCell(sheet2, shopIds.get(taskTables.getShopName()), shopIndex*2-2).setCellFormula("Sheet1!" + ExcelUtil.getColumnLabels(buyerIndex+1) + "1");
                        ExcelUtil.getOrCreateCell(sheet2, shopIds.get(taskTables.getShopName()), shopIndex*2-1).setCellType(Cell.CELL_TYPE_FORMULA);
                        ExcelUtil.getOrCreateCell(sheet2, shopIds.get(taskTables.getShopName()), shopIndex*2-1).setCellFormula("Sheet1!" + ExcelUtil.getColumnLabels(buyerIndex+1) + (shopIndex+2));
                    }
                }
            }

            //sheet1获取商家的总数
            int shopCount = shopIds.size();
            //sheet1最后几行填写刷手的汇总信息
            ExcelUtil.getOrCreateCell(sheet1, shopCount+2,0).setCellValue("合计单数");
            ExcelUtil.getOrCreateCell(sheet1, shopCount+3,0).setCellValue("合计货款");
            ExcelUtil.getOrCreateCell(sheet1, shopCount+4,0).setCellValue("单笔佣金");
            ExcelUtil.getOrCreateCell(sheet1, shopCount+5,0).setCellValue("合计佣金");
            ExcelUtil.getOrCreateCell(sheet1, shopCount+6,0).setCellValue("合计打款金额");
            //sheet1这几个单元格的背景调整为绿色
            ExcelUtil.setRegionColor(sheet1, IndexedColors.GREEN.getIndex(), 0, shopCount+2, 0, shopCount+6);
            for (int columnNum = 0; columnNum < buyerLists.size(); columnNum++) {
                //获取列序号对应的字母
                String columnStr = ExcelUtil.getColumnLabels(columnNum+1);
                //sheet1合计单数
                ExcelUtil.getOrCreateCell(sheet1,shopCount+2, columnNum+1).setCellType(Cell.CELL_TYPE_FORMULA);
                ExcelUtil.getOrCreateCell(sheet1,shopCount+2, columnNum+1).setCellFormula("COUNT(" + columnStr + "3:" + columnStr + (shopCount+2) + ")");
                //sheet1合计货款
                ExcelUtil.getOrCreateCell(sheet1,shopCount+3, columnNum+1).setCellType(Cell.CELL_TYPE_FORMULA);
                ExcelUtil.getOrCreateCell(sheet1,shopCount+3, columnNum+1).setCellFormula("SUM(" + columnStr + "3:" + columnStr + (shopCount+2) + ")");
                //sheet1合计佣金
                ExcelUtil.getOrCreateCell(sheet1,shopCount+5, columnNum+1).setCellType(Cell.CELL_TYPE_FORMULA);
                ExcelUtil.getOrCreateCell(sheet1,shopCount+5, columnNum+1).setCellFormula(columnStr + (shopCount+3) + "*" + columnStr + (shopCount+5));
                //sheet1合计打款金额
                ExcelUtil.getOrCreateCell(sheet1,shopCount+6, columnNum+1).setCellType(Cell.CELL_TYPE_FORMULA);
                ExcelUtil.getOrCreateCell(sheet1,shopCount+6, columnNum+1).setCellFormula(columnStr + (shopCount+4) + "+" + columnStr + (shopCount+6));
            }

            //sheet1最后一列商家汇总信息
            for (int row = 0; row < shopCount + 5; row++) {
                //单笔佣金不汇总
                if (row == shopCount+2) {
                    continue;
                }

                //合计单数
                ExcelUtil.getOrCreateCell(sheet1, row+2, buyerLists.size()+1).setCellType(Cell.CELL_TYPE_FORMULA);
                String columnStrBegin = ExcelUtil.getColumnLabels(1);
                String columnStrEnd = ExcelUtil.getColumnLabels(buyerLists.size());
                ExcelUtil.getOrCreateCell(sheet1,row+2, buyerLists.size()+1).setCellFormula("SUM(" + columnStrBegin + (row+3) + ":" + columnStrEnd + (row+3) + ")");
            }

            try {
                String fileName = "exceltmp/小组汇总" + team + type + ".xls";
                fileNames.add(fileName);
                Path p = Paths.get(fileName);
                OutputStream fileOut = Files.newOutputStream(p);
                wb.write(fileOut);
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /** 生成所有刷手任务书(按商家维度分配) */
    public static void buildDownloadShopkeeperZip(List<TaskTables> all,String zipname){
        Map<String,ShopkeeperTaskList> stbmap = new HashMap<String,ShopkeeperTaskList>();
        for(TaskTables task:all){
            String uuid = task.getTaskBookUuid()+task.getSubTaskBookId();
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
            String uuid = entry.getValue().getTasklist().get(0).getTaskBookUuid();
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
            //生成刷手任务书(商家维度)
            stb.generateExcel("exceltmp/"+stb.getTaskBookName());
            names.add("exceltmp/"+stb.getTaskBookName());
        }

        ZIPTool.compressFiles2Zip(names.toArray(new String[names.size()]), zipname);
        for(String s:names){
            FileTool.delete(s);
        }
    }


    /** 生成商家刷手映射表 */
    public static String buildCombineShopBuyerExcel(List<CombineShopBuyer> combineShopBuyerList) throws Exception {
        Workbook wb = new XSSFWorkbook();
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
        String excelName = "combineExcelTmp/" + dateToday + "+"
                + combineShopBuyerList.get(0).getShopName() + "+"
                + combineShopBuyerList.size() + "+"
                + AmountUtil.changeF2Y(String.valueOf(price)) + ".xls";

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


    /** 生成压缩包 */
    public static byte[] buildDownloadZip(List<String> excelNameList, String zipName) {
        ZIPTool.compressFiles2Zip(excelNameList.toArray(new String[excelNameList.size()]), zipName);
        excelNameList.forEach(FileTool::delete);

        try {
            return FileTool.getFileContent(zipName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 生成商家汇总表 */
    public static void buildShopCollectionExcel(List<ShopCollectionExcel> shopCollectionExcels, String zipName) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = ExcelUtil.getOrCreateSheet(wb, "sheet1");

        //第一列填写标题
        ExcelUtil.getOrCreateCell(sheet,0,0).setCellValue("店铺名");
        ExcelUtil.getOrCreateCell(sheet,0,1).setCellValue("总单数");
        ExcelUtil.getOrCreateCell(sheet,0,2).setCellValue("总金额");

        for (int index=0; index<shopCollectionExcels.size(); index++) {
            ExcelUtil.getOrCreateCell(sheet,index+1,0).setCellValue(shopCollectionExcels.get(index).getShopName());
            ExcelUtil.getOrCreateCell(sheet,index+1,1).setCellValue(shopCollectionExcels.get(index).getOrderNum());
            ExcelUtil.getOrCreateCell(sheet,index+1,2).setCellValue(AmountUtil.changeF2Y(String.valueOf(shopCollectionExcels.get(index).getTotalAmount())));
        }

        try {
            //生成xls文件
            Path p = Paths.get(zipName);
            OutputStream fileOut = Files.newOutputStream(p);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
