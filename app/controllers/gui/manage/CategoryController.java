package controllers.gui.manage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.KbjCategory;
import models.entities.MallCategory;
import models.entities.form.BindCategory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.CategoryMapService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle connection on kbjCategory and mallCategory
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class CategoryController extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;
    private static CategoryMapService categoryMapService;

    @Inject
    public CategoryController(FormFactory formFactory,
                              HttpExecutionContext httpExecutionContext,
                              CategoryMapService categoryMapService) {
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.categoryMapService = categoryMapService;
    }

    public CompletionStage<Result> getRootCates() {
        Form<BindCategory> cateMap = formFactory.form(BindCategory.class).fill(new BindCategory());
        Form<BindCategory> cateMapBind = formFactory.form(BindCategory.class).fill(new BindCategory());
        List<BindCategory> mallCates = new ArrayList<>();
        return categoryMapService.getKbjRootCates().thenApplyAsync(kbjFatherCates -> {
//            System.out.println(cateMap.get().rootCate == null);
            return ok(views.html.manage.categoryMap.render(kbjFatherCates, cateMap, cateMapBind, mallCates));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getLeafCates(String cateRootId) {
        return categoryMapService.getKbjLeafCates(cateRootId).thenApplyAsync(catesJson -> {
           return ok( Json.toJson(catesJson));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getMallCates() {
        Form<BindCategory> categoryForm = formFactory.form(BindCategory.class).bindFromRequest();
        Form<BindCategory> cateMapBind = formFactory.form(BindCategory.class).fill(categoryForm.get());
        CompletionStage<List<KbjCategory>> kbjFatherCates = categoryMapService.getKbjRootCates();

        return categoryMapService.getMallCategory(categoryForm).thenCombineAsync(kbjFatherCates, (mallCates, kbjFather) -> {
            return ok(views.html.manage.categoryMap.render(kbjFather, categoryForm, cateMapBind, mallCates));
        }, httpExecutionContext.current());

    }

    public CompletionStage<Result> edit() {
        Http.RequestBody body = request().body();
        Map<String, String[]> data = body.asFormUrlEncoded();

        Long rootCate = Long.valueOf(data.get("rootCate")[0]);
        Long leafCate = Long.valueOf(data.get("leafCate")[0]);
        String mall = data.get("mall")[0];
        String keyWord = data.get("keyWord")[0];
        String isBind = data.get("isBind")[0];

        //用于检索条件显示
        Form<BindCategory> cateMap = formFactory.form(BindCategory.class).fill(new BindCategory());
        cateMap.get().rootCate = rootCate;
        cateMap.get().leafCate = leafCate;
        cateMap.get().mall = mall;
        cateMap.get().keyWord = keyWord;
        cateMap.get().isBind = isBind;

        //绑定更新表单
        Form<BindCategory> bindCate = formFactory.form(BindCategory.class).fill(cateMap.get());

        //下拉父选框
        CompletionStage<List<KbjCategory>> kbjFatherCates = categoryMapService.getKbjRootCates();

        return categoryMapService.edit(data, cateMap).thenCombineAsync(kbjFatherCates, (mallCates, kbjFather) -> {
            return ok(views.html.manage.categoryMap.render(kbjFather, cateMap, bindCate, mallCates));
        }, httpExecutionContext.current());
    }
}
