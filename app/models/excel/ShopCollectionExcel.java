package models.excel;


import lombok.Data;

/**
 * Created by shanmao on 17/9/9.
 *
 * 商家汇总信息
 */
@Data
public class ShopCollectionExcel {
    /** 店铺名 */
    private String shopName;

    /** 总单数 */
    private int orderNum;

    /** 总金额 */
    private int totalAmount;
}
