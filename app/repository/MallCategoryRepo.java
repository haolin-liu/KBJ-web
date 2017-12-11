package repository;

import io.ebean.*;
import models.entities.CategoryMapping;
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

    @Inject
    public MallCategoryRepo(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    /**
     * 有效Id检索
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

    public List<MallCategory> find() {
        return ebeanServer.find(MallCategory.class)
            .fetch("categoryMapping", "mall_category_id")
            .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
            .findList();
    }

    /**
     * 画面检索
     * @param id
     * @param name
     * @param mall
     * @param page
     * @param pageSize
     * @param flg
     * @param validFlg
     * @return
     */
    public PagedList<MallCategory> find(String id, String name, String mall, int page, int pageSize, String flg, String validFlg) {
        ExpressionList<MallCategory> categoryExpressionList = ebeanServer.find(MallCategory.class)
            .fetch("categoryMapping", "mall_category_id")
            .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
            .where();
        if (id.length() != 0) {
            return categoryExpressionList.eq("id", id)
                .ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%")
                .ilike("isCrawlTarget", "%" + flg + "%")
                .ilike("valid", "%" + validFlg + "%")
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
        } else {
            return categoryExpressionList.ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%")
                .ilike("isCrawlTarget", "%" + flg + "%")
                .ilike("valid", "%" + validFlg + "%")
                .setFirstRow(page * pageSize)
                .setMaxRows(pageSize)
                .findPagedList();
        }
    }

    /**
     * 检索绑定数据
     * @param id
     * @param name
     * @param mall
     * @param page
     * @param pageSize
     * @param flg
     * @param validFlg
     * @return
     */
    public PagedList<MallCategory> findBind(String id, String name, String mall, int page, int pageSize, String flg, String validFlg) {
        ExpressionList<MallCategory> categoryExpressionList = ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .where();
        if (id.length() != 0) {
            return categoryExpressionList
                    .isNotNull("categoryMapping")
                    .eq("id", id)
                    .ilike("name", "%" + name + "%")
                    .ilike("mall", "%" + mall + "%")
                    .ilike("isCrawlTarget", "%" + flg + "%")
                    .ilike("valid", "%" + validFlg + "%")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        } else {
            return categoryExpressionList
                    .isNotNull("categoryMapping")
                    .ilike("name", "%" + name + "%")
                    .ilike("mall", "%" + mall + "%")
                    .ilike("isCrawlTarget", "%" + flg + "%")
                    .ilike("valid", "%" + validFlg + "%")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        }
    }

    /**
     * 检索未绑定数据
     * @param id
     * @param name
     * @param mall
     * @param page
     * @param pageSize
     * @param flg
     * @param validFlg
     * @return
     */
    public PagedList<MallCategory> findNoBind(String id, String name, String mall, int page, int pageSize, String flg, String validFlg) {
        ExpressionList<MallCategory> categoryExpressionList = ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .where();
        if (id.length() != 0) {
            return categoryExpressionList
                    .isNull("categoryMapping")
                    .eq("id", id)
                    .ilike("name", "%" + name + "%")
                    .ilike("mall", "%" + mall + "%")
                    .ilike("isCrawlTarget", "%" + flg + "%")
                    .ilike("valid", "%" + validFlg + "%")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        } else {
            return categoryExpressionList
                    .isNull("categoryMapping")
                    .ilike("name", "%" + name + "%")
                    .ilike("mall", "%" + mall + "%")
                    .ilike("isCrawlTarget", "%" + flg + "%")
                    .ilike("valid", "%" + validFlg + "%")
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList();
        }
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
                Boolean valid = Boolean.parseBoolean(data.get("hidvalid")[i]);
                Boolean isCrawlTarget = Boolean.parseBoolean(data.get("hidisCrawlTarget")[i]);
                Boolean bindFlg = Boolean.parseBoolean(data.get("hidbind")[i]);
                int chgFlgValid = Integer.parseInt(data.get("chgFlgValid")[i]);
                int chgFlgIsCrawlTarget = Integer.parseInt(data.get("chgFlgIsCrawlTarget")[i]);
                int chgFlgBind = Integer.parseInt(data.get("chgFlgBind")[i]);

                Transaction txn = ebeanServer.beginTransaction();
                if (chgFlgValid == 1 || chgFlgIsCrawlTarget == 1 || chgFlgBind == 1) {
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
                        if (!bindFlg) {
                           Optional<CategoryMapping> delBind = Optional.ofNullable(ebeanServer.find(CategoryMapping.class)
                                .where()
                                .eq("mall_category_id", id)
                                .findUnique());
                           if(delBind.isPresent()) {
                               delBind.get().delete();
                           }
                        }
                    } finally {
                        txn.end();
                    }
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
