package models;

import models.data.NowTask;
import models.data.ShuashouUser;
import models.util.DatabaseTool;
import play.db.DB;

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
 * Created by fengya on 15-12-5.
 */
public class NowTaskManager {



    public static boolean insert(String realid,String taskbookid,int id,String keyword,String taskrequirement,
                                 double unit_price,int goods_number,double all_price,String pics,
                                 String shangjia_name,String shop_name,String shop_wangwang,
                                 String item_link,double pc_cost,double phone_cost){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            NowTask entry = new NowTask();
            entry.setRealid(realid);
            entry.setTaskbookid(taskbookid);
            entry.setId(id);
            entry.setKeyword(keyword);
            entry.setTaskrequirement(taskrequirement);
            entry.setUnit_price(unit_price);
            entry.setGoods_number(goods_number);
            entry.setAll_price(all_price);
            entry.setShangjia_name(shangjia_name);
            entry.setShop_name(shop_name);
            entry.setShop_wangwang(shop_wangwang);
            entry.setItem_link(item_link);
            entry.setPc_cost(pc_cost);
            entry.setPc_cost(phone_cost);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            DatabaseTool.defaultEm.getTransaction().rollback();
            return false;
        }
        return true;
    }

    public static boolean insert(NowTask entry){
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

    public static List<NowTask> getALl(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select u from NowTask u");
            List<NowTask>  entry =(List<NowTask>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static Map<String,ArrayList<NowTask>> getALlByShangjiaBook(){
        Map<String,ArrayList<NowTask>> ret = new  HashMap<String,ArrayList<NowTask>>();
        List<NowTask>  ntlist = getALl();
        for(NowTask nt:ntlist){
            if(!ret.containsKey(nt.getTaskbookid())){
                ArrayList<NowTask> ant = new ArrayList<NowTask>();
                ant.add(nt);
                ret.put(nt.getTaskbookid(),ant);
            }else{
                ret.get(nt.getTaskbookid()).add(nt);
            }
        }
        return ret;
    }

    public static List<String> getDianpu(){
        try {
            Query query = DatabaseTool.defaultEm.createQuery("select distinct u.shop_name from NowTask u");
            List<String>  entry =(List<String>)query.getResultList();
            return entry;
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return null;
        }
    }

    public static int getNeedShuashouNum(){
        DataSource ds = DB.getDataSource("default");
        Connection conn = null;
        Statement st = null;
        int resstr = 0;

        try {
            conn = ds.getConnection();
            ResultSet result = null;
            st = conn.createStatement();
            String sql  = "select max(c) from (select count(*) as c from NowTask group by taskbookid)";
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

    public static boolean setShuashouWangWangAndtaskbookid(String id,String wangwang,int taskid){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.NowTask  task = DatabaseTool.defaultEm.find(models.data.NowTask.class, id);
            task.setShuashou_wangwang(wangwang);
            task.setShuashoutaskbookid(taskid);
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
        Map<String,ArrayList<NowTask>> alltask = getALlByShangjiaBook();
        int index=0;
        for(Map.Entry<String,ArrayList<NowTask>> entry:alltask.entrySet()){
            ArrayList<NowTask> ant = entry.getValue();
            for(NowTask nt:ant){
                String ww = ssu.get(index).getWangwang();
                setShuashouWangWangAndtaskbookid(nt.getRealid(),ww,index+1);
                index = (index+1)%num;
            }
        }
        return true;
    }

}
