package models;

import models.util.DatabaseTool;
import models.dbtable.Shop;
import models.dbtable.Shopkeeper;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by weng on 15-12-15.
 */
public class ShopManager {
    public static boolean insert(int shopkeeperId,String shopName,String shopWangwang){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            Shop  entry = new Shop();
            entry.setShopkeeperId(shopkeeperId);
            entry.setShopName(shopName);
            entry.setShopWangwang(shopWangwang);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }




    public static List<Shop> getByShopkeeperId(int shopkeeperId){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Shop u where u.shopkeeperId=?1");
            query.setParameter(1,shopkeeperId);
            List<Shop> entry =(List<Shop>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }



    public static boolean insertWithCheck(String name,String mobilephone,String shopName,String shopWangwang){
        Shopkeeper shopkeeper = ShopkeeperManager.getByNameAndPhnoe(name,mobilephone);
        if(shopkeeper == null){
            return false;
        }else{
            return insert(shopkeeper.getShopkeeperId(),shopName,shopWangwang);
        }
    }

}
