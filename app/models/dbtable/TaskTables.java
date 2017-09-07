package models.dbtable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by weng on 15-12-16.
 *
 * 商家任务实体
 */
@Entity
public class TaskTables {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    /** 任务Id,主键 */
    int taskid;

    /** 商家任务书Id */
    String taskbookUuid;
    /** 商家任务书名称 */
    String taskbookName;

    /** 商家姓名 */
    String shopkeeperName;
    /** 店铺名 */
    String shopName;
    /** 商家旺旺名 */
    String shopWangwang;

    /** 在当前任务书中的序号 */
    int id;
    /** 关键词 */
    String keyword;
    /** 要求 */
    String taskRequirement;
    /** 单价 */
    double unitPrice;
    /** 数量 */
    int goodsNumber;
    /** 总价 */
    double allPrice;
    /** 图片1 */
    String pic1;
    /** 图片2 */
    String pic2;
    /** 图片3 */
    String pic3;

    /** 商品链接 */
    String itemLink;
    /** pc端价格 */
    double pcCost;
    /** 手机端价格 */
    double phoneCost;

    /** 买家旺旺名 */
    String buyerWangwang;
    /** 买家任务书Id */
    int buyerTaskBookId;
    /** 买家任务书子Id */
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