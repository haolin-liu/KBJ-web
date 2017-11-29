package controllers.auth;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/*
 * 管理员权限
 * 超级管理员和管理员可以访问
 *
 * @author lichen
 * @date 2017.11.28
 */
public class AdminSecured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        String group = ctx.session().get(Config.USER_GROUP);

        if (Config.SUPER.equals(group) || Config.ADMIN.equals(group)) {
            return ctx.session().get(Config.USERNAME);
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        //TODO
        return redirect("http://localhost:9000/login");
    }

}
