package controllers.gui;

import models.entities.User;
import models.form.LoginForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserServices;
import javax.inject.Inject;

public class UserController extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;
    private static UserServices userServices;

    @Inject
    public UserController(FormFactory formFactory, HttpExecutionContext httpExecutionContext, UserServices userServices) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.userServices = userServices;
    }

    /**
     * 登录
     * @return
     */
    public Result login() {
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(views.html.login.render(loginForm));
        } else {

            Constraints.Validatable constraint = new Constraints.Validatable() {
                @Override
                public Object validate() {
                    return new ValidationError("name", "您输入的帐号或密码有误");
                }
            };

            // 取得登录名和密码
            LoginForm login = loginForm.get();
            String name = login.name;
            String password = login.password;

            String message;

            // email验证
            String em =  "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            // phone验证
            String ph =  "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

            // 判断登录方式
            if (name.matches(em)) {
                message = authentication(name, password, 1);
            } else if (name.matches(ph)) {
                message = authentication(name, password, 2);
            } else {
                message = authentication(name, password, 3);
            }

            if (("").equals(message)) {
                return redirect(controllers.gui.routes.HomeController.index());
            } else {
                return  badRequest(views.html.login.render(loginForm.withError((ValidationError)constraint.validate())));
            }
        }
    }

    /**
     * 登录验证
     * @param name
     * @param password
     * @param loginMode
     * @return
     */
    public String authentication(String name, String password, int loginMode) {
        User user = userServices.getUsers(name, password, loginMode);

        if (user == null) {
            return "用户或密码无效";
        }

        // 存入缓存
        session().put("name", name);
        session().put("password", password);

        return "";
    }
}
