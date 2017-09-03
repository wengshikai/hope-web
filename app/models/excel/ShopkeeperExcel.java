package models.excel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.util.*;
import util.ExcelUtil;


/**
 * Created by fengya on 15-12-16.
 */
public class ShopkeeperExcel {
    List<ShopkeeperExcelItem> shopkeeperExcelItemList  = new ArrayList<ShopkeeperExcelItem>();
    public boolean parse(String filepath){
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filepath));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            for(int i = 0;;i++){
                try{
                    if(ExcelUtil.getCellString(sheet, i, 0) == null){
                        break;
                    }
                    ShopkeeperExcelItem shopkeeperExcelItem = new ShopkeeperExcelItem();
                    shopkeeperExcelItem.setShopName(ExcelUtil.getCellString(sheet,i,2));
                    shopkeeperExcelItem.setShopWangwang(ExcelUtil.getCellString(sheet,i,4));
                    shopkeeperExcelItem.setShopkeeperName(ExcelUtil.getCellString(sheet, i, 6));
                    shopkeeperExcelItem.setShopkeeperMobilephone(ExcelUtil.getCellString(sheet, i, 8));
                    shopkeeperExcelItem.setShopkeeperQq(ExcelUtil.getCellString(sheet, i, 10));
                    shopkeeperExcelItem.setShopkeeperWechat(ExcelUtil.getCellString(sheet,i, 12));
                    shopkeeperExcelItemList.add(shopkeeperExcelItem);
                }catch (Exception e){
                    break;
                }

            }

            wb.close();
            fs.close();

        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }

        return true;
    }
    public List<ShopkeeperExcelItem> getShopkeeperExcelItemList() {
        return shopkeeperExcelItemList;
    }

    public void setShopkeeperExcelItemList(List<ShopkeeperExcelItem> shopkeeperExcelItemList) {
        this.shopkeeperExcelItemList = shopkeeperExcelItemList;
    }

}
