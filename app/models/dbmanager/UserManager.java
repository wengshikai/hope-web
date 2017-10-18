package models.dbmanager;

import models.entity.User;
import models.util.DatabaseTool;
import util.Security;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by shanmao on 15-10-25.
 */
public class UserManager {
    private static Random random = new Random();

    /** 创建用户 */
    public static boolean insertUser(String name,String password){
        int salt = random.nextInt();
        String md5password;
        try {
            md5password = Security.getMD5(name+password+salt);
        } catch (NoSuchAlgorithmException e) {
            GlobalTool.logger.error("生成密码失败!",e);
            return false;
        }

        DatabaseTool.userEm.getTransaction().begin(); //启动事务
        try {
            User user = new User();
            user.setName(name);
            user.setSalt("" + salt);
            user.setPassword(md5password);
            DatabaseTool.userEm.persist(user);
            DatabaseTool.userEm.getTransaction().commit(); //提交事务
        } catch (Exception e) {
            GlobalTool.logger.error("创建用户失败!",e);
            DatabaseTool.userEm.getTransaction().rollback(); //插入失败,事务回滚
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

        //查询用户
        User user = DatabaseTool.userEm.find(User.class, name);
        try {
            //计算密文
            String  md5password = Security.getMD5(name+password+user.getSalt());
            //返回密码校验结果
            return md5password.equals(user.getPassword());
        } catch (NoSuchAlgorithmException e) {
            GlobalTool.logger.error("计算密文失败!",e);
            return false;
        }
    }
}
