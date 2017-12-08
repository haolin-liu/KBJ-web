package controllers.api;

import models.Product;
import models.ProductsWithNum;
import models.entities.CategoryExhibition;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import repository.CategoryExhibitionRepo;
import services.SearchService;
import utils.PageList;

import javax.inject.Inject;
import java.util.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private static SearchService searcher;
    private static CategoryExhibitionRepo cateExhibitRepo;
    @Inject
    public HomeController(SearchService searcher,
                          CategoryExhibitionRepo cateExhibitRepo) {
        this.searcher = searcher;
        this.cateExhibitRepo = cateExhibitRepo;
    }

    /**
     * TODO
     * to be remove
     * just for test
     *
     * @return
     */
    public Result index() {

        String keyword = "小米（MI）小 米　家";

        List<F.Tuple<String, String>> sorters = new ArrayList<>();
        sorters.add(new F.Tuple("price", 1));

        List<F.Tuple4<String, String, String, String>> filters = new ArrayList<>();
        filters.add(new F.Tuple4("", "price", "10", ""));

        ProductsWithNum result = searcher.query(keyword, 0, 10, sorters, filters);

        Logger.debug("-----------solr: " + result.getNumFound());
        for (Product product : result.getProducts()) {
            Logger.debug(product.getSkuid() + " : " + product.getName() + " : " + product.getPrice());
        }

        return ok(Json.toJson(result));
    }

    public Result generalSearch(String keyword, int start, int rows) {

        // todo
        // for test
        keyword = "小米（MI）小 米　家";
        start = 0;
        rows = 10;

        List<F.Tuple<String, String>> sorters = new ArrayList<>();
        sorters.add(new F.Tuple("price", 1));

        List<F.Tuple4<String, String, String, String>> filters = new ArrayList<>();
        filters.add(new F.Tuple4("", "price", "10", ""));

        ProductsWithNum result = searcher.query(keyword, start, rows, sorters, filters);

        Logger.debug("-----------solr: " + result.getNumFound());
        for (Product product : result.getProducts()) {
            Logger.debug(product.getSkuid() + " : " + product.getName() + " : " + product.getPrice());
        }

        return ok(Json.toJson(result));
    }

    /**
     * test method
     * @auther jie-z
     * @date 2017/11/20
     *
     * @param keyword
     * @param start
     * @param rows
     * @param sorter
     * @param filter
     * @return
     */
    public Result generalSearch(String keyword, String start, String rows, String sorter, String filter) {

        ProductsWithNum result = searcher.query(keyword, Integer.valueOf(start),
                Integer.valueOf(rows), sorter, filter);
        Logger.debug("-----------solr: " + result.getNumFound());
        for (Product product : result.getProducts()) {
            Logger.debug(product.getSkuid() + " : " + product.getName() + " : " + product.getPrice());
        }

        try {
            int page = (int)Math.ceil((Integer.valueOf(start) + 1) / (Double.valueOf(rows)));
            Logger.debug("page: " + page);
            PageList<Product> data = new PageList<Product>(result.getNumFound(), page, Integer.valueOf(rows), result.getProducts());
            Logger.debug("total record: " + data.getRecordCount());
            Logger.debug("page count: " + data.getPageCount());
            Logger.debug("current page index: " + data.getPageIndex());
            Logger.debug("navigate from: " + data.getNavigationFrom());
            Logger.debug("navigate to: " + data.getNavigationTo());
            Logger.debug("has previous page: " + data.hasPrevPage());
            Logger.debug("has next page: " + data.hasNextPage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<CategoryExhibition> cateExhibits = cateExhibitRepo.findFirstLevel();
        Logger.debug("----------size of first class cate to be exhibited: " + cateExhibits.size());
        for (CategoryExhibition cate : cateExhibits) {
            Logger.debug("id: " + cate.kbjCategory.id + ", priority: " + cate.priority + ", parent.id: " + cate.kbjCategory.parent.id);
        }

        List<CategoryExhibition> secondCateExhibits = cateExhibitRepo.findSecondLevel();
        Logger.debug("----------size of second class cate to be exhibited: " + secondCateExhibits.size());
        for (CategoryExhibition cate : secondCateExhibits) {
            Logger.debug("id: " + cate.kbjCategory.id + ", priority: " + cate.priority + ", parent.id: " + cate.kbjCategory.parent.id);
        }

        return ok(Json.toJson(result));
    }

}
