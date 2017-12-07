package controllers.gui.manage;

import controllers.Config;
import controllers.auth.AdminSecured;
import models.entities.Admin;
import org.jetbrains.annotations.Nullable;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.manage.LoginService;

import javax.inject.Inject;

public class LoginController extends Controller {

    private final Form<Admin> form;
    private final LoginService loginService;
//    private ValidationError error;

    @Inject
    public LoginController(FormFactory formFactory, LoginService loginService) {
        this.form = formFactory.form(Admin.class);
        this.loginService = loginService;
    }

    /**
     * Login page.
     */
    public Result index() {
        return ok(
                views.html.manage.login.render(this.form)
        );
    }

    /**
     * Handle login form submission.
     */
    public Result login() {
        Form<Admin> loginForm = this.form.bindFromRequest();

//        Constraints.Validatable validate = new Constraints.Validatable() {
//            @Override
//            public Object validate() {
//                error = new ValidationError("username", "12345");
//                return null;
//            }
//        };
//        System.out.println("----------------------------------2");
//        loginForm = loginForm.withError(error);
//        System.out.println(loginForm);

        if (loginForm.hasErrors()) {
            return badRequest(views.html.manage.login.render(loginForm));
        } else {
            String invalid = this.authenticate(loginForm.get().username, loginForm.get().password);

            if (invalid != null) {
                return badRequest(views.html.manage.login.render(loginForm.withGlobalError(invalid)));
            }

            return redirect(
                    routes.IndexController.index()
            );
        }
    }

    /**
     * Logout and clean the session.
     */
    public Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.LoginController.index()
        );
    }

    @Nullable
    private String authenticate(final String username, final String password) {
        Admin admin = this.loginService.login(username, password);

        if (admin == null) {
            return "无效的用户名或密码！";
        }

        session(Config.USERNAME, admin.username);
        session(Config.USER_GROUP, admin.userGroupId);

        return null;
    }

}
