package models;

import models.data.Dianpu;
import models.data.ShangjiaUser;
import models.data.ShuashouUser;
import models.util.DatabaseTool;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by fengya on 15-12-4.
 */
public class DianpuManager {
    public static boolean insert(String id,String wangwang){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.Dianpu  entry = new Dianpu();
            entry.setId(id);
            entry.setWangwang(wangwang);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }

    public static Dianpu get(String id){
        try {
            models.data.Dianpu entry = DatabaseTool.userEm.find(models.data.Dianpu.class, id);
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static ShangjiaUser getShangjia(String id){
        Dianpu d = get(id);
        ShangjiaUser sj = ShangjiaUserManager.getByWangwang(d.getWangwang());
        return sj;
    }

    public static List<Dianpu> getByWangwang(String wangwang){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Dianpur u where u.wangwang=?1");
            query.setParameter(1,wangwang);
            List<Dianpu> entry =(List<Dianpu>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static boolean insertWithCheck(String id,String wangwang){
        ShangjiaUser sj = ShangjiaUserManager.getByWangwang(wangwang);
        if(sj == null){
            return false;
        }else{
            return insert(id,wangwang);
        }
    }

}
