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
                .fetch("kbj_category")
                .fetch("kbj_category.id", "1")
                .where()
                .eq("valid", "1")
                .findList();
    }

}
