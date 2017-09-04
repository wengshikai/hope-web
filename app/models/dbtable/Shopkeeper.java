package models.dbtable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Created by weng on 15-12-4.
 */

@Entity //@Entity 标注为实体类
public class Shopkeeper {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    int shopkeeperId;
    protected String name;
    protected String mobilephone;
    String qq;
    String wechat;

    public int getShopkeeperId() {
        return shopkeeperId;
    }

    public void setShopkeeperId(int shopkeeperId) {
        this.shopkeeperId = shopkeeperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    @Override
    public String toString(){
        return "shopkeeperId: " + shopkeeperId+" name: "+name+" mobilephone: "+mobilephone+" qq: "+qq+" wechat: "+wechat;
    }
}

