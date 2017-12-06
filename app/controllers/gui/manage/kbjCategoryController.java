package controllers.gui.manage;

import models.entities.KbjCategory;
import models.forms.KbjCategoryForm;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import com.google.inject.Inject;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import services.KbjCategoryServices;
import java.util.concurrent.CompletionStage;

import static play.data.Form.*;


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
    private final KbjCategoryServices kbjCategoryServices;

    @Inject
    public kbjCategoryController(FormFactory formFactory,
                                 HttpExecutionContext httpExecutionContext,
                                 KbjCategoryServices kbjCategoryServices) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.kbjCategoryServices = kbjCategoryServices;
    }

    /**
     * 可比价分类的初始页面
     *
     * @author daiqingyi
     * @date 2017-11-27
     */

    public Result index() {
        Form<KbjCategoryForm> kbCateForm = formFactory.form(KbjCategoryForm.class).fill(new KbjCategoryForm());
        return ok(views.html.manage.kbjCategory.render(null, "id", "asc", kbCateForm));
    }

    /**
     * 可比价分类页面的检索结果
     *
     * @author daiqingyi
     * @date 2017-11-28
     */
    public CompletionStage<Result> list(String sortBy, String order) {
        Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).bindFromRequest();
        KbjCategoryForm kbjCates = kbjCateForm.get();
        return kbjCategoryServices.findList(kbjCates, sortBy, order).thenApplyAsync(list -> {
            return ok(views.html.manage.kbjCategory.render(list, sortBy, order, kbjCateForm));
        }, httpExecutionContext.current());
    }

    /**
     * 可比价分类的追加页面跳转
     * @author daiqingyi
     * @date 2017-12-05
     */
    public Result add() {
        Form<KbjCategoryForm> kbCateForm = formFactory.form(KbjCategoryForm.class).fill(new KbjCategoryForm());
        return ok(views.html.manage.kbjCateAdd.render(kbCateForm));
    }

    /**
     * 可比价分类的追加页面
     * @author daiqingyi
     * @date 2017-12-05
     */
    public Result save() {
        Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).bindFromRequest();
        KbjCategoryForm kbjCate = kbjCateForm.get();
        if(kbjCate.id == 0){
            kbjCategoryServices.addKbjCate(kbjCate);
        } else {
            kbjCategoryServices.updKbjCate(kbjCate);
        }
        return index();
    }

    /**
     * 可比价分类的更新页面跳转
     * @param id
     * @author daiqingyi
     * @date 2017-12-05
     */
    public CompletionStage<Result> update(Long id) {
        return kbjCategoryServices.find(id).thenApplyAsync(category -> {
            KbjCategoryForm kbjCate = new KbjCategoryForm();
            kbjCate.id = category.id;
            kbjCate.name = category.name;
            kbjCate.parentId = String.valueOf(category.parentId);
            kbjCate.bIsCrawleTarget = category.isCrawleTarget;
            kbjCate.bValid = category.valid;
            Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).fill(kbjCate);
            return ok(views.html.manage.kbjCateAdd.render(kbjCateForm));
        }, httpExecutionContext.current());
    }

}
