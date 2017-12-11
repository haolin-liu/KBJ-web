package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.PagedList;
import io.ebean.Transaction;
import models.entities.MallCategory;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author liu
 * @date 2017/11/28
 */
public class MallCategoryRepo {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public MallCategoryRepo(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    /**
     * 检索存在FLG条件
     * @param id
     * @return
     */
    public MallCategory find(Long id) {
        return ebeanServer.find(MallCategory.class)
                .where()
                .eq("id", id)
                .eq("valid", 1)
                .findUnique();
    }

    /**
     * 检索存在FLG条件
     * @param name
     * @param mall
     * @param flg
     * @param page
     * @param pageSize
     * @return
     */
    public PagedList<MallCategory> find(String name, String mall, String flg, Integer page, int pageSize, String validFlg) {
        return ebeanServer.find(MallCategory.class)
            .fetch("categoryMapping", "mall_category_id")
            .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
            .where()
            .ilike("name", "%" + name + "%")
            .ilike("mall", "%" + mall + "%")
            .ilike("isCrawlTarget", "%" + flg + "%")
            .ilike("valid", "%" + validFlg + "%")
            .setFirstRow(page * pageSize)
            .setMaxRows(pageSize)
            .findPagedList();
    }

    public PagedList<MallCategory> find(String id, String name, String mall, String flg, Integer page, int pageSize, String validFlg) {
        return ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name kbjname, is_crawl_target")
                .where()
                .eq("id", id)
                .ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%")
                .ilike("isCrawlTarget", "%" + flg + "%")
                .ilike("valid", "%" + validFlg + "%")
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
    }

    public List<MallCategory> find() {
        return ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .findList();
    }

    /**
     * 检索不存在FLG条件
     * @param name
     * @param mall
     * @param page
     * @param pageSize
     * @return
     */
    public PagedList<MallCategory> findId(String id, String name, String mall, int page, int pageSize) {
        return ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .where()
                .eq("id", id)
                .ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%")
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
    }

    public PagedList<MallCategory> findAll(String name, String mall, int page, int pageSize) {
        return ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .where()
                .ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%")
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
    }

    /**
     * 多条更新
     * @param data
     * @return
     */
    public Optional<Long> update(Map<String, String[]> data) {

        Optional<Long> value = Optional.empty();
            int strips = data.get("hidid").length;
            for(int i = 0; i < strips; i++) {
                Long id = Long.valueOf(data.get("hidid")[i]);
                Boolean valid = Boolean.valueOf(data.get("hidvalid")[i]);
                Boolean isCrawlTarget = Boolean.valueOf(data.get("hidisCrawlTarget")[i]);

                Transaction txn = ebeanServer.beginTransaction();
                try {
                    MallCategory savedMallCategory = ebeanServer.find(MallCategory.class).setId(id).findUnique();
                    if (savedMallCategory != null) {
                        savedMallCategory.valid = valid;
                        savedMallCategory.isCrawlTarget = isCrawlTarget;
                        savedMallCategory.updateDate = new Date();
                        savedMallCategory.update();
                        txn.commit();
                        value = Optional.of(id);
                    }
                } finally {
                    txn.end();
                }
            }
            return value;
    }

    /**
     * 单条更新
     * @param id
     * @param mallCategory
     * @return
     */
    public Optional<Long> updateOne(Long id, MallCategory mallCategory) {
        Optional<Long> value = Optional.empty();
            Transaction txn = ebeanServer.beginTransaction();
            try {
                MallCategory savedMallCategory = ebeanServer.find(MallCategory.class).setId(id).findUnique();
                if (savedMallCategory != null) {
                    savedMallCategory.name = mallCategory.name;
                    savedMallCategory.link = mallCategory.link;
                    savedMallCategory.mall = mallCategory.mall;
                    savedMallCategory.tag = mallCategory.tag;
                    savedMallCategory.valid = mallCategory.valid;
                    savedMallCategory.isCrawlTarget = mallCategory.isCrawlTarget;
                    savedMallCategory.updateDate = new Date();
                    savedMallCategory.update();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
        return value;
    }

    /**
     *
     * @param mallCategory
     * @return
     */
    public Long insert(MallCategory mallCategory) {
        mallCategory.insert();
        return mallCategory.id;
    }

    /**
     *
     * @return
     */
    public Optional<Long> empty() {
        return  Optional.empty();
    }

    /**
     * 更新用单条检索
     * @param id
     * @return
     */
    public MallCategory searchOne(Long id) {
        MallCategory mallCategory = ebeanServer.find(MallCategory.class).setId(id).findUnique();
        return  mallCategory;
    }
}
