package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.entities.CategoryExhibition;
import play.libs.Json;
import repository.CategoryExhibitionRepo;

import javax.inject.Inject;
import java.util.List;

/**
 *  @author jie-z
 *  @date 2017/12/07
 */
public class CategoryService {

    private static Config config;
    private static CategoryExhibitionRepo cateExhibitRepo;
    private static int bargain_cate_count_main;
    private static int bargain_cate_count_minor;

    @Inject
    public CategoryService(Config config, CategoryExhibitionRepo cateExhibitRepo) {
        this.config = config;
        this.cateExhibitRepo = cateExhibitRepo;

        this.bargain_cate_count_main = config.getInt("webapp.mainpage.bargain.cates.count.main");
        this.bargain_cate_count_minor = config.getInt("webapp.mainpage.bargain.cates.count.minor");
    }

    /**
     * Get all categories which will be shown as bargains in main page.
    */
    public JsonNode findBargainCates() {
        List<CategoryExhibition> roots = cateExhibitRepo.findRootCates();
        List<CategoryExhibition> leafs = cateExhibitRepo.findLeafCates();
        ArrayNode cates = Json.newArray();
        roots.forEach( root -> {
            if (cates.size() >= this.bargain_cate_count_main) {
                return;
            }

            ObjectNode parent = Json.newObject();
            parent.put("id", root.kbjCategory.id);
            parent.put("name", root.kbjCategory.name);
            ArrayNode children = Json.newArray();
            leafs.forEach( leaf -> {
                if (children.size() >= this.bargain_cate_count_minor) {
                    return;
                }
                if (leaf.kbjCategory.parent.id.equals(root.kbjCategory.id)) {
                    ObjectNode child = Json.newObject();
                    child.put("id", leaf.kbjCategory.id);
                    child.put("name", leaf.kbjCategory.name);
                    children.add(child);
                    parent.set("children", children);
                }
            });

            cates.add(parent);
        });

        return Json.toJson(cates);
    }
}
