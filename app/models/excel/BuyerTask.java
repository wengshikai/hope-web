package models.excel;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import util.ExcelUtil;
import util.ImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengya on 15-11-25.
 */
public class BuyerTask {
    class imageandtype{
        byte[] content;
        String type;
        int w;
        int h;
        Picture pict;
    }
    private int taskindex;
    private int top;
    private int left;
    private Sheet sheet;
    private List<imageandtype> images = new ArrayList<imageandtype>();
    private int goodnum;
    private double unit_price;
    private double allPrice;
    private String keyword;
    private String shopname;
    private String requirement;
    private double phoneCost;

    private static Map<String,Integer> picTypeMap = new HashMap<String,Integer>();
    static {
        picTypeMap.put("jpg",Workbook.PICTURE_TYPE_JPEG);
        picTypeMap.put("jpeg",Workbook.PICTURE_TYPE_JPEG);
        picTypeMap.put("png",Workbook.PICTURE_TYPE_PNG);
    }



    public void deal(){
        drawBorder();
        Cell c = ExcelUtil.getOrCreateCell(sheet, top, left + 5);
        ExcelUtil.setCellColor(c,IndexedColors.RED.getIndex());
        c = ExcelUtil.getOrCreateCell(sheet,top,left+6);
        ExcelUtil.setCellColor(c,IndexedColors.YELLOW.getIndex());
        c.setCellValue(1);
        Row r = ExcelUtil.getOrCreateRow(sheet,top+4);
        r.setHeightInPoints(200);
        Cell cell = ExcelUtil.getOrCreateCell(sheet, top + 2, left);
        cell.setCellValue(getOpStr1());
        if(requirement != null && !requirement.equals("")){
            cell = ExcelUtil.getOrCreateCell(sheet,top+6,left);
            cell.setCellValue(getColorRequirement());
        }
        cell = ExcelUtil.getOrCreateCell(sheet,top+7,left);
        cell.setCellValue(getOpStr2());
        cell = ExcelUtil.getOrCreateCell(sheet,top+8,left);
        cell.setCellValue("(加入购物车，尺码颜色随意)");
        cell = ExcelUtil.getOrCreateCell(sheet,top+9,left);
        cell.setCellValue(getPriceText(allPrice));
        cell = ExcelUtil.getOrCreateCell(sheet,top+10,left);
        cell.setCellValue("店铺："+shopname);
        cell = ExcelUtil.getOrCreateCell(sheet,top+1,left);
        cell.setCellValue(getOpKeyWord());
        cell = ExcelUtil.getOrCreateCell(sheet,top+1,left+5);
        int low = (int) ((phoneCost)*0.9);
        int high = (int) ((phoneCost)*1.1);
        cell.setCellValue("卡"+low+"-"+high);
        cell = ExcelUtil.getOrCreateCell(sheet,top,left+5);
        cell.setCellValue(allPrice);

        //插图
        // 横跨7格，竖跨3
        float maxh = ExcelUtil.getOrCreateRow(sheet,top+3).getHeightInPoints()+
                ExcelUtil.getOrCreateRow(sheet,top+4).getHeightInPoints()+
                ExcelUtil.getOrCreateRow(sheet,top+5).getHeightInPoints();
        float maxw = sheet.getColumnWidthInPixels(left)*7;


        float h = ExcelUtil.getOrCreateRow(sheet,top+3).getHeightInPoints();
        float w = sheet.getColumnWidthInPixels(left);
        float radio = w/h;

        int allpicw = 0;
        int allpich = 0;
        for(int i=0;i<images.size();i++){
            imageandtype imgtmp = images.get(i);
            int[] tmpr1 = new int[0];
            try {
                tmpr1 = ImageUtil.getImageSize(imgtmp.content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            allpicw += tmpr1[0];
            if(allpich<tmpr1[1]){
                allpich = tmpr1[1];
            }
            imgtmp.w = tmpr1[0];
            imgtmp.h = tmpr1[1];
        }

        int numberwide=0;
        int usegezishu = 4;
        for(int i=0;i<images.size();i++){
            float picradio = 1;
            imageandtype imgtmp = images.get(i);
            if(allpicw>allpich){
                picradio = ((float)(imgtmp.w))/allpicw;
            }
            int pictureIdx = sheet.getWorkbook().addPicture(imgtmp.content,picTypeMap.get(imgtmp.type.toLowerCase()));
            CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            if(allpicw>allpich){
                anchor.setCol1(left+numberwide);
                numberwide += (int)(usegezishu*picradio)+1;
            }else{
                anchor.setCol1(left+numberwide);
                numberwide += (int)(usegezishu*imgtmp.w/imgtmp.h/radio*2.5)+1;
            }
            anchor.setRow1(top+3);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            imgtmp.pict = pict;
            if(allpicw>allpich){
                imgtmp.pict.resize(usegezishu*picradio,usegezishu*radio*imgtmp.h/imgtmp.w*picradio);
            }else{
                imgtmp.pict.resize(usegezishu*imgtmp.w/imgtmp.h/radio*2.5,usegezishu*2.5);
            }
        }
    }





    private void drawBorder(){
        for(int i=1;i<6;i++){
            Cell c = ExcelUtil.getOrCreateCell(sheet, top,left+i);
            ExcelUtil.setThickBorder(c,false,true,false,false);
        }

        for(int i=1;i<6;i++){
            Cell c = ExcelUtil.getOrCreateCell(sheet, top+10,left+i);
            ExcelUtil.setThickBorder(c,false,false,false,true);
        }

        for(int i=1;i<10;i++){
            Cell c = ExcelUtil.getOrCreateCell(sheet, top+i,left);
            ExcelUtil.setThickBorder(c,true,false,false,false);
        }

        for(int i=1;i<10;i++){
            Cell c = ExcelUtil.getOrCreateCell(sheet, top+i,left+6);
            ExcelUtil.setThickBorder(c,false,false,true,false);
        }

        Cell c = ExcelUtil.getOrCreateCell(sheet, top,left);
        ExcelUtil.setThickBorder(c,true,true,false,false);
        c = ExcelUtil.getOrCreateCell(sheet, top,left+6);
        ExcelUtil.setThickBorder(c,false,true,true,false);
        c = ExcelUtil.getOrCreateCell(sheet, top+10,left+6);
        ExcelUtil.setThickBorder(c,false,false,true,true);
        c = ExcelUtil.getOrCreateCell(sheet, top+10,left);
        ExcelUtil.setThickBorder(c,true,false,false,true);
    }









    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
    public int getGoodnum() {
        return goodnum;
    }

    public void setGoodnum(int goodnum) {
        this.goodnum = goodnum;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    private XSSFRichTextString getOpStr1(){
        XSSFRichTextString richString = new XSSFRichTextString("先点开别人的3样东西，再找到下面这个一模一样的图片点击进去");
        richString.applyFont( 0, 14, BuyerTaskList.blackfont );
        richString.applyFont( 14, 16, BuyerTaskList.redfont );
        richString.applyFont( 16, 18, BuyerTaskList.blackfont );
        richString.applyFont( 18, 22, BuyerTaskList.bluegreyfont );
        richString.applyFont( 22, 23, BuyerTaskList.blackfont );
        richString.applyFont( 23, 25, BuyerTaskList.redfont );
        richString.applyFont( 25, 27, BuyerTaskList.turquoisefont );
        richString.applyFont( 27, 29, BuyerTaskList.blackfont );
        return richString;
    }

    private XSSFRichTextString getOpStr2(){
        XSSFRichTextString richString = new XSSFRichTextString("找到这款宝贝浏览下，宝贝收藏下，简单浏览，页面停留5-8分钟");
        richString.applyFont( 0, 2, BuyerTaskList.blackfont );
        richString.applyFont( 2, 30, BuyerTaskList.redfont );
        return richString;
    }

    private XSSFRichTextString getColorRequirement(){
        XSSFRichTextString richString = new XSSFRichTextString("注意要求："+requirement);
        richString.applyFont( 0, 5, BuyerTaskList.blackfont );
        richString.applyFont( 5, 5+requirement.length(), BuyerTaskList.redfont );
        return richString;
    }

    private XSSFRichTextString getPriceText(double allprice){
        XSSFRichTextString richString = new XSSFRichTextString("价格拍"+allprice+"元");
        richString.applyFont(BuyerTaskList.redfont);
        return richString;
    }

    private XSSFRichTextString getOpKeyWord(){
        XSSFRichTextString richString = new XSSFRichTextString("");
        richString.append("第", (XSSFFont) BuyerTaskList.blackfont);
        richString.append(""+taskindex, (XSSFFont) BuyerTaskList.redfont);
        richString.append("件 在淘宝上搜索（", (XSSFFont) BuyerTaskList.blackfont);
        richString.append(keyword, (XSSFFont) BuyerTaskList.redfont);
        richString.append("）", (XSSFFont) BuyerTaskList.blackfont);
        return richString;
    }



    public int getTaskindex() {
        return taskindex;
    }

    public void setTaskindex(int taskindex) {
        this.taskindex = taskindex;
    }

    public void  addPic(byte[] content,String type){
        imageandtype img = new imageandtype();
        img.content = content;
        img.type = type;
        images.add(img);
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public double getPhoneCost() {
        return phoneCost;
    }

    public void setPhoneCost(double phoneCost) {
        this.phoneCost = phoneCost;
    }

    public double getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(double allPrice) {
        this.allPrice = allPrice;
    }
}
