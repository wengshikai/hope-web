package models.dbtable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by weng on 15-12-15.
 */
@Entity //@Entity 标注为实体类
public class Shop {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    int shopId;
    int shopkeeperId;
    String shopName;
    String shopWangwang;

    public String getShopWangwang() {
        return shopWangwang;
    }

    public void setShopWangwang(String shopWangwang) {
        this.shopWangwang = shopWangwang;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getShopkeeperId() {
        return shopkeeperId;
    }

    public void setShopkeeperId(int shopkeeperId) {
        this.shopkeeperId = shopkeeperId;
    }

    @Override
    public String toString(){
        return "shopId: " + shopId+" shopkeeperId: "+shopkeeperId;
    }
}
