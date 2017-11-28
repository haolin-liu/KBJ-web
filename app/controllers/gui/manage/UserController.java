package controllers.gui.manage;

import javax.inject.Inject;
import io.ebean.PagedList;
import models.entities.User;
import models.form.UserSearchForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;
import services.UserServices;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 *  后台用户管理的查询及更新
 *  @author lv
 *  @date 2017/11/27
 */
public class UserController extends play.mvc.Controller {
    private final FormFactory formFactory;
    private final UserRepository userRepository;
    private final HttpExecutionContext httpExecutionContext;
    private static UserServices userServices;

    @Inject
    public UserController(
                          FormFactory formFactory,
                          UserRepository userRepository,
                          HttpExecutionContext httpExecutionContext,
                          UserServices userServices) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.httpExecutionContext = httpExecutionContext;
        this.userServices = userServices;
    }

    /**
     * 跳转到用户管理界面
     */
    public Result show() {
        Form<UserSearchForm> userForm = formFactory.form(UserSearchForm.class).fill(new UserSearchForm());
        PagedList<User> lists = null;
        return ok(views.html.manage.users.render(userForm, lists));
    }

    /**
     *  查询用户
     */
    public CompletionStage<Result> find() {
        Form<UserSearchForm> userForm;
        Form<UserSearchForm> form = formFactory.form(UserSearchForm.class).bindFromRequest();
        UserSearchForm userSearchForm = form.get();

        int page;
        if (userSearchForm.isSearch) {
            page = 0;
            userSearchForm.page = 0;
            userForm = formFactory.form(UserSearchForm.class).fill(userSearchForm);
        } else {
            page = userSearchForm.page;
            userForm = form;
        }

        return userServices.getUsers(page, 2, userSearchForm).thenApplyAsync(lists -> {
            return ok(views.html.manage.users.render(userForm, lists));
        }, httpExecutionContext.current());
    }

    /**
     * 更新用户
     * @return
     */
    public CompletionStage<Result> update() {

        Http.RequestBody body = request().body();
        Map<String, String[]> data = body.asFormUrlEncoded();

        // 取得检索条件
        String name = data.get("searchName")[0];
        String email = data.get("searchEmail")[0];
        String phone = data.get("searchPhone")[0];
        String searchPage = data.get("searchPage")[0];
        int page = Integer.parseInt(searchPage);

        return userServices.updUser(data).thenApplyAsync(lists -> {
            UserSearchForm userSearchForm = new UserSearchForm(page, name, phone, email, false);
            Form<UserSearchForm> userForm = formFactory.form(UserSearchForm.class).fill(userSearchForm);
            return ok(views.html.manage.users.render(userForm ,lists));
        }, httpExecutionContext.current());
    }
}
