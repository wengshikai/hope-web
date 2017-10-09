package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.dbmanager.GlobalTool;
import org.apache.commons.mail.EmailException;
import play.libs.Json;
import play.mvc.*;
import util.SendEmail;
import models.data.EmailBean;

/**
 * Created by shanmao on 15-9-29.
 */
public class Service  extends Controller {

    // curl  --request POST
    // --data '{"to": ["xxxx@mogujie.com","xxxx@gmail.com"],"subject":"测试邮件","msg":"测试邮件内容"}'
    // http://127.0.0.1:9000/hope/service/sendmail
    @BodyParser.Of(BodyParser.Raw.class)
    public Result sendemail() {
        play.mvc.Http.RawBuffer rb = request().body().asRaw();
        if(rb == null){
            return badRequest("Expecting Json data");
        }
        JsonNode json = Json.parse(rb.asBytes());
        if(json == null) {
            return badRequest("Expecting Json data");
        } else {
            EmailBean eb;
            eb = Json.fromJson(json,EmailBean.class);
            try {
                SendEmail.SendEmail(eb.getSubject(),eb.getMsg(),eb.getTo());
                return ok("ok");
            } catch (EmailException e) {
                GlobalTool.loger.error("send email error",e);
                return badRequest("Expecting Json data");
            }
        }

    }
}
