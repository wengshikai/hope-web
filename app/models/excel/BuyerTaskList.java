package models.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ExcelUtil;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanmao on 15-11-25.
 */
@Data
public class BuyerTaskList {
    private String filepath;
    private Workbook wb;
    private Sheet sheet;
    private int taskindex = 0;
    private int taskbookid;
    private int top = 5;
    private int left = 1;
    private List<BuyerTask> stlist =new ArrayList<BuyerTask>();

    public static Font redfont; // 红
    public static Font turquoisefont; //天蓝
    public static Font bluegreyfont; //灰蓝
    public static Font blackfont; //灰蓝

    //前端打包有些bug,需要手动添加get函数
    public List<BuyerTask> getStlist() {
        return stlist;
    }


    public void Deal() throws Exception {
        wb = new XSSFWorkbook();
        sheet = ExcelUtil.getOrCreateSheet(wb, "sheet1");
        redfont =  wb.createFont();
        redfont.setColor(IndexedColors.RED.getIndex());
        turquoisefont = wb.createFont();
        turquoisefont.setColor(IndexedColors.TURQUOISE.getIndex());
        bluegreyfont = wb.createFont();
        bluegreyfont.setColor(IndexedColors.BLUE_GREY.getIndex());
        blackfont = wb.createFont();
        blackfont.setColor(IndexedColors.BLACK.getIndex());
        for(BuyerTask sst:stlist){
            sst.setLeft(left);
            sst.setTop(top);
            sst.setSheet(sheet);
            sst.setTaskindex(taskindex + 1);

            sst.deal();
            next();
        }
        makeHead();
        FileOutputStream fileOut = new FileOutputStream(filepath);
        wb.write(fileOut);
        fileOut.close();
    }

    void makeHead(){
        Cell c = ExcelUtil.getOrCreateCell(sheet, 0,0);
        ExcelUtil.setThickBorder(c,true,true,false,false);
        c = ExcelUtil.getOrCreateCell(sheet, 0,1);
        c.setCellValue("总单数");
        ExcelUtil.setThickBorder(c,true,true,false,false);
        c = ExcelUtil.getOrCreateCell(sheet, 0,2);
        ExcelUtil.setThickBorder(c,false,true,true,true);
        c = ExcelUtil.getOrCreateCell(sheet, 1,0);
        ExcelUtil.setThickBorder(c,true,false,true,false);
        c = ExcelUtil.getOrCreateCell(sheet, 1,1);
        c.setCellValue("总本金");
        ExcelUtil.setThickBorder(c,true,true,false,true);
        c = ExcelUtil.getOrCreateCell(sheet, 1,2);
        ExcelUtil.setThickBorder(c,false,true,true,true);
        c = ExcelUtil.getOrCreateCell(sheet, 2,0);
        ExcelUtil.setThickBorder(c,true,false,true,false);
        c = ExcelUtil.getOrCreateCell(sheet, 2,1);
        c.setCellValue("总佣金");
        ExcelUtil.setThickBorder(c,true,true,false,true);
        c = ExcelUtil.getOrCreateCell(sheet, 2,2);
        ExcelUtil.setThickBorder(c,false,true,true,true);
        c = ExcelUtil.getOrCreateCell(sheet, 3,0);
        ExcelUtil.setThickBorder(c,true,false,true,true);
        c = ExcelUtil.getOrCreateCell(sheet, 3,1);
        c.setCellValue("总金额");
        ExcelUtil.setThickBorder(c,true,true,false,true);
        c = ExcelUtil.getOrCreateCell(sheet, 3,2);
        ExcelUtil.setThickBorder(c,false,true,true,true);

        ExcelUtil.setRegionColor(sheet, IndexedColors.YELLOW.getIndex(), 0, 0, 2, 3);

        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                3, //last row  (0-based)
                0, //first column (0-based)
                0  //last column  (0-based)
        ));

        c = ExcelUtil.getOrCreateCell(sheet, 0,0);
        c.setCellValue(taskbookid+"号");
        CellStyle style =c.getCellStyle();
        style.setAlignment( CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment( CellStyle.VERTICAL_CENTER);
        c.setCellStyle(style);
        c = ExcelUtil.getOrCreateCell(sheet, 0,2);
        c.setCellType(Cell.CELL_TYPE_FORMULA);
        c.setCellFormula(getZongDanShu());
        c = ExcelUtil.getOrCreateCell(sheet, 1,2);
        c.setCellType(Cell.CELL_TYPE_FORMULA);
        c.setCellFormula(getZongBenJin());
        c = ExcelUtil.getOrCreateCell(sheet, 2,2);
        c.setCellType(Cell.CELL_TYPE_FORMULA);
        c.setCellFormula("C1*5");
        c = ExcelUtil.getOrCreateCell(sheet, 3,2);
        c.setCellType(Cell.CELL_TYPE_FORMULA);
        c.setCellFormula("C2+C3");
    }

    public void next() {
        taskindex++;
        int tmprow = taskindex/3;
        int tmpcolumn = taskindex%3;
        top = 5+13*tmprow;
        left = 1+8*tmpcolumn;
    }

    //G O W
    //H P X
    public String getZongDanShu(){
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i<taskindex;i++){
            String c = "";
            switch(i%3){
                case 0:{
                    c="H";
                    break;
                }
                case 1:{
                    c="P";
                    break;
                }
                case 2:{
                    c="X";
                    break;
                }
            }
            int tmprow = i/3;
            int tmp = 6+13*tmprow;
            c = c+tmp;
            if(i==0){
                sb.append(c);
            }else{
                sb.append("+"+c);
            }
        }
        return sb.toString();
    }

    //G O W
    //H P X
    public int getZongDanShuNum(){
        return stlist.size();
    }

    //G O W
    //H P X
    public String getZongBenJin(){
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i<taskindex;i++){
            String c = "";
            switch(i%3){
                case 0:{
                    c="G";
                    break;
                }
                case 1:{
                    c="O";
                    break;
                }
                case 2:{
                    c="W";
                    break;
                }
            }
            int tmprow = i/3;
            int tmp = 6+13*tmprow;
            c = c+tmp;
            if(i==0){
                sb.append(c);
            }else{
                sb.append("+"+c);
            }
        }
        return sb.toString();
    }

    public double getZongBenJinNum(){
        double all = 0;
        for(BuyerTask st:stlist){
            all += st.getAllPrice();
        }
        return util.Misc.formatDoubleForMoney(all);
    }

    public double getZongYongjinNum(){
        return 5*getZongDanShuNum();
    }

    public void addShuashouTask(BuyerTask sst){
        stlist.add(sst);
    }

    public static String num2alph(int i){
        String s = ""+(char)((26+i-1)%26+'A');
        for(;(i-1)/26!=0;){
            i = i/26;
            s =(char)((26+i-1)%26+'A')+s;
        }
        return s;
    }

    public static int alph2num(String alph){
        int sum = 0;
        for(int i=0;i<alph.length();i++){
            sum = sum*26;
            sum += (alph.charAt(i)-'A'+1);
        }
        return sum;
    }
}
