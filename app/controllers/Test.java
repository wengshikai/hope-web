package controllers;

import models.*;
import models.data.ShuashouUser;
import models.util.DatabaseTool;
import play.mvc.*;
import views.html.*;

import java.util.ArrayList;
import java.util.List;


public class Test extends Controller {


    public Result index() {
        int index = 0;
        //ShopkeeperManager.insert("zhuzi",""+index,"11","111");;
        //Shopkeeper shopkeeper = ShopkeeperManager.getByNameAndPhnoe("zhuzi", "14500095861880");
//        List<Shopkeeper> allShopkeeper= ShopkeeperManager.getAllShopkeeper();
//        for(Shopkeeper shopkeeper:allShopkeeper)
//            System.out.println(shopkeeper);
        //String str = DatabaseTool.selectTable("default","select * from TaskTables where buyerTaskBookId=45");
        String str = DatabaseTool.selectTable("default","select * from TaskTables where buyerTaskBookId=45");
        //String str = DatabaseTool.selectTable("default","select shopName,phoneCost from TaskTables where buyerTaskBookId=45");
//        str += DatabaseTool.selectTable("default","select sum(allPrice) from TaskTables where buyerTaskBookId=45");
//        str += DatabaseTool.selectTable("default","select sum(phoneCost) from TaskTables where buyerTaskBookId=45");
//        str += DatabaseTool.selectTable("default","select phoneCost from TaskTables where buyerTaskBookId=45");
        //str += DatabaseTool.selectTable("default","select sum(phonePrice) from TaskTables where buyerTaskBookId=45");
        str = ""+TaskTablesManager.getMaxBuyerTaskBookId();
        return ok(str);
    }

}
