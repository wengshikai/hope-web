package models.dbtable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by fengya on 15-12-16.
 */
@Entity //@Entity 标注为实体类
public class TaskTables {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    int taskid;
    String taskbookUuid;
    String taskbookName;
    int id;
    String keyword;
    String taskRequirement;
    double unitPrice;
    int goodsNumber;
    double allPrice;
    String pic1;
    String pic2;
    String pic3;
    String shopkeeperName;
    String shopName;
    String shopWangwang;
    String itemLink;
    double pcCost;
    double phoneCost;
    String buyerWangwang;
    int buyerTaskBookId;
    int subTaskbookId;

    public int getSubTaskbookId() {
        return subTaskbookId;
    }

    public void setSubTaskbookId(int subTaskbookId) {
        this.subTaskbookId = subTaskbookId;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getTaskbookUuid() {
        return taskbookUuid;
    }

    public void setTaskbookUuid(String taskbookUuid) {
        this.taskbookUuid = taskbookUuid;
    }

    public String getTaskbookName() {
        return taskbookName;
    }

    public void setTaskbookName(String taskbookName) {
        this.taskbookName = taskbookName;
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

    public String getTaskRequirement() {
        return taskRequirement;
    }

    public void setTaskRequirement(String taskRequirement) {
        this.taskRequirement = taskRequirement;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public double getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(double allPrice) {
        this.allPrice = allPrice;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getShopkeeperName() {
        return shopkeeperName;
    }

    public void setShopkeeperName(String shopkeeperName) {
        this.shopkeeperName = shopkeeperName;
    }

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

    public String getItemLink() {
        return itemLink;
    }

    public void setItemLink(String itemLink) {
        this.itemLink = itemLink;
    }

    public double getPcCost() {
        return pcCost;
    }

    public void setPcCost(double pcCost) {
        this.pcCost = pcCost;
    }

    public double getPhoneCost() {
        return phoneCost;
    }

    public void setPhoneCost(double phoneCost) {
        this.phoneCost = phoneCost;
    }

    public String getBuyerWangwang() {
        return buyerWangwang;
    }

    public void setBuyerWangwang(String buyerWangwang) {
        this.buyerWangwang = buyerWangwang;
    }

    public int getBuyerTaskBookId() {
        return buyerTaskBookId;
    }

    public void setBuyerTaskBookId(int buyerTaskBookId) {
        this.buyerTaskBookId = buyerTaskBookId;
    }
}