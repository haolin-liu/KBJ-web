package controllers.gui;

import models.Product;
import models.ProductsWithNum;
import models.forms.KeywordForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import services.PriceService;
import services.SearchService;
import utils.ConfigUtil;
import utils.PageList;
import views.html.productDetail;
import views.html.productSearch;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's product page.
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class ProductController extends Controller{

    private final SearchService searchService;
    private final FormFactory formFactory;
    private final ConfigUtil config;
    private final PriceService priceService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public ProductController(SearchService searchService, FormFactory formFactory,
                             ConfigUtil config, PriceService priceService,
                             HttpExecutionContext httpExecutionContext) {
        this.searchService = searchService;
        this.formFactory = formFactory;
        this.config = config;
        this.priceService = priceService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public Result productDetail() {
        Map<String, String> productCommentL1 = new HashMap<>();
        productCommentL1.put("name", "qq_222014FP");
        productCommentL1.put("quote", "1楼");
        productCommentL1.put("time", "2017/11/8 10:03:24");
        productCommentL1.put("image", "http://apic-qiniu.zuyushop.com/cuxiaoPic/image/mmbAdmin/20171107082657_6155.jpg");
        productCommentL1.put("con", "我也是49.一3");

        List<Map<String, String>> productCommentL = new ArrayList<>(Arrays.asList(productCommentL1, productCommentL1));

        List<String> hotProductL = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));
        return ok(productDetail.render(productCommentL, hotProductL, ""));
    }

    public Result searchGoods() {
        PageList<Product> products = null;
        List<Integer> pageIndexList = new ArrayList<Integer>();
        Form<KeywordForm> key = formFactory.form(KeywordForm.class).bindFromRequest();
        String keyWord = key.get().kw;

        if (keyWord == null) {
            keyWord = "";
        }

        List<String> hotProducts = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));
        ProductsWithNum results = searchService.query(keyWord, 0, config.getPagesizeOfProducts(), "", "");

        try {
            products = new PageList<Product>(results.getNumFound(), 1, config.getPagesizeOfProducts(), results.getProducts());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = products.getNavigationFrom(); i <= products.getNavigationTo(); i++) {
            pageIndexList.add(i);
        }
        return ok(productSearch.render(products, hotProducts, pageIndexList, keyWord, ""));
    }

    public Result generalGoods(String keyword, Integer start, Integer rows, String sorter, String filter) {
        PageList<Product> products = null;
        List<Integer> pageIndexList = new ArrayList<Integer>();

        if (keyword.equals("*")) {
            keyword = "";
        }
        if (sorter.equals("*")) {
            sorter = "";
        }
        if (filter.equals("*")) {
            filter = "";
        }

        Integer star = (start -1) * rows;
        ProductsWithNum results = searchService.query(keyword, star, rows, sorter, filter);
        List<String> hotProducts = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));

        try{
            products = new PageList<Product>(results.getNumFound(), Integer.valueOf(start), rows, results.getProducts());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = products.getNavigationFrom(); i <= products.getNavigationTo(); i++) {
            pageIndexList.add(i);
        }
        return ok(productSearch.render(products, hotProducts, pageIndexList, keyword, sorter));
    }

    public CompletionStage<Result> getPrices(String mall, String skuid) {
        return priceService.getPrices(mall, skuid).thenApplyAsync(json-> {
            return ok(Json.toJson(json));
        }, httpExecutionContext.current());
    }
}
