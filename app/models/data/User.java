package models.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 系统用户名实体类，用于登录校验
 *
 * Created by shanmao on 15-10-25.
 */

@Entity
@Data
public class User {
    @Id
    private String name;

    private String salt;

    private String password;
}
