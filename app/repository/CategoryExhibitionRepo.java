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

    public List<CategoryExhibition> findFirstLevel() {
        return ebeanServer.find(CategoryExhibition.class)
                .fetch("kbjCategory")
                .fetch("kbjCategory", "name")
                .fetch("kbjCategory.parent")
                .where()
                .eq("valid", "1")
                .eq("kbjCategory.parent.id", "1")
                .findList();
    }

}
