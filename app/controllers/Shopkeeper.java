package controllers;

import models.ShopManager;
import models.ShopkeeperManager;
import models.dbtable.Shop;
import models.excel.ShopkeeperExcel;
import models.excel.ShopkeeperExcelItem;
import util.ExcelUtil;
import play.data.Form;
import play.mvc.*;
import views.html.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weng on 15-12-15.
 */
public class Shopkeeper  extends Controller {

    //    String name;
//    String mobilephone;
//    String qq;
//    String wechat;
    public static class ShopkeeperForm extends models.dbtable.Shopkeeper{

        public String validate() {
            if (name==null || name.equals("") || mobilephone==null || mobilephone.equals("")) {
                return "无效商家";
            }
            return null;
        }

    }

    public static class ShopForm{
        String shopName;
        String shopWangwang;
        String shopkeeperName;
        String shopkeeperMobilephone;

        public String getShopWangwang() {
            return shopWangwang;
        }

        public void setShopWangwang(String shopWangwang) {
            this.shopWangwang = shopWangwang;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopkeeperName() {
            return shopkeeperName;
        }

        public void setShopkeeperName(String shopkeeperName) {
            this.shopkeeperName = shopkeeperName;
        }

        public String getShopkeeperMobilephone() {
            return shopkeeperMobilephone;
        }

        public void setShopkeeperMobilephone(String shopkeeperMobilephone) {
            this.shopkeeperMobilephone = shopkeeperMobilephone;
        }

        public String validate() {
            if (shopWangwang==null || shopWangwang.equals("") ||
                    shopkeeperName==null || shopkeeperName.equals("") ||
                    shopkeeperMobilephone==null || shopkeeperMobilephone.equals("") ) {
                return "无效店铺";
            }
            return null;
        }
    }

    @Security.Authenticated(Secured.class)
    public Result addshopkeeper() {
        return ok(addshopkeeper.render(Form.form(ShopkeeperForm.class)));
    }

    @Security.Authenticated(Secured.class)
    public Result doaddshopkeeper() {
        Form<ShopkeeperForm> form = Form.form(ShopkeeperForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(addshopkeeper.render(form));
        } else {
            ShopkeeperForm sk = form.get();
            ShopkeeperManager.insert(sk);
            flash("success", "成功添加商家，姓名：" + sk.getName() + "\t手机号:" + sk.getMobilephone());
            return redirect(
                    routes.Shopkeeper.addshopkeeper()
            );

        }
    }

    @Security.Authenticated(Secured.class)
    public Result addshop() {
        return ok(addshop.render(Form.form(ShopForm.class)));
    }

    @Security.Authenticated(Secured.class)
    public Result doaddshop() {
        Form<ShopForm> form = Form.form(ShopForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(addshop.render(form));
        } else {
            ShopForm fc = form.get();
            if(ShopManager.insertWithCheck(fc.shopkeeperName,fc.shopkeeperMobilephone,fc.shopName,fc.shopWangwang)){
                flash("success", "成功添加店铺，店铺名：" + fc.shopName + "\t旺旺名:" + fc.shopWangwang);
            }else{
                flash("failed", "添加店铺，店铺名：" + fc.shopName + "\t旺旺名:" + fc.shopWangwang + " 失败");
            }
            return redirect(
                    routes.Shopkeeper.addshop()
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public Result all() {
        Map<Integer,models.dbtable.Shopkeeper> shopkeeperMap = new HashMap<Integer,models.dbtable.Shopkeeper>();
        Map<Integer,List<Shop>> shopMap = new HashMap<Integer,List<Shop>>();
        List<models.dbtable.Shopkeeper> shopkeeperList = ShopkeeperManager.getAllShopkeeper();
        for(models.dbtable.Shopkeeper shopkeeper:shopkeeperList){
            shopkeeperMap.put(shopkeeper.getShopkeeperId(),shopkeeper);
            shopMap.put(shopkeeper.getShopkeeperId(),ShopManager.getByShopkeeperId(shopkeeper.getShopkeeperId()));
        }
        return ok(allshopkeeper.render(shopkeeperMap, shopMap));
    }

    @Security.Authenticated(Secured.class)
    public Result batchadd() {
        return ok(batchaddshopkeeper.render());
    }

    @Security.Authenticated(Secured.class)
    public Result dobatchadd() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart excel = body.getFile("shopkeeperexcel");
        if (excel != null) {
            java.io.File file = excel.getFile();
            ShopkeeperExcel shopkeeperExcel = new ShopkeeperExcel();
            shopkeeperExcel.parse(file.getAbsolutePath());
            List<ShopkeeperExcelItem> seilist = shopkeeperExcel.getShopkeeperExcelItemList();
            StringBuffer retok = new StringBuffer();
            StringBuffer reterror = new StringBuffer();
            for(ShopkeeperExcelItem sei:seilist){
                if(ShopkeeperManager.getByNameAndPhnoe(sei.getShopkeeperName(),sei.getShopkeeperMobilephone()) == null){
                    ShopkeeperManager.insert(sei.getShopkeeperName(),sei.getShopkeeperMobilephone(),sei.getShopkeeperQq(),sei.getShopkeeperWechat());
                }
                models.dbtable.Shopkeeper shopkeeper = ShopkeeperManager.getByNameAndPhnoe(sei.getShopkeeperName(),sei.getShopkeeperMobilephone());
                ShopManager.insert(shopkeeper.getShopkeeperId(),sei.getShopName(),sei.getShopWangwang());
                retok.append(sei.getShopkeeperName()+" : "+sei.getShopName()+" 插入成功\n");
            }

            flash("success", retok.toString());
            flash("error", reterror.toString());
            return redirect(
                    routes.Shopkeeper.batchadd()
            );
        } else {
            flash("error", "Missing file");
            return redirect(
                    routes.Shopkeeper.batchadd()
            );
        }
    }
}




