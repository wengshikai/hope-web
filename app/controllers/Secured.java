package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

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
