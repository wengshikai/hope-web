package models.data;

import lombok.Data;

/**
 * Created by shanmao on 15-9-29.
 */
@Data
public class EmailBean{
    private String[] to;
    private String subject;
    private String msg;
}