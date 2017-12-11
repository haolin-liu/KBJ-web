package controllers.gui.manage;

import controllers.Config;
import controllers.auth.AdminSecured;
import models.entities.Admin;
import models.entities.LoginAttempt;
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
import java.sql.Timestamp;
import java.util.Date;

public class LoginController extends Controller {

    private final static String ERROR_LOGIN = "警告： 帐号或密码不匹配！";
    private final static String ERROR_ATTEMPT_ACCOUNT = "警告： 尝试登录次数超过了限制，请一小时后再次登录。";
    private final static String ERROR_ATTEMPT_IP = "警告： 尝试登录次数超过了限制，请二小时后再次登录。";

    private final static int LOGIN_ATTEMPT_ACCOUNT = 5;
    private final static int LOGIN_ATTEMPT_IP = 10;

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

//        Constraints.Validatable constraint = new Constraints.Validatable() {
//            @Override
//            public Object validate() {
//                return new ValidationError("username", "12345");
//            }
//        };
//        System.out.println("----------------------------------2");
//        loginForm = loginForm.withError((ValidationError)constraint.validate());
//        System.out.println(loginForm);

        if (loginForm.hasErrors()) {
            return badRequest(views.html.manage.login.render(loginForm));
        }

        String invalid = this.authenticate(loginForm.get().username, loginForm.get().password);

        if (invalid != null) {
            return badRequest(views.html.manage.login.render(loginForm.withGlobalError(invalid)));
        }

        return redirect(
                routes.IndexController.index()
        );
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
    private String authenticate(final String account, final String password) {

        if (this.valiAttemptIp()) {
            return ERROR_ATTEMPT_IP;
        }

        LoginAttempt attemptAccount = loginService.getLoginAttempt(account, request().remoteAddress());

        if (attemptAccount != null && this.valiAttemptAccount(attemptAccount)) {
            return ERROR_ATTEMPT_ACCOUNT;
        }

        Admin admin = loginService.login(account, password);

        if (admin == null) {
            loginService.addLoginAttempt(attemptAccount, account, request().remoteAddress());
            return ERROR_LOGIN;
        }

        if (attemptAccount != null) {
            loginService.deleteLoginAttempt(attemptAccount);
        }

        this.setSession(admin);

        return null;
    }

    /**
     * IP攻击
     * @return
     */
    private boolean valiAttemptIp() {
        int totals = 0;
        long time = 0;

        for(LoginAttempt value : loginService.getLoginAttemptsByIp(request().remoteAddress())) {
            totals += value.total;
            if (time < value.updateDate.getTime()) {
                time = value.updateDate.getTime();
            }
        }

        if (totals >= LOGIN_ATTEMPT_IP && this.compareNow(time, 2)) {
            return true;
        }

        return false;
    }

    /**
     * Account攻击
     * @return
     */
    private boolean valiAttemptAccount(LoginAttempt loginAttempt) {
        if (loginAttempt.total >= LOGIN_ATTEMPT_ACCOUNT
                && this.compareNow(loginAttempt.updateDate.getTime(), 1)) {
            return true;
        }

        return false;
    }

    private void setSession(Admin admin) {
        session(Config.USERNAME, admin.username);
        session(Config.GROUP_ID, admin.adminGroup.groupId);
    }

    /**
     *
     * @param time
     * @param hours
     * @return
     */
    private boolean compareNow(long time, int hours) {
        if ((time + 3600000 * hours) > new Date().getTime()) {
            return true;
        }
        return false;
    }

}
