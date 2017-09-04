package models;
import models.GlobalTool;
import models.data.UserId;
import models.util.DatabaseTool;


/**
 * Created by weng on 15-12-4.
 */
public class UserIdManager {
    public static boolean insertId(String tablename,long id){
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.UserId  userid = new UserId();
            userid.setTablename(tablename);
            userid.setId(id);
            DatabaseTool.defaultEm.persist(userid);
            DatabaseTool.defaultEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("something error!",e);
            return false;
        }
        return true;
    }

    public static long incrAndGetId(String tablename){
        long id;
        try {
            DatabaseTool.defaultEm.getTransaction().begin();
            models.data.UserId  userid = DatabaseTool.defaultEm.find(models.data.UserId.class, tablename);
            id = userid.getId()+1;
            userid.setId(id);
            DatabaseTool.defaultEm.merge(userid);
        } catch (Exception e) {
            GlobalTool.loger.error("error!",e);
            return 0;
        }finally {
            DatabaseTool.defaultEm.getTransaction().commit();
        }

        return id;
    }
}
