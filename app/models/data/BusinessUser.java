package models.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by fengya on 15-11-9.
 */

@Entity //@Entity 标注为实体类
public class BusinessUser {
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getWangwang() {
        return wangwang;
    }

    public void setWangwang(String wangwang) {
        this.wangwang = wangwang;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getAli_heart() {
        return ali_heart;
    }

    public void setAli_heart(int ali_heart) {
        this.ali_heart = ali_heart;
    }

    public int getAli_diamond() {
        return ali_diamond;
    }

    public void setAli_diamond(int ali_diamond) {
        this.ali_diamond = ali_diamond;
    }

    public int getAli_credit() {
        return ali_credit;
    }

    public void setAli_credit(int ali_credit) {
        this.ali_credit = ali_credit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroducer_id() {
        return introducer_id;
    }

    public void setIntroducer_id(String introducer_id) {
        this.introducer_id = introducer_id;
    }

    public String getIntroducer_relationship() {
        return introducer_relationship;
    }

    public void setIntroducer_relationship(String introducer_relationship) {
        this.introducer_relationship = introducer_relationship;
    }

    public String getZhifubao_account() {
        return zhifubao_account;
    }

    public void setZhifubao_account(String zhifubao_account) {
        this.zhifubao_account = zhifubao_account;
    }

    public String getZhifubao_name() {
        return zhifubao_name;
    }

    public void setZhifubao_name(String zhifubao_name) {
        this.zhifubao_name = zhifubao_name;
    }

    public String getWechat_account() {
        return wechat_account;
    }

    public void setWechat_account(String wechat_account) {
        this.wechat_account = wechat_account;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Id
    private String userid;

    private String name;

    private int type; // 1:刷手 2:店主 3:店铺工作人员

    private String qq;
    private String weixin;
    private String wangwang;
    private String mobilephone;
    private String telephone;
    private int ali_heart;
    private int ali_diamond;
    private int ali_credit;
    private String address;
    private String introducer_id;
    private String introducer_relationship;
    private String zhifubao_account;
    private String zhifubao_name;
    private String wechat_account;
    private String bank_account;
    private String note;
}
