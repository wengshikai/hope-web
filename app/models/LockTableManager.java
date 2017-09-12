package models;
import models.util.DatabaseTool;




/**
 * Created by weng on 15-12-28.
 */
public class LockTableManager {
    public static boolean insert(String key,int value){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.dbtable.LockTable entry = new models.dbtable.LockTable();
            entry.setKey(key);
            entry.setValue(value);
            DatabaseTool.defaultEm.persist(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }finally {
        }
        return true;
    }

    public static boolean update(String key,int value){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, key);
            entry.setValue(value);
            DatabaseTool.defaultEm.merge(entry);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("calculate password error!",e);
            return false;
        }finally {

        }

        return true;
    }

    public static boolean isLock(String key){
        try {
            models.dbtable.LockTable entry = DatabaseTool.defaultEm.find(models.dbtable.LockTable.class, key);
            if(entry != null && entry.getValue() == 1){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            GlobalTool.loger.error("calculate password error!",e);
            return false;
        }finally {

        }
    }
}
