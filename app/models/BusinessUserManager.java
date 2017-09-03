package models;


import models.data.BusinessUser;
import models.util.DatabaseTool;


/**
 * Created by fengya on 15-11-15.
 */
public class BusinessUserManager {
    public static boolean insertUser(String userid, String name,int type,String qq,String weixin,
                                     String wangwang,String mobilephone,String telephone,int ali_heart,
                                     int ali_diamond,int ali_credit,String address,String introducer_id,
                                     String introducer_relationship,String zhifubao_account,String zhifubao_name,
                                     String wechat_account,String bank_account,String note){

        DatabaseTool.defaultEm.getTransaction().begin();
        BusinessUser buser = new BusinessUser();
        buser.setUserid(userid);
        buser.setName(name);
        buser.setType(type);
        buser.setQq(qq);
        buser.setWeixin(weixin);
        buser.setWangwang(wangwang);
        buser.setMobilephone(mobilephone);
        buser.setTelephone(telephone);
        buser.setAli_heart(ali_heart);
        buser.setAli_diamond(ali_diamond);
        buser.setAli_credit(ali_credit);
        buser.setAddress(address);
        buser.setIntroducer_id(introducer_id);
        buser.setIntroducer_relationship(introducer_relationship);
        buser.setZhifubao_account(zhifubao_account);
        buser.setZhifubao_name(zhifubao_name);
        buser.setWechat_account(wechat_account);
        buser.setBank_account(bank_account);
        buser.setNote(note);
        DatabaseTool.defaultEm.persist(buser);
        DatabaseTool.defaultEm.getTransaction().commit();
        return true;
    }

    public static BusinessUser getBusinessUser(String userid){

        BusinessUser buser = DatabaseTool.defaultEm.find(models.data.BusinessUser.class, userid);
        return buser;
    }


}
