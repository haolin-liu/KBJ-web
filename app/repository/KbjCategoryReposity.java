package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.PagedList;
import models.entities.KbjCategory;
import play.db.ebean.EbeanConfig;
import play.db.ebean.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * @author daiqingyi
 * @date 2017-11-27
 */
public class KbjCategoryReposity {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public KbjCategoryReposity(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
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
     * @param isCrawleTarget
     * @return
     *  @author daiqingyi
     *  @date 2017/12/1
     */

    /*所有检索条件都填写*/
    public PagedList<KbjCategory> find(boolean valid,int page, String sortBy, String order, String name, int parentId, boolean isCrawleTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parentId", parentId)
                .eq("isCrawleTarget", isCrawleTarget)
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*爬取对象 条件未填写检索*/
    public PagedList<KbjCategory> find(boolean valid, int page, String sortBy, String order, String name, int parentId) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parentId", parentId)
                .eq("valid", valid)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name, int parentId, boolean isCrawleTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parentId", parentId)
                .eq("isCrawleTarget", isCrawleTarget)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*爬取对象、有效性 条件未填写检索*/
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name, int parentId) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("parentId", parentId)
                .orderBy(sortBy + " " + order)
                .setFirstRow(page * 10)
                .setMaxRows(10)
                .findPagedList();
    }

    /*父分类Id 条件未填写检索*/
    public PagedList<KbjCategory> find(boolean valid, int page, String sortBy, String order, String name,  boolean isCrawleTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("isCrawleTarget", isCrawleTarget)
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
    public PagedList<KbjCategory> find(int page, String sortBy, String order, String name,  boolean isCrawleTarget) {
        return ebeanServer.find(KbjCategory.class).where()
                .ilike("name", "%" + name + "%")
                .eq("isCrawleTarget", isCrawleTarget)
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
     * 更新操作
     * @author daiqingyi
     * @date 2017-11-30
     * @param id
     * @param name
     * @param parentId
     * @param isCrawleTarget
     * @param user
     */
    @Transactional
    public void update(long id, String name, int parentId, boolean isCrawleTarget, boolean valid, String user) {
        KbjCategory category = ebeanServer.find(KbjCategory.class).where()
                .eq("id", id)
                .findUnique();
        if(!isNull(parentId)) {
            category.parentId = parentId;
        }
        if(!category.isCrawleTarget.equals(isCrawleTarget)) {
            category.isCrawleTarget = isCrawleTarget;
        }
        category.valid = valid;
        category.update();
    }

    /**
     * 新规操作
     * 为可比价网
     * @author daiqingyi
     * @date 2017-11-30
     * @param category
     */
    @Transactional
    public void insert(KbjCategory category) {
        category.valid = true;
        category.isCrawleTarget = true;
        category.parentId = 0;
        ebeanServer.insert(category);
    }

    /**
     * 删除操作
     * 更新可比价网本地分类数据的valid字段为false的方法
     * @author daiqingyi
     * @date 2017-11-30
     * @param id
     * @param user
     */
    @Transactional
    public void delete(long id, String user) {
        KbjCategory category = ebeanServer.find(KbjCategory.class).where()
                .eq("id", id)
                .findUnique();
        category.valid = false;
        category.update();
    }


}
