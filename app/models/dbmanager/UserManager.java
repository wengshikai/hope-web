package models.dbmanager;

import models.dbtable.User;
import models.util.DatabaseTool;
import util.Security;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by shanmao on 15-10-25.
 */
public class UserManager {
    private static Random random = new Random();

    /** 新增用户 */
    public static boolean insertUser(String name,String password){
        int salt = random.nextInt();
        try {
            String md5password = Security.getMD5(name+password+salt);
            DatabaseTool.userEm.getTransaction().begin();
            try {
                User user = new User();
                user.setName(name);
                user.setSalt("" + salt);
                user.setPassword(md5password);
                DatabaseTool.userEm.persist(user);
                DatabaseTool.userEm.getTransaction().commit();
            } catch (Exception e) {
                GlobalTool.logger.error("insert User error!",e);
                //插入失败,事务回滚
                DatabaseTool.userEm.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            GlobalTool.logger.error("insert User error!",e);
            return false;
        }

        return true;
    }


    /** 校验用户密码 */
    public static boolean checkUser(String name,String password){
        //检查入参
        if(name == null || password == null || name.equals("") || password.equals("")){
            return false;
        }

        User user = DatabaseTool.userEm.find(User.class, name);
        String md5password;
        try {
            //计算密文
            md5password = Security.getMD5(name+password+user.getSalt());
        } catch (NoSuchAlgorithmException e) {
            GlobalTool.logger.error("calculate password error!",e);
            return false;
        }

        //返回密码校验结果
        return md5password.equals(user.getPassword());
    }
}
