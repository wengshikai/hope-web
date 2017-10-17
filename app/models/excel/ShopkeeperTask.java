package models.excel;

import lombok.Data;
import models.entity.TaskTables;

/**
 * Created by shanmao on 15-12-10.
 */
@Data
public class ShopkeeperTask {
    int taskId;
    String taskBookUuid;
    String taskBookName;
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
    int buyerTeam;
    int buyerTaskBookId;
    int subTaskBookId;

    //前端打包有些bug,需要手动添加get函数
    public int getSubTaskBookId() {
        return subTaskBookId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskBookUuid() {
        return taskBookUuid;
    }

    public String getTaskBookName() {
        return taskBookName;
    }

    public int getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getTaskRequirement() {
        return taskRequirement;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public double getAllPrice() {
        return allPrice;
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

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getShopkeeperName() {
        return shopkeeperName;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopWangwang() {
        return shopWangwang;
    }

    public String getItemLink() {
        return itemLink;
    }

    public double getPcCost() {
        return pcCost;
    }

    public double getPhoneCost() {
        return phoneCost;
    }

    public String getBuyerWangwang() {
        return buyerWangwang;
    }

    public int getBuyerTeam() {
        return buyerTeam;
    }

    public int getBuyerTaskBookId() {
        return buyerTaskBookId;
    }

    public void initByTables(TaskTables task){
        taskId = task.getTaskId();
        taskBookUuid = task.getTaskBookUuid();
        taskBookName = task.getTaskBookName();
        id = task.getId();
        keyword = task.getKeyword();
        taskRequirement = task.getTaskRequirement();
        unitPrice = task.getUnitPrice();
        goodsNumber = task.getGoodsNumber();
        allPrice = task.getAllPrice();
        pic1 = task.getPic1();
        pic2 = task.getPic2();
        pic3 = task.getPic3();
        shopkeeperName = task.getShopkeeperName();
        shopName = task.getShopName();
        shopWangwang = task.getShopWangwang();
        itemLink = task.getItemLink();
        pcCost = task.getPcCost();
        phoneCost = task.getPhoneCost();
        buyerWangwang = task.getBuyerWangwang();
        buyerTeam = task.getBuyerTeam();
        buyerTaskBookId = task.getBuyerTaskBookId();
    }

}