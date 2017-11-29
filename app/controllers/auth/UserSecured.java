package controllers.auth;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/*
 * 用户权限
 * 判断用户是否登录、判断用户session是否有效
 *
 * @author lichen
 * @date 2017.11.28
 */
public class UserSecured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(Config.USERNAME);
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        //TODO
        return redirect("http://localhost:9000/login");
    }

}
