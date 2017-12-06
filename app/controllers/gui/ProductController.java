package controllers.gui;

import com.typesafe.config.Config;
import models.ProductsWithNum;
import models.entities.KeyWordForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import services.SearchService;
import views.html.productDetail;
import views.html.productSearch;

import javax.inject.Inject;
import java.util.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's product page.
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class ProductController extends Controller{

    private final SearchService searchService;
    private final FormFactory formFactory;
    private static Config config;

    @Inject
    public ProductController(SearchService searchService, FormFactory formFactory, Config config) {
        this.searchService = searchService;
        this.formFactory = formFactory;
        this.config = config;
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
        Form<KeyWordForm> key = formFactory.form(KeyWordForm.class).bindFromRequest();
        String keyWord = key.get().keyWord;

        if (keyWord == null) {
            keyWord = "";
        }

        ProductsWithNum products = searchService.query(keyWord, 0, config.getInt("rows"), "", "");
        Double totalPage = Math.ceil(products.getNumFound() / 12.0);
        Long countPage = new Double(totalPage).longValue();
        List<String> hotProducts = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));
        return ok(productSearch.render(products, hotProducts, keyWord, "1", countPage));
    }

    public Result generalGoods(String keyword, String start, String rows, String sorter, String filter) {

        if (keyword.equals("*")) {
            keyword = "";
        }
        if (sorter.equals("*")) {
            sorter = "";
        }
        if (filter.equals("*")) {
            filter = "";
        }

        String star =  String.valueOf(Long.valueOf(start) / Long.valueOf(rows) + 1);

        ProductsWithNum products = searchService.query(keyword, Integer.valueOf(start),
                Integer.valueOf(rows), sorter, filter);

        Double totalPage = Math.ceil(products.getNumFound()/Double.valueOf(rows));
        Long countPage = new Double(totalPage).longValue();
        List<String> hotProducts = new ArrayList<>(Arrays.asList("iphone X", "iphone 8", "小米", "华为p10", "iphone 7", "新ipad pro", "小米6"));
        return ok(productSearch.render(products, hotProducts, keyword, star, countPage));
    }
}
