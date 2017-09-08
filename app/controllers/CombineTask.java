package controllers;

import models.CombineShopBuyerManager;
import models.GlobalTool;
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
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart zipFile = body.getFile("shopbuyerzip");
        if (zipFile != null) {
            try {
                FileTool.deleteDirectory("data/combine/");
                FileTool.createDestDirectoryIfNotExists("data/combine/");
                ZIPTool.unZipToFolder(zipFile.getFile().getAbsolutePath(), "data/combine/");
                List<String> dirlist = FileTool.getFileListInDirectory("data/combine/");
                String dirpath = "";
                for (String s : dirlist) {
                    if (s.contains("__")) {
                        continue;
                    }
                    dirpath = s;
                }
                if (dirpath.equals("")) {
                    flash("batch_error", "file error!");
                    return redirect(
                            routes.CombineTask.combineShopBuyer()
                    );
                }

                /** 先清空表中的所有数据 */
                GlobalTool.initCombineShopBuyer();

                /** 获取目录下的所有xls文件,依次处理 */
                List<String> fileList = FileTool.getFileListInDirectory(dirpath);
                for(String fileName:fileList){
                    if(fileName.endsWith(".xls")) {
                        int doret = addOneShopBuyer(fileName);
                        if (doret == -1) {
                            flash("error", "文件解析失败");
                            return redirect(routes.CombineTask.combineShopBuyer());
                        }
                        if (doret == -2) {
                            flash("error", "文件插入数据库异常");
                            return redirect(routes.CombineTask.combineShopBuyer());
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            flash("success", "上传成功");
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


    private int addOneShopBuyer(String excelFileName){
        ArrayList<ArrayList<String>> excel = ExcelUtil.parse(excelFileName);
        if(excel == null || excel.get(0).size()==0 ) {
            return -1;
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
            return -2;
        }

        return 0;
    }
}
