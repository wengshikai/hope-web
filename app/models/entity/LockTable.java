package models.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by shanmao on 15-12-28.
 *
 * 任务书上传锁定状态
 */
@Entity
@Data
public class LockTable {
    @Id
    String key;
    int value;
}

