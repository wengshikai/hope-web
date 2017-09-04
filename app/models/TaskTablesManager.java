package models;

import models.dbtable.TaskTables;
import models.util.DatabaseTool;
import play.db.DB;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weng on 15-12-16.
 */


public class TaskTablesManager {
    public static boolean insert(String taskbookUuid,String taskbookName,int id,String keyword,String taskRequirement,
                                 double unitPrice,int goodsNumber,double allPrice,String pic1,String pic2,String pic3,
                                 String shopkeeperName,String shopName,String shopWangwang,
                                 String itemLink,double pcCost,double phoneCost,int subTaskbookId){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            TaskTables entry = new TaskTables();
            entry.setTaskbookUuid(taskbookUuid);
            entry.setTaskbookName(taskbookName);
            entry.setId(id);
            entry.setKeyword(keyword);
            entry.setTaskRequirement(taskRequirement);
            entry.setUnitPrice(unitPrice);
            entry.setGoodsNumber(goodsNumber);
            entry.setAllPrice(allPrice);
            entry.setPic1(pic1);
            entry.setPic2(pic2);
            entry.setPic3(pic3);
            entry.setShopkeeperName(shopkeeperName);
            entry.setShopName(shopName);
            entry.setShopWangwang(shopWangwang);
            entry.setItemLink(itemLink);
            entry.setPcCost(pcCost);
            entry.setPhoneCost(phoneCost);
            entry.setSubTaskbookId(subTaskbookId);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }

    public static boolean insert(TaskTables entry){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }

    public static List<TaskTables> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from TaskTables u");
            List<TaskTables> entry =(List<TaskTables>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static List<String> getALlTaskBookName(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select distinct u.taskbookName from TaskTables u");
            List<String> entry =(List<String>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static int getMaxBuyerTaskBookId(){
        EntityManager defaultEm = null;
        try {
            defaultEm = DatabaseTool.defaultFactory.createEntityManager();
            Query query = defaultEm.createQuery("select max(u.buyerTaskBookId)from TaskTables u");
//            int entry =(int)query.getSingleResult();
//            return entry;
            Integer entry =(Integer)query.getSingleResult();
            if(entry == null){
                return 0;
            }else{
                return entry;
            }
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return 0;
        }finally {
            if(defaultEm!=null){
                defaultEm.close();
            }

        }
    }

    public static boolean deleteByTaskbookUuid(String uuid){
        EntityManager em = DatabaseTool.defaultFactory.createEntityManager();
        try {
            Query query = em.createQuery("delete from TaskTables u where u.taskbookUuid=?1");
            query.setParameter(1,uuid);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
            em.close();
        }
    }

    public static boolean deleteByTaskbookName(String taskbookName){
        EntityManager em = DatabaseTool.defaultFactory.createEntityManager();
        try {
            Query query = em.createQuery("delete from TaskTables u where u.taskbookName=?1");
            query.setParameter(1,taskbookName);
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
            em.close();
        }
    }

    public static List<TaskTables> getShopkeeperBookByTaskbookName(String taskbookName){
        EntityManager em = DatabaseTool.defaultFactory.createEntityManager();
        try {
            Query query = em.createQuery("select u from TaskTables u where u.taskbookName=?1");
            query.setParameter(1,taskbookName);
            List<TaskTables> entrys = (List<TaskTables>)query.getResultList();
            return entrys;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }finally {
            em.close();
        }
    }

    public static Map<String,ArrayList<TaskTables>> getALlByShopkeeperTaskList(){
        Map<String,ArrayList<TaskTables>> ret = new HashMap<String,ArrayList<TaskTables>>();
        List<TaskTables>  ntlist = getALl();
        for(TaskTables nt:ntlist){
            if(!ret.containsKey(nt.getTaskbookUuid())){
                ArrayList<TaskTables> ant = new ArrayList<TaskTables>();
                ant.add(nt);
                ret.put(nt.getTaskbookUuid(),ant);
            }else{
                ret.get(nt.getTaskbookUuid()).add(nt);
            }
        }
        return ret;
    }

    public static int getNeedBuyerNum(){
        DataSource ds = DB.getDataSource("default");
        Connection conn = null;
        Statement st = null;
        int resstr = 0;

        try {
            conn = ds.getConnection();
            ResultSet result = null;
            st = conn.createStatement();
            String sql  = "select max(c) from (select count(*) as c from TaskTables group by taskbookUuid)";
            result = st.executeQuery(sql);
            int columnCount = result.getMetaData().getColumnCount();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resstr = result.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    GlobalTool.loger.error("something error", e1);
                }
            }
        }
        return resstr;
    }

    public static boolean setBuyerWangwangAndtaskbookid(int taskid,String buyerWangwang,int buyerTaskBookId){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.dbtable.TaskTables  task = DatabaseTool.defaultEm.find(models.dbtable.TaskTables.class, taskid);
            task.setBuyerWangwang(buyerWangwang);
            task.setBuyerTaskBookId(buyerTaskBookId);
            DatabaseTool.defaultEm.merge(task);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("calculate password error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }


    public static boolean updatenew(int num,List<models.dbtable.Buyer> ssu){
        Map<String,ArrayList<TaskTables>> alltask = getALlByShopkeeperTaskList();
        int index=0;
        for(Map.Entry<String,ArrayList<TaskTables>> entry:alltask.entrySet()){
            ArrayList<TaskTables> ant = entry.getValue();
            for(TaskTables nt:ant){
                String ww = ssu.get(index).getWangwang();
                setBuyerWangwangAndtaskbookid(nt.getTaskid(),ww,index+1);
                index = (index+1)%num;
            }
        }
        return true;
    }
}







