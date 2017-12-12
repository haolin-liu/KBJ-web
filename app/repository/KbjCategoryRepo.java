package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.entities.KbjCategory;
import play.db.ebean.EbeanConfig;
import play.db.ebean.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * @author daiqingyi
 * @date 2017-11-27
 */
public class KbjCategoryRepo {
    private final EbeanServer ebeanServer;

    @Inject
    public KbjCategoryRepo(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public KbjCategory find(Long id) {
        return  ebeanServer.find(KbjCategory.class, id);
    }

    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name,
                                       Long parentId, Boolean isCrawlTarget, Boolean valid) {
        ExpressionList<KbjCategory> express = ebeanServer.find(KbjCategory.class)
                .fetch("parent", "id, name")
                .where();

        if (name != null) {
            express = express.ilike("name", "%" + name + "%");
        }
        if (parentId != null) {
            express = express.eq("parent.id", parentId);
        }
        if (isCrawlTarget != null) {
            express = express.eq("isCrawlTarget", isCrawlTarget);
        }
        if (valid != null) {
            express = express.eq("valid", valid);
        }

        return express.orderBy(sortBy + " " + order).setFirstRow(page * 10).setMaxRows(10).findPagedList();
    }

    public KbjCategory find(Integer priority) {
        return ebeanServer.find(KbjCategory.class)
                .where()
                .eq("priority", priority)
                .eq("valid", 1)
                .findUnique();
    }

    @Transactional
    public void update(KbjCategory category) {
        KbjCategory cate = ebeanServer.find(KbjCategory.class).where()
                .eq("id", category.id)
                .findUnique();
        cate.parent.id = category.parent.id;
        cate.isCrawlTarget = category.isCrawlTarget;
        cate.valid = category.valid;
        cate.update();
    }

    @Transactional
    public void insert(KbjCategory category) {
        category.insert();
    }

    public List<KbjCategory> getParents() {
        long rootId = 1;
        return ebeanServer.find(KbjCategory.class)
                .fetch("parent")
                .where()
                .eq("parent.id", rootId)
                .ne("id", rootId)
                .eq("valid", 1)
                .orderBy("id")
                .findList();
    }

    @Transactional
    public Optional<Long> updPriority(Long id, Integer priority) {
        KbjCategory cate = ebeanServer.find(KbjCategory.class).where()
                .eq("id", id)
                .eq("valid", 1)
                .findUnique();
        cate.priority = priority;
        cate.update();
        return Optional.empty();
    }
}
