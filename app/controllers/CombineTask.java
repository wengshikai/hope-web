package controllers;

import models.CombineShopBuyerManager;
import models.GlobalTool;
import models.dbtable.CombineShopBuyer;
import models.util.MiscTool;
import util.AmountUtil;
import util.ExcelUtil;
import play.mvc.*;
import util.FileTool;
import util.ZIPTool;
import views.html.*;

import java.io.IOException;
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


    /** 上传商家刷手对应表 */
    public Result uploadShopBuyer() {
        String fileNameListStr = "";
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zipFile = body.getFile("shopbuyerzip");
        if (zipFile != null) {
            try {
                /** 删除目录 */
                FileTool.deleteDirectory("data/combine/");
                /** 创建目录 */
                FileTool.createDestDirectoryIfNotExists("data/combine/");
                /** 解压上传的文件 */
                ZIPTool.unZipToFolder(zipFile.getFile().getAbsolutePath(), "data/combine/");
                /** 获取解压后的所有文件 */
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

                /** 先清空表中的所有数据 */
                GlobalTool.initCombineShopBuyer();

                /** 获取目录下的所有xls文件,依次处理 */
                List<String> fileList = FileTool.getFileListInDirectory(dirPath);
                for(String fileName:fileList){
                    if(fileName.endsWith(".xls")) {
                        try {
                            addOneShopBuyer(fileName);
                        } catch (Exception e) {
                            flash("error", e.getMessage());
                            return redirect(routes.CombineTask.combineShopBuyer());
                        }

                        fileNameListStr += "  " + fileName.replaceAll("data/combine/", "") + ";";
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                flash("error", "解析文件失败");
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
                    String shopName = excel.get(0).get(columnIndex);
                    for (int rowIndex = 1; rowIndex < excel.size(); rowIndex++) {
                        String buyerWangwang = excel.get(rowIndex).get(columnIndex);
                        if (buyerWangwang == null || buyerWangwang.equals("")) {
                            break;
                        }
                        //价格转化为分,存成整数
                        int price = Integer.parseInt(AmountUtil.changeY2F(excel.get(rowIndex).get(columnIndex + 1)));
                        CombineShopBuyerManager.insert(shopName, buyerWangwang, price);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("插入数据库异常:" + excelFileName);
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
        List<String> excelNameList = new ArrayList<String>();
        for(String shopname: shopNames) {
            //每个店铺名获取相应的刷手列表
            List<CombineShopBuyer> combineShopBuyerList = CombineShopBuyerManager.getBuyerByShopName(shopname);
            //生成xls文件
            String excelName = MiscTool.buildCombineShopBuyerExcel(combineShopBuyerList);
            excelNameList.add(excelName);
        }

        //打成zip包
        byte[] ret = MiscTool.buildDownloadCombineZip(excelNameList, "combineExcelTmp/combine.zip");

        response().setHeader("Content-Disposition", "attachment;filename=combine.zip");
        return ok(ret);
    }

}
