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

    public MallCategory find(Long id) {
        return ebeanServer.find(MallCategory.class).where().eq("id", id).findUnique();
    }

    public PagedList<MallCategory> find(String id, String name, String mall, Boolean isBound,
                                        Boolean isCrawlTarget, Boolean isValid, int page, int pageSize) {
        ExpressionList<MallCategory> express = ebeanServer.find(MallCategory.class)
                .fetch("categoryMapping", "mall_category_id")
                .fetch("categoryMapping.kbjCategory", "name, is_crawl_target")
                .where()
                .ilike("name", "%" + name + "%")
                .ilike("mall", "%" + mall + "%");
        System.out.println("是否爬虫对象: " + isCrawlTarget);

        if (id.length() > 0) {
            express = express.eq("id", id);
        }
        if (isBound != null) {
            if (isBound) {
                express = express.isNotNull("categoryMapping");
            } else {
                express = express.isNull("categoryMapping");
            }
        }
        if (isCrawlTarget != null) {
            express = express.eq("isCrawlTarget", isCrawlTarget);
        }
        if (isValid != null) {
            express = express.eq("valid", isValid);
        }

        return express.setFirstRow(page * pageSize).setMaxRows(pageSize).findPagedList();
    }

    public void update(List<MallCategory> categories) {

    }

    public Optional<Long> update(MallCategory category) {
        return Optional.empty();
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

    public Long insert(MallCategory mallCategory) {
        mallCategory.insert();
        return mallCategory.id;
    }

    public Optional<Long> empty() {
        return  Optional.empty();
    }

}
