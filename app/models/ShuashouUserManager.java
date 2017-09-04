package models;

import models.data.ShuashouUser;
import models.util.DatabaseTool;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by weng on 15-12-4.
 */
public class ShuashouUserManager {
    public static boolean insert(String id,String name,String wangwang,String mobilephone,int level){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.ShuashouUser  entry = new ShuashouUser();
            entry.setId(id);
            entry.setName(name);
            entry.setWangwang(wangwang);
            entry.setMobilephone(mobilephone);
            entry.setLevel(level);
            DatabaseTool.defaultEm.persist(entry);
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
            DatabaseTool.defaultEm.getTransaction().commit();
        }
        return true;
    }

    public static ShuashouUser getByWangwang(String wangwang){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from ShuashouUser u where u.wangwang=?1");
            query.setParameter(1,wangwang);
            ShuashouUser  entry =(ShuashouUser)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<ShuashouUser> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from ShuashouUser u");
            List<ShuashouUser>  entry =(List<ShuashouUser>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<ShuashouUser> getALlWithNot(List<String> names){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from ShuashouUser u where u.wangwang not in :names");
            query.setParameter("names",names);
            List<ShuashouUser>  entry =(List<ShuashouUser>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }
}
