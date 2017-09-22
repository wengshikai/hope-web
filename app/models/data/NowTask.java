package models.data;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shanmao on 15-12-5.
 */
@Entity //@Entity 标注为实体类
public class NowTask {
    @Id
    String realid;
    int id;
    String taskbookid;
    String keyword;
    String taskrequirement;
    double unit_price;
    int goods_number;
    double all_price;
    String pics;
    String shangjia_name;
    String shop_name;
    String shop_wangwang;
    String item_link;
    double pc_cost;
    double phone_cost;
    String shuashou_wangwang;
    int shuashoutaskbookid;

    public int getShuashoutaskbookid() {
        return shuashoutaskbookid;
    }

    public void setShuashoutaskbookid(int shuashoutaskbookid) {
        this.shuashoutaskbookid = shuashoutaskbookid;
    }

    public String getShuashou_wangwang() {
        return shuashou_wangwang;
    }

    public void setShuashou_wangwang(String shuashou_wangwang) {
        this.shuashou_wangwang = shuashou_wangwang;
    }




    public String getTaskbookid() {
        return taskbookid;
    }

    public void setTaskbookid(String taskbookid) {
        this.taskbookid = taskbookid;
    }


    public String getRealid() {
        return realid;
    }

    public void setRealid(String realid) {
        this.realid = realid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTaskrequirement() {
        return taskrequirement;
    }

    public void setTaskrequirement(String taskrequirement) {
        this.taskrequirement = taskrequirement;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
    }

    public double getAll_price() {
        return all_price;
    }

    public void setAll_price(double all_price) {
        this.all_price = all_price;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getShangjia_name() {
        return shangjia_name;
    }

    public void setShangjia_name(String shangjia_name) {
        this.shangjia_name = shangjia_name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_wangwang() {
        return shop_wangwang;
    }

    public void setShop_wangwang(String shop_wangwang) {
        this.shop_wangwang = shop_wangwang;
    }

    public String getItem_link() {
        return item_link;
    }

    public void setItem_link(String item_link) {
        this.item_link = item_link;
    }

    public double getPc_cost() {
        return pc_cost;
    }

    public void setPc_cost(double pc_cost) {
        this.pc_cost = pc_cost;
    }

    public double getPhone_cost() {
        return phone_cost;
    }

    public void setPhone_cost(double phone_cost) {
        this.phone_cost = phone_cost;
    }
}
