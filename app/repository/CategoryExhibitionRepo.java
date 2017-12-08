package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.entities.CategoryExhibition;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;

public class CategoryExhibitionRepo {

    private final EbeanServer ebeanServer;

    @Inject
    public CategoryExhibitionRepo(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public List<CategoryExhibition> findRootCates() {
        return ebeanServer.find(CategoryExhibition.class)
                .select("id, priority")
                .fetch("kbjCategory")
                .fetch("kbjCategory", "name")
                .fetch("kbjCategory.parent", "name")
                .where()
                .eq("valid", 1)
                .eq("kbjCategory.parent.id", 1)
                .eq("kbjCategory.valid", 1)
                .eq("kbjCategory.isCrawlTarget", 1)
                .ne("kbjCategory.parent.id", "kbjCategory.id")
                // same to the statement below
                //.ne("kbjCategory.id", 1)
                .orderBy()
                .asc("priority")
                .findList();
    }

    public List<CategoryExhibition> findLeafCates() {
        return findLeafCates(null);
    }

    public List<CategoryExhibition> findLeafCates(Long parentCateId) {
        return ebeanServer.find(CategoryExhibition.class)
                .select("id, priority")
                .fetch("kbjCategory")
                .fetch("kbjCategory", "name")
                .fetch("kbjCategory.parent")
                .where()
                .eq("valid", 1)
                .ne("kbjCategory.parent.id", 1)
                .like("kbjCategory.parent.id", parentCateId == null ? "%" : parentCateId.toString())
                .eq("kbjCategory.valid", 1)
                .eq("kbjCategory.isCrawlTarget", 1)
                .orderBy("kbjCategory.parent.id asc, priority asc")
                .findList();
    }

}
