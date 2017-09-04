package models.data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by weng on 15-12-5.
 */

@Entity //@Entity 标注为实体类
public class TaskHistory {
    @Id
    String id;
    String shuashou;
    String dianpu;
    int timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShuashou() {
        return shuashou;
    }

    public void setShuashou(String shuashou) {
        this.shuashou = shuashou;
    }

    public String getDianpu() {
        return dianpu;
    }

    public void setDianpu(String dianpu) {
        this.dianpu = dianpu;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
