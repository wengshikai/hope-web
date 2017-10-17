package models.dbmanager;
import models.util.DatabaseTool;




/**
 * Created by shanmao on 15-12-28.
 */
public class LockTableManager {
    public static boolean insert(String key,int value){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            try {
                models.entity.LockTable entry = new models.entity.LockTable();
                entry.setKey(key);
                entry.setValue(value);
                DatabaseTool.defaultEm.persist(entry);
                DatabaseTool.defaultEm.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.logger.error("something error!",e);
                //插入失败,回滚
                DatabaseTool.defaultEm.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("something error!",e);
            return false;
        }

        return true;
    }

    public static boolean update(String key,int value){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            try {
                models.entity.LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, key);
                entry.setValue(value);
                DatabaseTool.defaultEm.merge(entry);
                DatabaseTool.defaultEm.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.logger.error("something error!",e);
                //更新失败,回滚
                DatabaseTool.defaultEm.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("update lockTable error!",e);
            return false;
        }

        return true;
    }

    public static boolean isLock(String key){
        try {
            models.entity.LockTable entry = DatabaseTool.defaultEm.find(models.entity.LockTable.class, key);
            if(entry != null && entry.getValue() == 1){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("find lockTable error!",e);
            return false;
        }
    }
}
