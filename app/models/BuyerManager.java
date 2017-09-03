package models;

import models.dbtable.Buyer;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by fengya on 15-12-16.
 */
public class BuyerManager {
    public static boolean insert(String name,String wangwang,String mobilephone,int level){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            Buyer  entry = new Buyer();
            entry.setName(name);
            entry.setWangwang(wangwang);
            entry.setMobilephone(mobilephone);
            entry.setLevel(level);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }
        return true;
    }

    public static Buyer getByWangwang(String wangwang){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u where u.wangwang=?1");
            query.setParameter(1,wangwang);
            Buyer  entry =(Buyer)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<Buyer> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u");
            List<Buyer> entry =(List<Buyer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static Long getBuyerCount(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select count(u) from Buyer u");
            Long entry =(Long)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return 0L;
        }
    }

    public static List<Buyer> getALlWithNot(List<String> names){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u where u.wangwang not in :names");
            query.setParameter("names",names);
            List<Buyer>  entry =(List<Buyer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }
}
