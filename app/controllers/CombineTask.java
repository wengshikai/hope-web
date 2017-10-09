package controllers;

import com.google.common.collect.Lists;
import models.dbmanager.CombineShopBuyerManager;
import models.dbmanager.GlobalTool;
import models.dbtable.CombineShopBuyer;
import models.excel.ShopCollectionExcel;
import models.util.MiscTool;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.AmountUtil;
import util.ExcelUtil;
import util.FileTool;
import util.ZIPTool;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanmao on 17/9/8.
 */
public class CombineTask extends Controller{


    @Security.Authenticated(Secured.class)
    public Result combineShopBuyer() {
        return ok(combineShopBuyer.render());
    }


    /** 清除商家刷手映射表数据 */
    public Result clearCombineShopBuyer() {

        /* 删除目录 */
        FileTool.deleteDirectory("data/combine/");

        /* 初始化数据库 */
        GlobalTool.initCombineShopBuyer();

        /* 获取所有的店铺名 */
        List<String> shopNames = CombineShopBuyerManager.getAllShopNames();

        if(shopNames == null || shopNames.size() == 0) {
            flash("clear_success", "清除数据成功");
            return redirect(
                    routes.CombineTask.combineShopBuyer()
            );
        } else {
            flash("clear_error", "清除数据失败");
            return redirect(
                    routes.CombineTask.combineShopBuyer()
            );
        }
    }



    /** 上传商家刷手对应表 */
    public Result uploadShopBuyer() {
        String fileNameListStr = "";
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zipFile = body.getFile("shopbuyerzip");
        if (zipFile != null) {
            try {
                /* 删除目录 */
                FileTool.deleteDirectory("data/combine/");
                /* 创建目录 */
                FileTool.createDestDirectoryIfNotExists("data/combine/");
                /* 解压上传的文件 */
                ZIPTool.unZipToFolder(zipFile.getFile().getAbsolutePath(), "data/combine/");
                /* 获取解压后的所有文件 */
                List<String> dirList = FileTool.getFileListInDirectory("data/combine/");
                String dirPath = "";
                for (String s : dirList) {
                    if (s.contains("__")) {
                        continue;
                    }
                    dirPath = s;
                }
                if (dirPath.equals("")) {
                    flash("error", "上传失败!");
                    return redirect(
                            routes.CombineTask.combineShopBuyer()
                    );
                }

                /* 先清空表中的所有数据 */
                GlobalTool.initCombineShopBuyer();

                /* 获取目录下的所有xls文件,依次处理 */
                List<String> fileList = FileTool.getFileListInDirectory(dirPath);
                for(String fileName:fileList){
                    if(fileName.endsWith(".xls")) {
                        //解析单个文件,并将表信息写入数据库
                        addOneShopBuyer(fileName);
                        fileNameListStr += "  " + fileName.replaceAll("data/combine/", "") + ";";
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                flash("error", e.getMessage());
                return redirect(
                        routes.CombineTask.combineShopBuyer()
                );
            }

            flash("success", "上传成功,文件列表:" + fileNameListStr);
            return redirect(
                    routes.CombineTask.combineShopBuyer()
            );
        } else {
            flash("error", "找不到文件");
            return redirect(
                    routes.CombineTask.combineShopBuyer()
            );
        }
    }


    /** 解析单个xls文件,并写入数据库 */
    private void addOneShopBuyer(String excelFileName) throws Exception {
        ArrayList<ArrayList<String>> excel = ExcelUtil.parse(excelFileName);
        if(excel == null || excel.get(0).size()==0 ) {
            throw new Exception("文件解析失败:" + excelFileName);
        }

        try {
            for (int columnIndex = 0; columnIndex < excel.get(0).size(); columnIndex++) {
                if (columnIndex % 2 == 0) {
                    String shopName = excel.get(0).get(columnIndex).trim();
                    if(shopName.equals("")) {
                        throw new Exception("没有读取到店铺名,请确认是否填写正确!" + excelFileName + ",单元格:" + ExcelUtil.getColumnLabels(columnIndex) + "1");
                    }

                    for (int rowIndex = 1; rowIndex < excel.size(); rowIndex++) {
                        String buyerWangwang = excel.get(rowIndex).get(columnIndex).trim();
                        if (buyerWangwang.equals("")) {
                            break;
                        }
                        //价格转化为分,存成整数
                        int price;
                        try {
                            price = Integer.parseInt(AmountUtil.changeY2F(excel.get(rowIndex).get(columnIndex + 1).trim()));
                        } catch (Exception e) {
                            throw new Exception("读取价格异常!" + excelFileName + ",单元格:" + ExcelUtil.getColumnLabels(columnIndex + 1) + (rowIndex+1));
                        }

                        if(price == 0) {
                            throw new Exception("读取到的价格为0,请确认是否填写正确!" + excelFileName + ",单元格:" + ExcelUtil.getColumnLabels(columnIndex + 1) + (rowIndex+1));
                        }

                        if(!CombineShopBuyerManager.insert(shopName, buyerWangwang, price)) {
                            throw new Exception("插入数据库异常,请检查同一个店铺是否有重复刷手!文件名:" + excelFileName + ",店铺名:" + shopName + ",买家旺旺名:" + buyerWangwang);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    /** 下载合并后的商家-刷手映射表 */
    @Security.Authenticated(Secured.class)
    public Result downloadShopBuyerTable() throws Exception {
        //初始化目录
        FileTool.deleteDirectory("combineExcelTmp");
        FileTool.createDestDirectoryIfNotExists("combineExcelTmp/");

        //获取所有的店铺名
        List<String> shopNames = CombineShopBuyerManager.getAllShopNames();
        if (shopNames != null && shopNames.size() !=0 ) { //判断存在数据才会生成表格
            List<String> excelNameList = Lists.newArrayList();
            List<ShopCollectionExcel> shopCollectionExcels = Lists.newArrayList();
            for (String shopName : shopNames) {
                //每个店铺名获取相应的刷手列表
                List<CombineShopBuyer> combineShopBuyerList = CombineShopBuyerManager.getBuyerByShopName(shopName);
                //生成xls文件
                String excelName = MiscTool.buildCombineShopBuyerExcel(combineShopBuyerList);
                excelNameList.add(excelName);

                //统计单数和金额
                ShopCollectionExcel shopCollectionExcel = new ShopCollectionExcel();
                shopCollectionExcel.setShopName(shopName);
                shopCollectionExcel.setOrderNum(combineShopBuyerList == null ? 0 : combineShopBuyerList.size());
                int totalAmount = 0;
                for(CombineShopBuyer combineShopBuyer: combineShopBuyerList) {
                    totalAmount += combineShopBuyer.getPrice();
                }
                shopCollectionExcel.setTotalAmount(totalAmount);
                shopCollectionExcels.add(shopCollectionExcel);
            }
            //打成zip包
            MiscTool.buildDownloadZip(excelNameList, "combineExcelTmp/combine.zip");

            //生成商家金额汇总表
            MiscTool.buildShopCollectionExcel(shopCollectionExcels, "combineExcelTmp/商家汇总.xls");

            //打成总的压缩包
            List<String> zipFiles = Lists.newArrayList();
            zipFiles.add("combineExcelTmp/combine.zip");
            zipFiles.add("combineExcelTmp/商家汇总.xls");
            byte[] ret = MiscTool.buildDownloadZip(zipFiles, "combineExcelTmp/商家刷手映射表.zip");

            response().setHeader("Content-Disposition", "attachment;filename=combine.zip");
            return ok(ret);
        } else {
            flash("download_error", "没有上传数据源");
            return redirect(
                    routes.CombineTask.combineShopBuyer()
            );
        }
    }

}
