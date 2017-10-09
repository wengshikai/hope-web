package controllers;

import models.dbmanager.UserManager;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {


    @Security.Authenticated(Secured.class)
    public Result index() {
        return ok(index.render());
    }

    public static class Login {

        public String name;
        public String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String validate() {
            if (!UserManager.checkUser(name, password)) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public Result login() {
        return ok(login.render(Form.form(Login.class)));
    }


    public Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
                session().clear();
                session("name", loginForm.get().name);
                return redirect(
                        routes.Application.index()
                );
        }
    }

    public  Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }

}
