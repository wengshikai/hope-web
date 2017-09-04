package models.excel;

import models.excel.ShopkeeperTask;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.*;
import util.ExcelUtil;
import util.ImageUtil;

/**
 * Created by weng on 15-12-10.
 */
public class ShopkeeperTaskList {

    private List<ShopkeeperTask> tasklist = new ArrayList<ShopkeeperTask>();
    private String shopkeeperName;
    private Map<String,byte[]> picContent = new HashMap<String,byte[]>();
    private Map<String,String> picType = new HashMap<String,String>();
    private Map<String,int[]> picSize = new HashMap<String,int[]>();
    private String shopName;
    private String shopWangwang;
    private String itemLink;
    private double pcCost;
    private double phoneCost;
    private String pic1 = "";
    private String pic2 = "";
    private String pic3 = "";
    private int subTaskBookid;

    public int getTaskNum(){
        int ret = 0;
        for(ShopkeeperTask task:tasklist){
            ret += 1;
        }
        return ret;
    }

    public double getTaskAllPriceSum(){
        double ret = 0;
        for(ShopkeeperTask task:tasklist){
            ret += task.getAllPrice();
        }
        return ret;
    }

    public int parse(HSSFSheet sheet,int start,int end,String taskbookUuid,String taskbookName,
                     Map<String,String> picMap,Integer idIndex,int subtaskbookid){

        shopkeeperName = ExcelUtil.getCellString(sheet,start,1);
        shopName  = ExcelUtil.getCellString(sheet,start,3);
        shopWangwang = ExcelUtil.getCellString(sheet,start,5);
        itemLink = ExcelUtil.getCellString(sheet,start+1,1);
        pcCost = ExcelUtil.getCellDouble(sheet, start + 2, 1);
        phoneCost = ExcelUtil.getCellDouble(sheet,start+3,1);
        String pic = ExcelUtil.getCellString(sheet,start+4,1);
        pic1 = picMap.get(pic);
        pic = ExcelUtil.getCellString(sheet,start+5,1);
        pic2 = picMap.get(pic);
        pic = ExcelUtil.getCellString(sheet,start+6,1);
        pic3 = picMap.get(pic);
        this.subTaskBookid = subtaskbookid;



        int index = 0;
        for(int i=start+8;i<=end;i++){
            if(!ExcelUtil.hasRow(sheet,i)){
                continue;
            }
            if(ExcelUtil.getCellDouble(sheet,i,4)==null){
                continue;
            }
            index++;
            ShopkeeperTask st = new ShopkeeperTask();
            st.setId(index+idIndex);
            st.setKeyword(ExcelUtil.getCellString(sheet, i, 1));
            st.setTaskRequirement(ExcelUtil.getCellString(sheet, i, 2));
            st.setUnitPrice(ExcelUtil.getCellDouble(sheet, i, 3));
            st.setGoodsNumber(ExcelUtil.getCellInt(sheet, i, 4));
            if(ExcelUtil.getCellDouble(sheet, i, 5) != null){
                st.setAllPrice(ExcelUtil.getCellDouble(sheet, i, 5));
            }else{
                Cell cell =  sheet.getRow(i).getCell(5);
                if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
                    st.setAllPrice(cell.getNumericCellValue());
                }else{
                    st.setAllPrice(ExcelUtil.getCellDouble(sheet, i, 3)*ExcelUtil.getCellDouble(sheet, i, 4));
                }

            }
            st.setTaskbookUuid(taskbookUuid);
            st.setTaskbookName(taskbookName);
            st.setShopkeeperName(shopkeeperName);
            st.setShopName(shopName);
            st.setShopWangwang(shopWangwang);
            st.setItemLink(itemLink);
            st.setPcCost(pcCost);
            st.setPhoneCost(phoneCost);
            st.setPic1(pic1);
            st.setPic2(pic2);
            st.setPic3(pic3);
            st.setSubTaskBookId(subtaskbookid);
            tasklist.add(st);
        }

        return index;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for(ShopkeeperTask st:tasklist){
            sb.append(st.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<ShopkeeperTask> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<ShopkeeperTask> tasklist) {
        this.tasklist = tasklist;
    }

    public String getShopkeeperName() {
        return shopkeeperName;
    }

    public void setShopkeeperName(String shopkeeperName) {
        this.shopkeeperName = shopkeeperName;
    }

    public Map<String, byte[]> getPicContent() {
        return picContent;
    }

    public void setPicContent(Map<String, byte[]> picContent) {
        this.picContent = picContent;
    }

    public Map<String, String> getPicType() {
        return picType;
    }

    public void setPicType(Map<String, String> picType) {
        this.picType = picType;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopWangwang() {
        return shopWangwang;
    }

    public void setShopWangwang(String shopWangwang) {
        this.shopWangwang = shopWangwang;
    }

    public String getItemLink() {
        return itemLink;
    }

    public void setItemLink(String itemLink) {
        this.itemLink = itemLink;
    }

    public double getPcCost() {
        return pcCost;
    }

    public void setPcCost(double pcCost) {
        this.pcCost = pcCost;
    }

    public double getPhoneCost() {
        return phoneCost;
    }

    public void setPhoneCost(double phoneCost) {
        this.phoneCost = phoneCost;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public int generateExcel(Sheet sheet,int top,Map<String,Integer> allline,Map<String,byte[]> picContentMap){
        int prelinenum = allline.get("allline");
        ExcelUtil.getOrCreateCell(sheet,top+0,0).setCellValue("商家姓名");
        ExcelUtil.getOrCreateCell(sheet,top+0,1).setCellValue(shopkeeperName);
        ExcelUtil.getOrCreateCell(sheet,top+0,2).setCellValue("店铺名称");
        ExcelUtil.getOrCreateCell(sheet,top+0,3).setCellValue(shopName);
        ExcelUtil.getOrCreateCell(sheet,top+0,4).setCellValue("店铺旺旺");
        ExcelUtil.getOrCreateCell(sheet,top+0,5).setCellValue(shopWangwang);
        ExcelUtil.getOrCreateCell(sheet,top+1,0).setCellValue("产品链接");
        CellStyle hlink_style = sheet.getWorkbook().createCellStyle();
        Font hlink_font = sheet.getWorkbook().createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);

        if(itemLink != null){
            Cell cell = ExcelUtil.getOrCreateCell(sheet, top+1, 1);
            cell.setCellValue(itemLink);
            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
            Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
            link.setAddress(itemLink);
            cell.setHyperlink(link);
            cell.setCellStyle(hlink_style);
        }

        ExcelUtil.getOrCreateCell(sheet,top+2,0).setCellValue("电脑端价格");
        ExcelUtil.getOrCreateCell(sheet,top+2,1).setCellValue(pcCost);
        ExcelUtil.getOrCreateCell(sheet,top+3,0).setCellValue("手机端价格");
        ExcelUtil.getOrCreateCell(sheet,top+3,1).setCellValue(phoneCost);

        ExcelUtil.getOrCreateCell(sheet,top+7,0).setCellValue("序号");
        ExcelUtil.getOrCreateCell(sheet,top+7,1).setCellValue("关键词（手机淘宝搜索）");
        ExcelUtil.getOrCreateCell(sheet,top+7,2).setCellValue("任务要求");
        ExcelUtil.getOrCreateCell(sheet,top+7,3).setCellValue("单价");
        ExcelUtil.getOrCreateCell(sheet,top+7,4).setCellValue("数量");
        ExcelUtil.getOrCreateCell(sheet,top+7,5).setCellValue("总价");
        ExcelUtil.getOrCreateCell(sheet,top+7,6).setCellValue("刷手旺旺账号");
        ExcelUtil.getOrCreateCell(sheet,top+7,7).setCellValue("任务书");
        for(ShopkeeperTask st:tasklist){
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,0).setCellValue(st.getId());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,1).setCellValue(st.getKeyword());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,2).setCellValue(st.getTaskRequirement());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,3).setCellValue(st.getUnitPrice());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,4).setCellValue(st.getGoodsNumber());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,5).setCellValue(st.getAllPrice());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,6).setCellValue(st.getBuyerWangwang());
            ExcelUtil.getOrCreateCell(sheet,top+7+st.getId()-prelinenum,7).setCellValue(st.getBuyerTaskBookId());
        }


        //放图片
//        System.out.println(picContentMap);
//        System.out.println(pic1);
        if(pic1!=null&&!pic1.equals("")){
            picContent.put(pic1,picContentMap.get(pic1));
        }
        if(pic2!=null&&!pic2.equals("")){
            picContent.put(pic2,picContentMap.get(pic2));
        }
        if(pic3!=null&&!pic3.equals("")){
            picContent.put(pic3,picContentMap.get(pic3));
        }

//        System.out.println(picContentMap.size());

        float h = ExcelUtil.getOrCreateRow(sheet,8).getHeightInPoints();
        float w = sheet.getColumnWidthInPixels(0);
        float radio = w/h;

        int allpicw = 0;
        int allpich = 0;
        for(Map.Entry<String,byte[]> entry:picContent.entrySet()){
            byte[] content = entry.getValue();
            int[] tmpr = new int[0];
            try {
                tmpr = ImageUtil.getImageSize(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            allpicw += tmpr[0];
            if(allpich<tmpr[1]){
                allpich = tmpr[1];
            }
            picSize.put(entry.getKey(),tmpr);
        }

        int numberwide=0;
        int numberwideall = 4;
        //System.out.println(picContent.size());
        for(Map.Entry<String,byte[]> entry:picContent.entrySet()){
            int ww =  picSize.get(entry.getKey())[0];
            int hh =  picSize.get(entry.getKey())[1];
            float picradio = 1;
            if(allpicw>allpich){
                picradio = ((float)(ww))/allpicw;
            }
            int pictureIdx = sheet.getWorkbook().addPicture(entry.getValue(), ExcelUtil.getPicTypeByString(entry.getKey().split("\\.")[1]));
            CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            if(allpicw>allpich){
                anchor.setCol1(10+numberwide);
                numberwide += (int)(numberwideall*picradio)+1;
            }else{
                anchor.setCol1(10+numberwide);
                numberwide += (int)(numberwideall*ww/hh/radio*2.5)+1;
            }
            anchor.setRow1(2+top);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            if(allpicw>allpich){
                pict.resize(numberwideall*picradio,numberwideall*radio*hh/ww*picradio);
            }else{
                pict.resize(numberwideall*ww/hh/radio*2.5,numberwideall*2.5);
            }
        }
        allline.put("allline",allline.get("allline")+tasklist.size());
        return 7+tasklist.size()+4;
    }

    public int getSubTaskBookid() {
        return subTaskBookid;
    }

    public void setSubTaskBookid(int subTaskBookid) {
        this.subTaskBookid = subTaskBookid;
    }

    public void initByTask(ShopkeeperTask task){
        shopkeeperName = task.shopkeeperName;
        shopName = task.shopName;
        shopWangwang = task.shopWangwang;
        itemLink = task.getItemLink();
        pcCost = task.getPcCost();
        phoneCost = task.phoneCost;
        pic1 = task.pic1;
        pic2 = task.pic2;
        pic3 = task.pic3;
        subTaskBookid = task.getSubTaskBookId();
    }

    public void addShopkeeperTask(ShopkeeperTask st){
        tasklist.add(st);
    }






}