package models;

import models.data.User;
import models.util.DatabaseTool;
import util.Security;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by fengya on 15-10-25.
 */
public class UserManager {
    private static Random random = new Random();
    public static boolean insertUser(String name,String password){
        int salt = random.nextInt();
        String md5password = null;
        try {
            md5password = Security.getMD5(name+password+salt);
            DatabaseTool.userEm.getTransaction().begin();
            models.data.User  user = new User();
            user.setName(name);
            user.setSalt("" + salt);
            user.setPassword(md5password);
            DatabaseTool.userEm.persist(user);
            DatabaseTool.userEm.getTransaction().commit();
        } catch (Exception e) {
            GlobalTool.loger.error("calculate password error!",e);
            return false;
        }finally {
        }

        return true;
    }

    public static boolean checkUser(String name,String password){
        if(name == null || password == null || name.equals("") || password.equals("")){
            return false;
        }
        models.data.User user = DatabaseTool.userEm.find(models.data.User.class, name);
        String md5password = null;
        try {
            md5password = Security.getMD5(name+password+user.getSalt());
        } catch (NoSuchAlgorithmException e) {
            GlobalTool.loger.error("calculate password error!",e);
            return false;
        }
        if(md5password.equals(user.getPassword())){
            return true;
        }else{
            return false;
        }
    }

    public static boolean updateUser(String name,String password){
        int salt = random.nextInt();
        String md5password = null;
        try {
            md5password = Security.getMD5(name+password+salt);
            DatabaseTool.userEm.getTransaction().begin();
            models.data.User  user = DatabaseTool.userEm.find(models.data.User.class, name);
            user.setSalt("" + salt);
            user.setPassword(md5password);
            DatabaseTool.userEm.merge(user);
        } catch (Exception e) {
            GlobalTool.loger.error("calculate password error!",e);
            return false;
        }finally {
            DatabaseTool.userEm.getTransaction().commit();
        }

        return true;
    }
}
