package controllers.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.CategoryExhibition;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repository.CategoryExhibitionRepo;
import services.CategoryService;
import services.SearchService;

import javax.inject.Inject;
import java.util.List;

/**
 * This controller contains actions to handle HTTP requests
 * to the category relevant APIs.
 *  @author jie-z
 *  @date 2017/12/07
 */
public class CateController extends Controller {

    private static SearchService searcher;
    private static CategoryService categoryService;
    @Inject
    public CateController(SearchService searcher,
                          CategoryService categoryService) {
        this.searcher = searcher;
        this.categoryService = categoryService;
    }

    /**
     * Get all categories which will be shown as bargains in main page.
    */
    public Result findBargainCates() {
        return ok(Json.toJson(categoryService.findBargainCates()));
    }

}
