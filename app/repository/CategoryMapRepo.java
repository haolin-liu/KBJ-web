package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlRow;
import models.entities.CategoryMapping;
import models.entities.KbjCategory;
import models.entities.MallCategory;
import models.entities.form.BindCategory;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This repository  created to handle category entity
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class CategoryMapRepo {

    private final EbeanServer ebeanServer;
    private final MallCategoryRepo mallCategoryRepo;
    private final KbjCategoryRepo kbjCategoryRepo;

    @Inject
    public CategoryMapRepo(EbeanConfig ebeanConfig, MallCategoryRepo mallCategoryRepo, KbjCategoryRepo kbjCategoryRepo) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.mallCategoryRepo = mallCategoryRepo;
        this.kbjCategoryRepo = kbjCategoryRepo;
    }

    public List<KbjCategory> findRootCates() {
        return ebeanServer.find(KbjCategory.class)
                .where()
                .notIn("id", 1)
                .eq("parent.id", 1)
                .eq("isCrawlTarget", 1)
                .eq("valid", 1).findList();
    }

    public List<KbjCategory> findLeafCates(String cateFatherId) {
        return ebeanServer.find(KbjCategory.class)
                .where()
                .eq("parent.id", cateFatherId)
                .eq("isCrawlTarget", 1)
                .eq("valid", 1).findList();
    }

    public List<BindCategory> find(ArrayList<String> keyWord, BindCategory bindCategory) {
        List<BindCategory> unBindCates = findUnBindCate(keyWord, bindCategory);
        List<BindCategory> bindCates = findBindCate(bindCategory);
        if (bindCategory.isBind.equals("ALL")) {
            bindCates.addAll(unBindCates);
            return bindCates;
        } else if (bindCategory.isBind.equals("0")) {
            return unBindCates;
        } else {
            return bindCates;
        }
    }

    /**
     * 查找没有绑定的商城品类
     * @param keyWord
     * @param bindCategory
     * @return
     */
    public List<BindCategory> findUnBindCate(ArrayList<String> keyWord, BindCategory bindCategory) {
        String sql = "select * " ;
        sql += " from ";
        sql += " mall_category ";
        sql +=  " where ";
        sql +=  " valid = 1 ";

        if (!bindCategory.mall.equals("ALL")) {
            sql += " and mall = '" +bindCategory.mall + "'";
        }

        if (keyWord.size() != 0) {
            sql += " and ";
            if (keyWord.size() > 1) {
                sql += " ( ";
            }
            sql += " tag like '%" + keyWord.get(0) + "%'";
            if (keyWord.size() > 1) {
                for(int i=1; i<keyWord.size(); i++) {
                    sql += " or tag like '%" + keyWord.get(i) + "%'";
                }
                sql += " ) ";
            }
        }

//        System.out.println(sql);
        List<SqlRow> sqlRows =  ebeanServer.createSqlQuery(sql).findList();
        List<SqlRow> notBindRows = new ArrayList<>();
        for (SqlRow sqlRow: sqlRows) {
            if(existMallCate((Long)sqlRow.get("id"))) {
                notBindRows.add(sqlRow);
            }
        }

        List<BindCategory> unBlindCategories = new ArrayList<>();
        for (SqlRow notBindRow: notBindRows) {
            BindCategory unBindCategoryTemp = new BindCategory();
            unBindCategoryTemp.isBind = "0";
            unBindCategoryTemp.mallCateId = (Long)notBindRow.get("id");
            unBindCategoryTemp.name = (String) notBindRow.get("name");
            unBindCategoryTemp.link = (String) notBindRow.get("link");
            unBindCategoryTemp.tag = (String) notBindRow.get("tag");
            unBindCategoryTemp.mall = (String) notBindRow.get("mall");
            unBlindCategories.add(unBindCategoryTemp);
        }
        return unBlindCategories;
    }

    /**
     * 查找绑定的品类
     * @param bindCategory
     * @return
     */
    public List<BindCategory> findBindCate(BindCategory bindCategory) {
        List<CategoryMapping> cateMaps = findList(bindCategory.leafCate);
        List<BindCategory> bindCates = new ArrayList<>();
        for (CategoryMapping cateMap: cateMaps) {
            BindCategory bindCate = new BindCategory();
            bindCate.mapId = cateMap.id;
            MallCategory mallCate = findMallCate(cateMap.mallCategory.id, bindCategory.mall);
            if (mallCate != null) {
                bindCate.isBind = "1";
                bindCate.mallCateId = mallCate.id;
                bindCate.name = mallCate.name;
                bindCate.link = mallCate.link;
                bindCate.tag = mallCate.tag;
                bindCate.mall = mallCate.mall;
                bindCates.add(bindCate);
            }
        }
        return bindCates;
    }

    /**
     * 解绑,从映设表里删除
     * @param id
     * @return
     */
    public Optional<Long> delete(Long id) {
        Optional<CategoryMapping> cateMap =  Optional.ofNullable(ebeanServer.find(CategoryMapping.class).setId(id).findUnique());
        cateMap.get().delete();
        return Optional.empty();
    }

    public Optional<Long> insert(Long kbjId, Long mallId) {
        CategoryMapping cateMap = new CategoryMapping();
        cateMap.kbjCategory = kbjCategoryRepo.find(kbjId);
        cateMap.mallCategory = mallCategoryRepo.find(mallId);
        cateMap.insert();
        return Optional.empty();
    }

    /**
     * 是否存在在映射表理
     * @param id
     * @return
     */
    private boolean existMallCate(Long id) {
        return findMap(id) == null ? true : false;
    }

    /**
     * 查找绑定的商城品类
     * @param id
     * @return
     */
    public CategoryMapping findMap(Long id) {
        return ebeanServer.find(CategoryMapping.class).where()
                .eq("mall_cate_id", id)
                .findUnique();
    }

    /**
     * 查找未绑定的商城品类
     * @param id
     * @return
     */
    public MallCategory findMallCate(Long id, String mall) {
        if (!mall.equals("ALL")) {
            return ebeanServer.find(MallCategory.class).where()
                    .eq("id", id)
                    .eq("mall", mall)
                    .eq("valid", 1)
                    .findUnique();
        } else {
            return ebeanServer.find(MallCategory.class).where()
                    .eq("id", id)
                    .eq("valid", 1)
                    .findUnique();
        }
    }

    /**
     * 查找绑定的kbj品类
     * @param id
     * @return
     */
    public List<CategoryMapping> findList(Long id) {
        return ebeanServer.find(CategoryMapping.class).where()
                .eq("kbj_cate_id", id)
                .findList();
    }


}
