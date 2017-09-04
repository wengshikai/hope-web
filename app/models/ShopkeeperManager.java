package models;


import models.dbtable.Shopkeeper;
import models.util.DatabaseTool;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;


/**
 * Created by weng on 15-12-13.
 */
public class ShopkeeperManager {
    public static boolean insert(String name,String mobilephone,String qq,String wechat){
        EntityManager defaultEm = null;
        try {
            defaultEm = DatabaseTool.getDefaultEntityManager();
            defaultEm.getTransaction().begin();
            Shopkeeper entry = new Shopkeeper();
            entry.setName(name);
            entry.setMobilephone(mobilephone);
            entry.setQq(qq);
            entry.setWechat(wechat);
            defaultEm.persist(entry);
            defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
            defaultEm.close();
        }
        return true;
    }

    public static boolean insert(Shopkeeper entry){
        return insert(entry.getName(),entry.getMobilephone(),entry.getQq(),entry.getWechat());
    }

    public static Shopkeeper getByNameAndPhnoe(String name,String mobilephone){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Shopkeeper u where u.name=?1" +
                    " and u.mobilephone = ?2");
            query.setParameter(1,name);
            query.setParameter(2,mobilephone);
            Shopkeeper  entry =(Shopkeeper)query.getSingleResult();
            return entry;
        }catch (javax.persistence.NoResultException e1){
            return null;
        }catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<Shopkeeper> getAllShopkeeper(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from Shopkeeper u");
            List<Shopkeeper>  entry =(List<Shopkeeper>)query.getResultList();
            return entry;
        }catch (javax.persistence.NoResultException e1){
            return null;
        }catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }
}
