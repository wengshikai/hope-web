package models;

import models.data.Dianpu;
import models.data.ShangjiaUser;
import models.util.DatabaseTool;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by weng on 15-12-4.
 */
public class ShangjiaUserManager {

    public static boolean insert(String id,String name,String wangwang,String mobilephone){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.ShangjiaUser  entry = new ShangjiaUser();
            entry.setId(id);
            entry.setName(name);
            entry.setWangwang(wangwang);
            entry.setMobilephone(mobilephone);
            DatabaseTool.defaultEm.persist(entry);
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
            DatabaseTool.defaultEm.getTransaction().commit();
        }
        return true;
    }

    public static ShangjiaUser getByWangwang(String wangwang){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from ShangjiaUser u where u.wangwang=?1");
            query.setParameter(1,wangwang);
            ShangjiaUser  entry =(ShangjiaUser)query.getSingleResult();
            return entry;
        }catch (javax.persistence.NoResultException e1){
            return null;
        }catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<ShangjiaUser> getAllShangjia(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from ShangjiaUser u");
            List<ShangjiaUser>  entry =(List<ShangjiaUser>)query.getResultList();
            return entry;
        }catch (javax.persistence.NoResultException e1){
            return null;
        }catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<Dianpu> getDianpuByWangwang(String wangwang){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Dianpu u where u.wangwang=?1");
            query.setParameter(1,wangwang);
            List<Dianpu> entry =(List<Dianpu>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }
}
