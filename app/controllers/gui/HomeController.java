package controllers.gui;

import models.entities.KbjCategory;
import models.entities.KeySearch;
import models.entities.User;
import models.form.LoginForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import repository.KeySearchRepository;
import services.CategoryMapService;
import services.CategoryService;
import services.HomeService;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class HomeController extends Controller {

    private final KeySearchRepository keySearchRepository;
    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;
    private static CategoryMapService categoryMapService;
    private static CategoryService categoryService;
    private static HomeService homeService;

    @Inject
    public HomeController(KeySearchRepository keySearchRepository,
                          FormFactory formFactory,
                          HttpExecutionContext httpExecutionContext,
                          CategoryMapService categoryMapService,
                          CategoryService categoryService,
                          HomeService homeService) {
        this.keySearchRepository = keySearchRepository;
        this.formFactory = formFactory;
        this.httpExecutionContext = httpExecutionContext;
        this.categoryMapService = categoryMapService;
        this.categoryService = categoryService;
        this.homeService = homeService;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index() {
        List<String> hotProductL = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));

        LinkedHashMap<KbjCategory, List<KbjCategory>> bargainCates = categoryService.findBargainCateList();

        //打印test
//        Set<KbjCategory> set = bargainCates.keySet();
//        bargainCates.forEach((k, v) -> {
//            System.out.println("--------------------Name" + k.name);
//            System.out.println("--------------------Id" + k.id);
//            List<KbjCategory> values = bargainCates.get(k);
//            for (KbjCategory value: values) {
//                System.out.println(value.id);
//                System.out.println(value.name);
//            }
//        });

        return categoryMapService.getKbjRootCates().thenApplyAsync(rootCates -> {
            return ok(views.html.index.render(rootCates, bargainCates, hotProductL, ""));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getLeafCates(String rootCate) {
        return categoryMapService.getKbjLeafCates(rootCate).thenApplyAsync(catesJson -> {
            return ok(Json.toJson(catesJson));
        }, httpExecutionContext.current());
    }


    public CompletionStage<Result> getBargains(String cate) {
        return homeService.getBargains(cate).thenApplyAsync(bargains -> {
            System.out.println(bargains);
            System.out.println("---------------------------------");
            return ok(Json.toJson(bargains));
        }, httpExecutionContext.current());
    }


    public CompletionStage<Result> editPage(long id) {
        Form<KeySearch> KeySearchAdd = formFactory.form(KeySearch.class);
        return keySearchRepository.getRow(id).thenApplyAsync(row -> {
            KeySearch c = row.get();
            Form<KeySearch> keySearchForm = formFactory.form(KeySearch.class).fill(c);
            return ok(views.html.edit.render(KeySearchAdd, keySearchForm, id));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> list() {
        return keySearchRepository.list().thenApplyAsync(list -> {
            return ok(views.html.keySearch.render(list));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> edit(long id) {
        Form<KeySearch> KeySearchAdd = formFactory.form(KeySearch.class);
        Form<KeySearch> keySearchForm = formFactory.form(KeySearch.class).bindFromRequest();
        if (keySearchForm.hasErrors()) {
            return keySearchRepository.getRow(id).thenApplyAsync(row -> {
                return badRequest(views.html.edit.render(KeySearchAdd, keySearchForm, id));
            }, httpExecutionContext.current());
        } else {
            KeySearch keySearchModel = keySearchForm.get();
            return keySearchRepository.edit(keySearchModel, id).thenApplyAsync(v -> {
                return redirect("/");
            }, httpExecutionContext.current());
        }
    }

    public CompletionStage<Result> delete(long id) {
        return keySearchRepository.delete(id).thenApplyAsync(v -> {
            return redirect("/");
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> add() {
        long a = 1;
        Form<KeySearch> keySearchForm = formFactory.form(KeySearch.class);
        Form<KeySearch> KeySearchAdd = formFactory.form(KeySearch.class).bindFromRequest();
        if (KeySearchAdd.hasErrors()) {
            return keySearchRepository.doNothing().thenApplyAsync(v -> {
                return badRequest(views.html.edit.render(KeySearchAdd, keySearchForm, a));
            }, httpExecutionContext.current());
        }

        KeySearch keySearchModel = KeySearchAdd.get();
        return keySearchRepository.add(keySearchModel).thenApplyAsync(v -> {
            return redirect("/");
        }, httpExecutionContext.current());
    }

}
