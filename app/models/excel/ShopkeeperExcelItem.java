package models.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengya on 15-12-16.
 */
public class ShopkeeperExcelItem {
    String shopName;
    String shopWangwang;
    String shopkeeperName;
    String shopkeeperMobilephone;
    String shopkeeperQq;
    String shopkeeperWechat;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopWangwang() {
        return shopWangwang;
    }

    public void setShopWangwang(String shopWangwang) {
        this.shopWangwang = shopWangwang;
    }

    public String getShopkeeperName() {
        return shopkeeperName;
    }

    public void setShopkeeperName(String shopkeeperName) {
        this.shopkeeperName = shopkeeperName;
    }

    public String getShopkeeperMobilephone() {
        return shopkeeperMobilephone;
    }

    public void setShopkeeperMobilephone(String shopkeeperMobilephone) {
        this.shopkeeperMobilephone = shopkeeperMobilephone;
    }

    public String getShopkeeperQq() {
        return shopkeeperQq;
    }

    public void setShopkeeperQq(String shopkeeperQq) {
        this.shopkeeperQq = shopkeeperQq;
    }

    public String getShopkeeperWechat() {
        return shopkeeperWechat;
    }

    public void setShopkeeperWechat(String shopkeeperWechat) {
        this.shopkeeperWechat = shopkeeperWechat;
    }

    @Override
    public String toString(){
        return  shopName+" "+ shopWangwang+" "+
                shopkeeperName+" "+shopkeeperMobilephone+" "+
                shopkeeperQq+" "+ shopkeeperWechat;
    }

}
