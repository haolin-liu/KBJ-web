package services;

import io.ebean.PagedList;
import models.entities.KbjCategory;
import models.forms.KbjCategoryForm;
import org.jetbrains.annotations.Contract;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.KbjCategoryReposity;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This service created to get datas from entity
 *  @author daiqingyi
 *  @date 2017/12/1
 */

public class KbjCategoryServices {

    private final DatabaseExecutionContext executionContext;
    private final KbjCategoryReposity kbjCategoryReposity;
    private final KbjCategoryForm kbjCategoryForm;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public KbjCategoryServices(DatabaseExecutionContext executionContext,
                               KbjCategoryReposity kbjCategoryReposity,
                               KbjCategoryForm kbjCategoryForm,
                               HttpExecutionContext httpExecutionContext) {
        this.executionContext = executionContext;
        this.kbjCategoryReposity = kbjCategoryReposity;
        this.kbjCategoryForm = kbjCategoryForm;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<PagedList<KbjCategory>> findList(KbjCategoryForm  kbjCates, String sortBy, String order) {
        int page;
        if(kbjCates.isSearch) {
            page = kbjCates.page;
        } else {
            page = 0;
        }

        /*判断是否输入父分类Id，输入执行if，未输入执行else */
        if(!kbjCates.parentId.equals("")){
            int parentId = Integer.valueOf(kbjCates.parentId);
             /*判断是否选择 有效性，输入执行if，未输入执行else */
            if(kbjCates.valid.equals("0") || kbjCates.valid.equals("1")) {
                System.out.println("services.valid.if" + kbjCates.valid);
                boolean isSelectValid = isSelected(kbjCates.valid);
                 /*判断是否选择 爬取对象，输入执行if，未输入执行else */
                if(kbjCates.isCrawleTarget.equals("0") || kbjCates.isCrawleTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawleTarget);
                    return supplyAsync(() -> {
                        /*所有检索条件都填写*/
                        return kbjCategoryReposity.find(isSelectValid, page, sortBy, order, kbjCates.name, parentId, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*爬取对象 条件未填写检索*/
                        return kbjCategoryReposity.find(isSelectValid, page, sortBy, order, kbjCates.name, parentId);
                    }, executionContext);
                }
            } else {
                if(kbjCates.isCrawleTarget.equals("0") || kbjCates.isCrawleTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawleTarget);
                    return supplyAsync(() -> {
                        /*有效性 条件未填写检索*/
                        return kbjCategoryReposity.find(page, sortBy, order, kbjCates.name, parentId, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*爬取对象、有效性 条件未填写检索*/
                        return kbjCategoryReposity.find(page, sortBy, order, kbjCates.name, parentId);
                    }, executionContext);
                }
            }
        } else {
            /*判断是否输入父分类Id，输入执行if，未输入执行else */
            if(kbjCates.valid.equals("0") || kbjCates.valid.equals("1")) {
                boolean isSelectValid = isSelected(kbjCates.valid);
                /*判断是否选择 爬取对象，输入执行if，未输入执行else */
                if(kbjCates.isCrawleTarget.equals("0") || kbjCates.isCrawleTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawleTarget);
                    return supplyAsync(() -> {
                        /*父分类Id 条件未填写检索*/
                        return kbjCategoryReposity.find(isSelectValid, page, sortBy, order, kbjCates.name, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*父分类Id、爬取对象 条件未填写检索*/
                        return kbjCategoryReposity.find(isSelectValid, page, sortBy, order, kbjCates.name);
                    }, executionContext);
                }
            } else {
                if(kbjCates.isCrawleTarget.equals("0") || kbjCates.isCrawleTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawleTarget);
                    return supplyAsync(() -> {
                        /*父分类Id、有效性 条件未填写检索*/
                        return kbjCategoryReposity.find(page, sortBy, order, kbjCates.name, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*父分类Id、爬取对象、有效性 条件未填写检索*/
                        return kbjCategoryReposity.find(page, sortBy, order, kbjCates.name);
                    }, executionContext);
                }
            }
        }
    }

    /**
     * 根据传递的字符串进行型转
     * @param selection
     * @return 返回布尔型的值用于检索
     */
    @Contract(pure = true)
    private boolean isSelected(String selection) {
        boolean isSeleced;
        if (selection.equals("0")) {
            isSeleced = false;
        } else {
            isSeleced = true;
        }
        return isSeleced;
    }

    /**
     *
     * @param kbjCateform
     * @author daiqingyi
     * @date 2017-12-05
     */
    public CompletionStage<Optional<Long>> addKbjCate(KbjCategoryForm  kbjCateform) {
        KbjCategory category = new KbjCategory();
        category.name = kbjCateform.name;
        category.parentId = Integer.valueOf(kbjCateform.parentId);
        category.isCrawleTarget = kbjCateform.bIsCrawleTarget;
        category.valid = kbjCateform.bValid;
        return supplyAsync(() -> {
            return kbjCategoryReposity.insert(category);
        }, executionContext);
    }

    /**
     *
     * @param id
     * @return
     */
    public CompletionStage<KbjCategory> find(Long id) {
        return supplyAsync(() -> {
            return kbjCategoryReposity.find(id);
        }, executionContext);
    }

    /**
     *
     * @param kbjCateform
     * @author daiqingyi
     * @date 2017-12-05
     */
    public CompletionStage<Optional<Long>> updKbjCate(KbjCategoryForm  kbjCateform){
        KbjCategory category = new KbjCategory();
        category.id = kbjCateform.id;
        category.name = kbjCateform.name;
        category.parentId = Integer.valueOf(kbjCateform.parentId);
        category.isCrawleTarget = kbjCateform.bIsCrawleTarget;
        category.valid = kbjCateform.bValid;
        return supplyAsync(() -> {
            return kbjCategoryReposity.update(category);
        }, executionContext);
    }
}
