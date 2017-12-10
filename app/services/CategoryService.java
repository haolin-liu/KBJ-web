package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.CategoryExhibition;
import models.entities.KbjCategory;
import play.Logger;
import play.libs.Json;
import repository.CategoryExhibitionRepo;
import utils.ConfigUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *  @author jie-z
 *  @date 2017/12/07
 */
public class CategoryService {

    private static ConfigUtil config;
    private static CategoryExhibitionRepo cateExhibitRepo;

    @Inject
    public CategoryService(ConfigUtil config, CategoryExhibitionRepo cateExhibitRepo) {
        this.config = config;
        this.cateExhibitRepo = cateExhibitRepo;
    }

    /**
     * Get all categories which will be shown as bargains in main page in json format.
    */
    public JsonNode findBargainCates() {

        LinkedHashMap<KbjCategory, List<KbjCategory>> parents = findBargainCateList();

        ArrayNode cates = Json.newArray();
        parents.forEach( (k, v) -> {
            ObjectNode jsonParent = Json.newObject();
            jsonParent.put("id", k.id);
            jsonParent.put("name", k.name);

            ArrayNode jsonChildren = Json.newArray();
            v.forEach( leaf -> {
                ObjectNode child = Json.newObject();
                child.put("id", leaf.id);
                child.put("name", leaf.name);
                jsonChildren.add(child);
                jsonParent.set("children", jsonChildren);
            });

            cates.add(jsonParent);
        });

        return Json.toJson(cates);
    }

    /**
     * Get all categories which will be shown as bargains in main page.
    */
    public LinkedHashMap<KbjCategory, List<KbjCategory>> findBargainCateList() {
        List<CategoryExhibition> roots = cateExhibitRepo.findRootCates();
        List<CategoryExhibition> leafs = cateExhibitRepo.findLeafCates();
        int mainCatesCount = config.getCountOfMainCatesForBargain();
        int minorCatesCount = config.getCountOfMinorCatesForBargain();

        Logger.debug("main categories for bargain: " + mainCatesCount);
        Logger.debug("minor categories for bargain: " + minorCatesCount);

        LinkedHashMap<KbjCategory, List<KbjCategory>> cates = new LinkedHashMap<>();
        roots.forEach( root -> {
            if (cates.size() >= mainCatesCount) {
                return;
            }

            KbjCategory parent = root.kbjCategory;
            List<KbjCategory> children = new ArrayList<>();
            leafs.forEach( leaf -> {
                if (children.size() >= minorCatesCount) {
                    return;
                }
                if (leaf.kbjCategory.parent.id.equals(root.kbjCategory.id)) {
                    children.add(leaf.kbjCategory);
                }
            });
            cates.put(parent, children);
        });

        return cates;
    }
}
