package models.dbmanager;

import models.entity.CombineShopBuyer;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by shanmao on 17/9/8.
 */
public class CombineShopBuyerManager {

    public static boolean insert(String shopName,String buyerWangwang,int price){
        try {
            DatabaseTool.defaultEm.getTransaction().begin(); //启动事务
            try {
                CombineShopBuyer entry = new CombineShopBuyer();
                entry.setShopName(shopName);
                entry.setBuyerWangwang(buyerWangwang);
                entry.setPrice(price);
                DatabaseTool.defaultEm.persist(entry);
                DatabaseTool.defaultEm.getTransaction().commit(); //提交事务
            } catch (Exception e) {
                e.printStackTrace();
                DatabaseTool.defaultEm.getTransaction().rollback(); //插入失败,回滚
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }


    /** 根据店铺名获取刷手信息 */
    public static List<CombineShopBuyer> getBuyerByShopName(String shopName){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from CombineShopBuyer u where u.shopName =?1");
            query.setParameter(1,shopName);
            List<CombineShopBuyer> combineShopBuyerList =(List<CombineShopBuyer>)query.getResultList();
            return combineShopBuyerList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
