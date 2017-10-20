package models.dbmanager;

import models.entity.Buyer;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by shanmao on 15-12-16.
 */
public class BuyerManager {


    /** 插入一个刷手 */
    public static boolean insert(String name,String wangwang,String mobilephone,int team){
        try {
            DatabaseTool.defaultEm.getTransaction().begin(); //启动事务
            Buyer entry = new Buyer();
            try {
                entry.setName(name);
                entry.setWangwang(wangwang);
                entry.setMobilephone(mobilephone);
                entry.setTeam(team);
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


    /** 获取所有刷手 */
    public static List<Buyer> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u");
            List<Buyer> entry =(List<Buyer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 获取所有分组 */
    public static List<Integer> getALlTeams(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select distinct team from Buyer");
            List<Integer> entry =(List<Integer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 获取指定小组的所有刷手 */
    public static List<Buyer> getALlByTeam(int team){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Buyer u where u.team = " + team);
            List<Buyer> entry =(List<Buyer>)query.getResultList();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 获取指定小组的刷手数量 */
    public static Long getBuyerConutByTeam(int team){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select count(u) from Buyer u where u.team = " + team);
            Long entry =(Long)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 获取刷手的总数量 */
    public static Long getBuyerCount(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select count(u) from Buyer u");
            Long entry =(Long)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
