package controllers.auth;

import controllers.Config;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/*
 * 超级管理员权限
 * 只有超级管理员能访问
 *
 * @author lichen
 * @date 2017.11.28
 */
public class SuperSecured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        if (Config.SUPER.equals(ctx.session().get(Config.USER_GROUP))) {
            return ctx.session().get(Config.USERNAME);
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(
                controllers.gui.manage.routes.LoginController.index()
        );
    }

}
