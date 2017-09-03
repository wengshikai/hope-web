package controllers;

import models.BuyerManager;
import models.GlobalTool;
import util.ExcelUtil;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by fengya on 15-12-16.
 */
public class Buyer   extends Controller{

    public static class BuyerForm extends models.dbtable.Buyer{
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
            BuyerManager.insert(fc.getName(),fc.getWangwang(),fc.getMobilephone(),fc.getLevel());
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
            java.io.File file = shangjiaexcel.getFile();
            ArrayList<ArrayList<String>> res = ExcelUtil.parse(file.getAbsolutePath());
            for(int i=0;i<res.size();i++){
                ArrayList<String> res_i = res.get(i);
                if(res_i.get(0).trim().equals("")||res_i.get(1).trim().equals("")||res_i.get(2).trim().equals("")||res_i.get(3).trim().equals("")){
                    continue;
                }
                try{
                    BuyerManager.insert(res_i.get(0), res_i.get(1), res_i.get(2), Double.valueOf(res_i.get(3)).intValue());
                }catch (Exception e){
                    continue;
                }

            }
            String rets = "批量添加刷手成功！";
            flash("success", rets);
            return redirect(
                    routes.Buyer.batchadd()
            );
        } else {
            flash("error", "Missing file");
            return redirect(
                    routes.Buyer.batchadd()
            );
        }
    }




    @Security.Authenticated(Secured.class)
    public Result all() {
        List<models.dbtable.Buyer> ssl = BuyerManager.getALl();
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




