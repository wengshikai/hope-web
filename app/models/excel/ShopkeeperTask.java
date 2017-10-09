package models.excel;

import lombok.Data;
import models.dbtable.TaskTables;

/**
 * Created by shanmao on 15-12-10.
 */
@Data
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