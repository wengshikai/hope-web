package models.dbmanager;

import models.entity.TaskTables;
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
 * Created by shanmao on 15-12-16.
 */


public class TaskTablesManager {

    /** 添加任务 */
    public static boolean insert(int ShopId, String taskBookUuid,String taskBookName,int id,String keyword,String taskRequirement,
                                 double unitPrice,int goodsNumber,double allPrice,String pic1,String pic2,String pic3,
                                 String shopkeeperName,String shopName,String shopWangwang,
                                 String itemLink,double pcCost,double phoneCost,int subTaskBookId){
        try {
            DatabaseTool.defaultEm.getTransaction().begin(); //启动事务
            try {
                TaskTables entry = new TaskTables();
                entry.setShopId(ShopId);
                entry.setTaskBookUuid(taskBookUuid);
                entry.setTaskBookName(taskBookName);
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
                entry.setSubTaskBookId(subTaskBookId);
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


    /** 分配刷手 */
    public static boolean setBuyerAndTaskBookId(int taskId,String buyerWangwang,int buyerTeam, int buyerTaskBookId){
        try {
            DatabaseTool.defaultEm.getTransaction().begin(); //启动事务
            try {
                models.entity.TaskTables task = DatabaseTool.defaultEm.find(models.entity.TaskTables.class, taskId);
                task.setBuyerWangwang(buyerWangwang);
                task.setBuyerTeam(buyerTeam);
                task.setBuyerTaskBookId(buyerTaskBookId);
                DatabaseTool.defaultEm.merge(task);
                DatabaseTool.defaultEm.getTransaction().commit(); //提交事务
            } catch (Exception e) {
                e.printStackTrace();
                DatabaseTool.defaultEm.getTransaction().rollback(); //更新失败,回滚
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /** 获取某个商家任务书的任务数量 */
    public static Long getTaskCountByTaskBookName(String ShopName){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select count(u) from TaskTables u where u.taskBookName =?1");
            query.setParameter(1,ShopName);
            Long entry =(Long)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    /** 获取所有的任务数量 */
    public static Long getAllTaskCount(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select count(u) from TaskTables u");
            Long entry =(Long)query.getSingleResult();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    /** 获取所有商家任务书名称列表 */
    public static List<String> getALlTaskBookName(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select distinct u.taskBookName from TaskTables u");
            List<String> entry =(List<String>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.logger.error("获取商家任务书名称列表出错!",e);
            return null;
        }
    }


    /** 获取某个商家任务书的所有任务 */
    public static List<TaskTables> getTasksByTaskBookName(String ShopName){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from TaskTables u where u.taskBookName =?1");
            query.setParameter(1,ShopName);
            List<TaskTables> entry =(List<TaskTables>)query.getResultList();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
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
            GlobalTool.logger.error("something error!",e);
            return 0;
        }finally {
            if(defaultEm!=null){
                defaultEm.close();
            }

        }
    }


    public static boolean deleteByTaskBookName(String taskBookName){
        EntityManager em = DatabaseTool.defaultFactory.createEntityManager();
        try {
            Query query = em.createQuery("delete from TaskTables u where u.taskBookName=?1");
            query.setParameter(1,taskBookName);
            em.getTransaction().begin();
            try {
                query.executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.logger.error("something error!",e);
                //更新失败,事务回滚
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return false;
        }finally {
            em.close();
        }

        return true;
    }

    public static List<TaskTables> getShopkeeperBookByTaskBookName(String taskBookName){
        EntityManager em = DatabaseTool.defaultFactory.createEntityManager();
        try {
            Query query = em.createQuery("select u from TaskTables u where u.taskBookName=?1");
            query.setParameter(1,taskBookName);
            List<TaskTables> taskTablesList = (List<TaskTables>)query.getResultList();
            return taskTablesList;
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return null;
        }finally {
            em.close();
        }
    }


    /** 计算总共需要多少刷手 */
    public static int getNeedBuyerNum(){
        DataSource ds = DB.getDataSource("default");
        Connection conn = null;
        Statement st = null;
        int resstr = 0;

        try {
            conn = ds.getConnection();
            ResultSet result = null;
            st = conn.createStatement();
            String sql  = "select max(c) from (select count(*) as c from TaskTables group by taskBookUuid)";
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
                    GlobalTool.logger.error("something error", e1);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    GlobalTool.logger.error("something error", e1);
                }
            }
        }
        return resstr;
    }


    /** 获取所有商家任务 */
    public static List<TaskTables> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from TaskTables u");
            List<TaskTables> entry =(List<TaskTables>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return null;
        }
    }


    /** 获取所有的商家任务，并且按"商家任务书Id"分组 */
    public static Map<String,ArrayList<TaskTables>> getALlByShopkeeperTaskList(){
        Map<String,ArrayList<TaskTables>> ret = new HashMap<String,ArrayList<TaskTables>>();
        List<TaskTables>  ntlist = getALl();
        for(TaskTables nt:ntlist){
            if(!ret.containsKey(nt.getTaskBookUuid())){
                ArrayList<TaskTables> ant = new ArrayList<TaskTables>();
                ant.add(nt);
                ret.put(nt.getTaskBookUuid(),ant);
            }else{
                ret.get(nt.getTaskBookUuid()).add(nt);
            }
        }
        return ret;
    }


    /** 分配任务 */
    public static boolean updateNew(int num,List<models.entity.Buyer> ssu){
        Map<String,ArrayList<TaskTables>> alltask = getALlByShopkeeperTaskList();
        int index=0;
        for(Map.Entry<String,ArrayList<TaskTables>> entry:alltask.entrySet()){
            ArrayList<TaskTables> ant = entry.getValue();
            for(TaskTables nt:ant){
                String ww = ssu.get(index).getWangwang();
                int buyerTeam = ssu.get(index).getTeam();
                //分配刷手
                setBuyerAndTaskBookId(nt.getTaskId(),ww,buyerTeam,index+1);
                index = (index+1)%num;
            }
        }
        return true;
    }
}







