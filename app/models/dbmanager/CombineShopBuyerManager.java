package models.dbmanager;

import models.dbtable.CombineShopBuyer;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by shanmao on 17/9/8.
 */
public class CombineShopBuyerManager {

    public static boolean insert(String shopName,String buyerWangwang,int price){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            try {
                CombineShopBuyer entry = new CombineShopBuyer();
                entry.setShopName(shopName);
                entry.setBuyerWangwang(buyerWangwang);
                entry.setPrice(price);
                DatabaseTool.defaultEm.persist(entry);
                DatabaseTool.defaultEm.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.logger.error("something error!",e);
                //如果插入失败,需要回滚
                DatabaseTool.defaultEm.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return false;
        }
        return true;
    }


    /** 获取所有店铺名 */
    public static List<String> getAllShopNames(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select DISTINCT shopName from CombineShopBuyer");
            List<String> shopNames =(List<String>)query.getResultList();
            return shopNames;
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return null;
        }
    }


    /** 根据店铺名获取刷手信息 */
    public static List<CombineShopBuyer> getBuyerByShopName(String shopName){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from CombineShopBuyer u where u.shopName = \'" + shopName + "\' ");
            List<CombineShopBuyer> combineShopBuyerList =(List<CombineShopBuyer>)query.getResultList();
            return combineShopBuyerList;
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return null;
        }
    }



}
