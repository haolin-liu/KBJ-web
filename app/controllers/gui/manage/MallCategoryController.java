package controllers.gui.manage;

import io.ebean.PagedList;
import models.entities.MallCategory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.MallCategoryService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 *
 * @author liu
 * @date 2017/11/28
 */
public class MallCategoryController extends Controller{
    private final MallCategoryService mallCategoryService;
    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public MallCategoryController(FormFactory formFactory,
                            HttpExecutionContext httpExecutionContext,
                            MallCategoryService mallCategoryService) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.mallCategoryService = mallCategoryService;
    }

    /**
     * 返回到一个商城品类的初始画面
     * @return
     */
    public Result catgory() {
        PagedList<MallCategory> list = null;
        return ok(views.html.manage.mallCategory.render(list, "", "" ,1, 0, false, 1, 0));
    }

    /**
     * 返回查询结果
     * @return
     */
    public CompletionStage<Result> search() {
        Http.RequestBody body = request().body();
        Map<String, String[]> data = body.asFormUrlEncoded();

        String name = data.get("showSrcName")[0];
        String mall = data.get("showSrcMall")[0];
        int flg = Integer.parseInt(data.get("srcIsCrawlTarget")[0]);
        int validFlg = Integer.parseInt(data.get("srcValidFlg")[0]);
        int page;
        Boolean srcflg = Boolean.valueOf(data.get("isSearch")[0]);
        if(srcflg) {
            page = 0;
        } else {
            page = Integer.parseInt(data.get("page")[0]);
        }
        return mallCategoryService.getMallCategorySearch(name, mall, flg, page, validFlg).thenApplyAsync(list -> {
            return ok(views.html.manage.mallCategory.render(list, name, mall ,flg, page, srcflg, validFlg, 0));
        }, httpExecutionContext.current());
    }

    /**
     * 新规画面
     * @return
     */
    public Result create() {
        Form<MallCategory> mallCategoryForm = formFactory.form(MallCategory.class);
        HashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("true", "是");
        options.put("false", "否");
        return ok(views.html.manage.createMallCategory.render(null, mallCategoryForm, 0, options));
    }

    /**
     * 新规保存
     * @return
     */
    public CompletionStage<Result> save() {
        Form<MallCategory> mallCategoryForm = formFactory.form(MallCategory.class).bindFromRequest();
        HashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("true", "是");
        options.put("false", "否");
        if (mallCategoryForm.hasErrors()) {
            return mallCategoryService.getMallCategoryBadSave().thenApplyAsync(companies -> {
                return badRequest(views.html.manage.createMallCategory.render(null, mallCategoryForm, 2, options));
            }, httpExecutionContext.current());
        } else {
            MallCategory mallCategory = mallCategoryForm.get();
            return mallCategoryService.getMallCategorySave(mallCategory).thenApplyAsync(data -> {
                return ok(views.html.manage.createMallCategory.render(null, mallCategoryForm, 2, options));
            }, httpExecutionContext.current());
        }
    }

    /**
     * 整体更新(两个flg：数据有效，爬虫对象)
     * @return
     */
    public CompletionStage<Result> update() {
        Http.RequestBody body = request().body();
        Map<String, String[]> data = body.asFormUrlEncoded();

        String srcName = data.get("postname")[0];
        String srcMall = data.get("postmall")[0];
        int flg = Integer.parseInt(data.get("postflg")[0]);
        int page = Integer.parseInt(data.get("postpage")[0]);
        int validFlg = Integer.parseInt(data.get("postvalid")[0]);

        return mallCategoryService.getMallCategoryUpdate(data, srcName, srcMall, flg, page, validFlg).thenApplyAsync(list -> {
            return ok(views.html.manage.mallCategory.render(list, srcName, srcMall ,1, page, false, validFlg, 1));
        }, httpExecutionContext.current());
    }

    /**
     * 取得一条数据
     * @param id
     * @return
     */
    public CompletionStage<Result> getOneEdit(Long id) {
        HashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("true", "是");
        options.put("false", "否");
        return mallCategoryService.getMallCategoryEditOne(id).thenApplyAsync(row -> {
            Form<MallCategory> mallCategoryForm = formFactory.form(MallCategory.class).fill(row);
            return ok(views.html.manage.createMallCategory.render(id, mallCategoryForm, 1, options));
        }, httpExecutionContext.current());
    }

    /**
     * 单条数据更新
     * @param id
     * @return
     */
    public CompletionStage<Result> updateOne(Long id) {
        Form<MallCategory> mallCategoryForm = formFactory.form(MallCategory.class).bindFromRequest();
        HashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("true", "是");
        options.put("false", "否");
        if (mallCategoryForm.hasErrors()) {
            return mallCategoryService.getMallCategoryBadSave().thenApplyAsync(companies -> {
                return badRequest(views.html.manage.createMallCategory.render(id, mallCategoryForm, 3, options));
            }, httpExecutionContext.current());
        } else {
            MallCategory mallCategory = mallCategoryForm.get();
            return mallCategoryService.getMallCategoryUpdateOne(id, mallCategory).thenApplyAsync(data -> {
                return ok(views.html.manage.createMallCategory.render(id, mallCategoryForm, 3, options));
            }, httpExecutionContext.current());
        }
    }
}
