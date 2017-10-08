package models.excel;

import models.dbtable.TaskTables;

/**
 * Created by shanmao on 15-12-10.
 */

public class ShopkeeperTask {
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
    int buyerTeam;
    int buyerTaskBookId;
    int subTaskBookId;

    public int getSubTaskBookId() {
        return subTaskBookId;
    }

    public void setSubTaskBookId(int subTaskBookId) {
        this.subTaskBookId = subTaskBookId;
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

    public void setBuyerTeam(int buyerTeam) {
        this.buyerTeam = buyerTeam;
    }

    public int getBuyerTeam() {
        return buyerTeam;
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

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("taskid:"+taskid+" , ");
        sb.append("taskbookUuid:"+taskbookUuid+" , ");
        sb.append("taskbookName:"+taskbookName+" , ");
        sb.append("id:"+id+" , ");
        sb.append("keyword:"+keyword+" , ");
        sb.append("taskRequirement:"+taskRequirement+" , ");
        sb.append("unitPrice:"+unitPrice+" , ");
        sb.append("goodsNumber:"+goodsNumber+" , ");
        sb.append("allPrice:"+allPrice+" , ");
        sb.append("pic1:"+pic1+" , ");
        sb.append("pic2:"+pic2+" , ");
        sb.append("pic3:"+pic3+" , ");
        sb.append("shopkeeperName:"+shopkeeperName+" , ");
        sb.append("shopName:"+shopName+" , ");
        sb.append("shopWangwang:"+shopWangwang+" , ");
        sb.append("itemLink:"+itemLink+" , ");
        sb.append("pcCost:"+pcCost+" , ");
        sb.append("phoneCost:"+phoneCost+" , ");
        sb.append("buyerWangwang:"+buyerWangwang+" , ");
        sb.append("buyerTaskBookId:"+buyerTaskBookId+" , ");
        return sb.toString();
    }

    public void initByTables(TaskTables task){
        taskid = task.getTaskid();
        taskbookUuid = task.getTaskbookUuid();
        taskbookName = task.getTaskbookName();
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