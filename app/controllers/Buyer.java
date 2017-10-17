package controllers;

import models.dbmanager.BuyerManager;
import models.dbmanager.GlobalTool;
import util.ExcelUtil;
import play.data.Form;
import play.mvc.*;
import views.html.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shanmao on 15-12-16.
 */
public class Buyer   extends Controller{

    public static class BuyerForm extends models.entity.Buyer{
        public String validate() {
            if (wangwang==null || wangwang.equals("")) {
                return "无效刷手";
            }
            return null;
        }
    }
    @Security.Authenticated(Secured.class)
    public Result addbuyer() {
        return ok(addbuyer.render(Form.form(BuyerForm.class)));
    }

    @Security.Authenticated(Secured.class)
    public Result doaddbuyer() {
        Form<BuyerForm> form = Form.form(BuyerForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(addbuyer.render(form));
        } else {
            BuyerForm fc = form.get();
            BuyerManager.insert(fc.getName(),fc.getWangwang(),fc.getMobilephone(),fc.getTeam());
            flash("success", "成功添加刷手，姓名：" + fc.getName() + "\t旺旺名:" + fc.getWangwang());
            return redirect(
                    routes.Buyer.addbuyer()
            );

        }
    }



    @Security.Authenticated(Secured.class)
    public Result batchadd() {
        return ok(batchaddbuyer.render());
    }


    @Security.Authenticated(Secured.class)
    public Result dobatchadd() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart shangjiaexcel = body.getFile("buyerexcel");
        if (shangjiaexcel != null) {
            ArrayList<ArrayList<String>> excel;
            try {
                try {
                    java.io.File file = shangjiaexcel.getFile();
                    excel = ExcelUtil.parse(file.getAbsolutePath());
                } catch (Exception e) {
                    throw new Exception("解析文件失败,请确认文件格式是否正确!");
                }

                if (excel == null || excel.size() == 0) {
                    throw new Exception("excel中没有读取到数据!");
                }

                int buyerIndex = 0;
                for (ArrayList<String> row : excel) {
                    String name = row.get(0).trim();
                    if (name.equals("")) {
                        throw new Exception("刷手名称不能为空!单元格:A" + (buyerIndex+1));
                    }

                    String wangwang = row.get(1).trim();
                    if (wangwang.equals("")) {
                        throw new Exception("刷手旺旺名不能为空!单元格:B" + (buyerIndex+1));
                    }

                    String mobilephone = row.get(2).trim();

                    int team;
                    try {
                        team = Double.valueOf((row.get(3).trim())).intValue();
                    } catch (Exception e) {
                        throw new Exception("组别填写错误,当前只能填写整数!单元格:D" + (buyerIndex+1));
                    }

                    if (!BuyerManager.insert(name, wangwang, mobilephone, team)) {
                        throw new Exception("插入数据库异常,请检查刷手是否重复!" + "刷手旺旺名:" + wangwang);
                    }
                    buyerIndex ++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                //如果有一条数据插入失败,全部删除
                //GlobalTool.initBuyer();
                flash("error", e.getMessage());
                return redirect(
                        routes.Buyer.batchadd()
                );
            }

            flash("success", "批量添加刷手成功！");
            return redirect(
                    routes.Buyer.batchadd()
            );
        } else {
            flash("error", "文件不存在");
            return redirect(
                    routes.Buyer.batchadd()
            );
        }
    }




    @Security.Authenticated(Secured.class)
    public Result all() {
        List<models.entity.Buyer> ssl = BuyerManager.getALl();
        return ok(allbuyer.render(ssl));
    }

    @Security.Authenticated(Secured.class)
    public Result clear() {
        GlobalTool.initBuyer();
        return redirect(
                routes.Buyer.all()
        );
    }
}




