package controllers.gui.manage;

import com.google.inject.Inject;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import repository.KbjCategoryReposity;

import java.util.concurrent.CompletionStage;


/**
 * This controller contains actions to handle HTTP requests
 * to the kbjCategory's home page.
 *
 * @author daiqingyi
 * @date 2017-11-27
 */

public class kbjCategoryController extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;
    private final KbjCategoryReposity kbjCategoryReposity;

    @Inject
    public kbjCategoryController(FormFactory formFactory,
                                 HttpExecutionContext httpExecutionContext,
                                 KbjCategoryReposity kbjCategoryReposity) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.kbjCategoryReposity = kbjCategoryReposity;
    }

    /**
     * An action that renders an HTML page with a list of KbjCategor's infomation.
     *
     */
    public CompletionStage<Result> index() {
        return kbjCategoryReposity.find().thenApplyAsync(list -> {
            return ok(views.html.manage.kbjCategory.render(list, "id", "asc"));
        }, httpExecutionContext.current());
    }

}
