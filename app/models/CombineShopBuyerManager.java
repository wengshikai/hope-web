package models;

import models.dbtable.CombineShopBuyer;
import models.util.DatabaseTool;

/**
 * Created by shanmao on 17/9/8.
 */
public class CombineShopBuyerManager {
    public static boolean insert(String shopName,String buyerWangwang,int price){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            CombineShopBuyer entry = new CombineShopBuyer();
            entry.setShopName(shopName);
            entry.setBuyerWangwang(buyerWangwang);
            entry.setPrice(price);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }
        return true;
    }
}
