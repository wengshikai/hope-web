package models.dbtable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by shanmao on 15-12-16.
 *
 * 买家(刷手)实体
 */
@Entity
public class Buyer {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    /** 买家Id,主键 */
    int id;
    /** 买家姓名 */
    protected String name;
    /** 买家旺旺名 */
    protected String wangwang;
    /** 买家手机号 */
    protected String mobilephone;
    /** 买家所在的组 */
    protected int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWangwang() {
        return wangwang;
    }

    public void setWangwang(String wangwang) {
        this.wangwang = wangwang;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
