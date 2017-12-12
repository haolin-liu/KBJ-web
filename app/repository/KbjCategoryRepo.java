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

    /**
     * Return a paged list of KbjCategory
     * @param page
     * @param sortBy
     * @param order
     * @param name
     * @param parentId
     * @param isCrawlTarget
     * @param valid
     * @return
     * @author daiqingyi
     * @date 2017/12/12
     */
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name, Long parentId, Boolean isCrawlTarget, Boolean valid) {

        if (name == null) {
            name = "";
        }
        ExpressionList<KbjCategory> express = ebeanServer.find(KbjCategory.class)
                .fetch("parent", "id, name").where()
                .ilike("name", "%" + name + "%");

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

    /**
     * 通过id检索
     * @param id
     * @author daiqingyi
     * @date 2017-12-5
     */
    public KbjCategory find(Long id) {
        return  ebeanServer.find(KbjCategory.class, id);
    }

    public KbjCategory find(Integer priority) {
        return  ebeanServer.find(KbjCategory.class).where()
                .eq("priority", priority)
                .findUnique();
    }

    /**
     * 更新
     * @param category
     * @author daiqingyi
     * @date 2017-11-30
     */
    @Transactional
    public Optional<Long> update(KbjCategory category) {
        KbjCategory cate = ebeanServer.find(KbjCategory.class).where()
                .eq("id", category.id)
                .findUnique();
        cate.parent.id = category.parent.id;
        cate.isCrawlTarget = category.isCrawlTarget;
        cate.valid = category.valid;
        cate.update();
        return Optional.empty();
    }

    /**
     * 新规
     * @param category
     * @author daiqingyi
     * @date 2017-12-5
     */
    @Transactional
    public Optional<Long> insert(KbjCategory category) {
        category.insert();
        return Optional.empty();
    }

    /**
     * 取得父分类
     * @return
     * @author daiqingyi
     * @date 2017-12-7
     */
    public Map<String, String> getParents() {
        long rootId = 1;
        List<KbjCategory> list = ebeanServer.find(KbjCategory.class)
                .fetch("parent")
                .where()
                .eq("parent.id", rootId)
                .orderBy("id")
                .findList();
        HashMap<String, String> options = new LinkedHashMap<String, String>();
        for (KbjCategory c : list) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    }

    /**
     * 更新优先度
     * @param id
     * @param priority
     * @return
     * @author daiqingyi
     * @date 2017-12-11
     */
    @Transactional
    public Optional<Long> updPriority(Long id, Integer priority) {
        KbjCategory cate = ebeanServer.find(KbjCategory.class).where()
                .eq("id", id)
                .findUnique();
        cate.priority = priority;
        cate.update();
        return Optional.empty();
    }
}
