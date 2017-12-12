package controllers.gui.manage;

import models.forms.KbjCategoryForm;
import play.data.Form;
import play.mvc.*;
import com.google.inject.Inject;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import services.KbjCategoryService;

import java.util.Map;
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
    private final KbjCategoryService kbjCategoryService;

    @Inject
    public kbjCategoryController(FormFactory formFactory,
                                 HttpExecutionContext httpExecutionContext,
                                 KbjCategoryService kbjCategoryService) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.kbjCategoryService = kbjCategoryService;
    }

    /**
     * 可比价分类的初始页面
     *
     * @author daiqingyi
     * @date 2017-11-27
     */

    public Result index() {
        Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).fill(new KbjCategoryForm());
        Map<String, String> options = kbjCategoryService.getParent();
        return ok(views.html.manage.kbjCategory.render(null, "priority", "asc", kbjCateForm, options));
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
        return kbjCategoryService.findList(kbjCates, sortBy, order).thenApplyAsync(list -> {
            Map<String, String> options = kbjCategoryService.getParent();
            return ok(views.html.manage.kbjCategory.render(list, sortBy, order, kbjCateForm, options));
        }, httpExecutionContext.current());
    }

    /**
     * 可比价分类的追加页面跳转
     * @author daiqingyi
     * @date 2017-12-05
     */
    public CompletionStage<Result> add() {
        Form<KbjCategoryForm> kbCateForm = formFactory.form(KbjCategoryForm.class).fill(new KbjCategoryForm());
        return kbjCategoryService.getParents().thenApplyAsync((Map<String, String> parentNames) -> {
            return ok(views.html.manage.kbjCateAdd.render(kbCateForm, parentNames));
        }, httpExecutionContext.current());
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
            kbjCategoryService.addKbjCate(kbjCate);
        } else {
            kbjCategoryService.updKbjCate(kbjCate);
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
        return kbjCategoryService.find(id).thenApplyAsync(category -> {
            KbjCategoryForm kbjCate = new KbjCategoryForm();
            kbjCate.id = category.id;
            kbjCate.name = category.name;
            kbjCate.parentId = category.parent.id;
            kbjCate.bIsCrawlTarget = category.isCrawlTarget;
            kbjCate.bValid = category.valid;
            Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).fill(kbjCate);
            Map<String,String> parentNames = kbjCategoryService.getParent();
            return ok(views.html.manage.kbjCateAdd.render(kbjCateForm, parentNames));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> updPriority(Boolean isUpOrDown, Long id, Integer priority, int page) {

        Form<KbjCategoryForm> kbjCateForm = formFactory.form(KbjCategoryForm.class).fill(new KbjCategoryForm());
        KbjCategoryForm kbjCates = kbjCateForm.get();
        kbjCates.isSearch = true;
        kbjCates.page = page;
        return kbjCategoryService.updPriority(isUpOrDown, id, priority, kbjCates, "priority", "asc").thenApplyAsync(list -> {
            kbjCateForm.fill(kbjCates);
            Map<String, String> options = kbjCategoryService.getParent();
            return ok(views.html.manage.kbjCategory.render(list, "priority", "asc", kbjCateForm, options));
        }, httpExecutionContext.current());
    }

}
