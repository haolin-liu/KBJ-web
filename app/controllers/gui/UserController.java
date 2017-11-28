package controllers.gui;

import models.entities.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class UserController extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public UserController(FormFactory formFactory, HttpExecutionContext httpExecutionContext) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
    }

    public Result login() {
        Form<User> userForm = formFactory.form(User.class).fill(new User());
        return ok(views.html.login.render(userForm));
    }
}
