package models.dbtable;
import lombok.Data;

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
@Data
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
}
