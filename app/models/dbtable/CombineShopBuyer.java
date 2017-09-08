package models.dbtable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by shanmao on 17/9/8.
 */
@Entity
public class CombineShopBuyer {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    /** 买家Id,主键 */
    int id;
    String shopName;
    String buyerWangwang;
    int price;

    public int getId() {
        return id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBuyerWangwang() {
        return buyerWangwang;
    }

    public void setBuyerWangwang(String buyerWangwang) {
        this.buyerWangwang = buyerWangwang;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
