package models.excel;


/**
 * Created by shanmao on 17/9/9.
 *
 * 商家汇总信息
 */
public class ShopCollectionExcel {
    /** 店铺名 */
    private String shopName;

    /** 总单数 */
    private int orderNum;

    /** 总金额 */
    private int totalAmount;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
