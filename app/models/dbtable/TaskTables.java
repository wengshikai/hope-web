package models.dbtable;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by shanmao on 15-12-16.
 *
 * 商家任务实体
 */
@Entity
@Data
public class TaskTables {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    /** 任务Id,主键 */
    int taskid;

    /** 商家任务书Id */
    String taskbookUuid;
    /** 商家任务书名称 */
    String taskbookName;

    /** 商家编号 */
    int shopId;
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

    /** 刷手旺旺名 */
    String buyerWangwang;
    /** 刷手分组 */
    int buyerTeam;
    /** 刷手任务书Id */
    int buyerTaskBookId;
    /** 刷手任务书子Id */
    int subTaskbookId;
}