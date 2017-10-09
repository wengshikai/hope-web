package controllers;

import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by shanmao on 15-10-28.
 */
public class Secured extends Security.Authenticator{
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("name");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
}
