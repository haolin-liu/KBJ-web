package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
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
     *
     * @param valid
     * @param page              Page to display
     * @param sortBy            KbjCategory property used for sorting
     * @param order             Sort order (either or asc or desc)
     * @param name
     * @param parentId
     * @param isCrawlTarget
     * @return
     *  @author daiqingyi
     *  @date 2017/12/1
     */

    /*所有检索条件都填写*/
    public PagedList<KbjCategory> find(boolean valid,int page, String sortBy, String order, String name, long parentId, boolean isCrawlTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parent_id", parentId)
                .eq("isCrawlTarget", isCrawlTarget)
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*爬取对象 条件未填写检索*/
    public PagedList<KbjCategory> find(boolean valid, int page, String sortBy, String order, String name, long parentId) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parent_id", parentId)
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name, long parentId, boolean isCrawlTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parent_id", parentId)
                .eq("isCrawlTarget", isCrawlTarget)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*爬取对象、有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name, long parentId) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parent_id", parentId)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*父分类Id 条件未填写检索*/
    public PagedList<KbjCategory> find(boolean valid, int page, String sortBy, String order, String name,  boolean isCrawlTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("isCrawlTarget", isCrawlTarget)
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*父分类Id、爬取对象 条件未填写检索*/
    public PagedList<KbjCategory> find(boolean valid, int page, String sortBy, String order, String name) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*父分类Id、有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name,  boolean isCrawlTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("isCrawlTarget", isCrawlTarget)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*父分类Id、爬取对象、有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
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
}
