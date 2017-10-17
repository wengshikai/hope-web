package models.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by shanmao on 17/9/8.
 */
@Entity
@Data
public class CombineShopBuyer {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    /** 买家Id,主键 */
    int id;

    /** 店铺名 */
    String shopName;

    /** 刷手旺旺名 */
    String buyerWangwang;

    /** 价格 */
    int price;
}
