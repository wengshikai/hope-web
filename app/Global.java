/**
 * Created by fengya on 15-10-13.
 */
import models.*;
import models.util.DatabaseTool;
import play.*;
import play.mvc.*;
import play.libs.F.*;
import views.html.http404;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {

    public Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return Promise.<Result>pure(notFound(
                http404.render(request.uri())
        ));
    }

    @Override
    public void onStart(Application app) {

    }

}