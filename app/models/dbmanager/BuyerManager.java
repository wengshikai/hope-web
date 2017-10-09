package models.dbmanager;

import models.dbtable.Buyer;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by shanmao on 15-12-16.
 */
public class BuyerManager {
    public static boolean insert(String name,String wangwang,String mobilephone,int level){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            Buyer entry = new Buyer();
            try {
                entry.setName(name);
                entry.setWangwang(wangwang);
                entry.setMobilephone(mobilephone);
                entry.setLevel(level);
                DatabaseTool.defaultEm.persist(entry);
                DatabaseTool.defaultEm.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.loger.error("insert Buyer error: " + entry.getName() + " " + entry.getWangwang()
                        + " " + entry.getMobilephone()  + " " + entry.getLevel(),e);
                //插入失败,回滚
                DatabaseTool.defaultEm.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            DatabaseTool.defaultEm.getTransaction().rollback();
            GlobalTool.loger.error("insert Buyer error!",e);
            return false;
        }

        return true;
    }

    /** 获取所有刷手 */
    public static List<Buyer> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u");
            List<Buyer> entry =(List<Buyer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("insert Buyer error!",e);
            return null;
        }
    }


    /** 获取所有刷手分组 */
    public static List<Integer> getALlTeams(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select distinct level from Buyer");
            List<Integer> teams =(List<Integer>)query.getResultList();
            return teams;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }


    /** 获取指定小组的所有刷手 */
    public static List<Buyer> getALlByTeam(int level){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u where u.level = " + level);
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
}
