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

    /** 刷手姓名 */
    protected String name;

    /** 刷手旺旺名 */
    protected String wangwang;

    /** 刷手手机号 */
    protected String mobilephone;

    /** 刷手分组 */
    protected int level;
}
