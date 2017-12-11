package services;

import io.ebean.PagedList;
import models.entities.KbjCategory;
import models.forms.KbjCategoryForm;
import org.jetbrains.annotations.Contract;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.KbjCategoryRepo;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This service created to get datas from entity
 *  @author daiqingyi
 *  @date 2017/12/1
 */

public class KbjCategoryService {

    private final DatabaseExecutionContext executionContext;
    private final KbjCategoryRepo kbjCategoryRepo;
    private final KbjCategoryForm kbjCategoryForm;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public KbjCategoryService(DatabaseExecutionContext executionContext,
                              KbjCategoryRepo kbjCategoryRepo,
                              KbjCategoryForm kbjCategoryForm,
                              HttpExecutionContext httpExecutionContext) {
        this.executionContext = executionContext;
        this.kbjCategoryRepo = kbjCategoryRepo;
        this.kbjCategoryForm = kbjCategoryForm;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<PagedList<KbjCategory>> findList(KbjCategoryForm kbjCates, String sortBy, String order) {
        int page;
        if(kbjCates.isSearch) {
            page = kbjCates.page;
        } else {
            page = 0;
        }

        /*判断是否输入父分类Id，输入执行if，未输入执行else */
        if(kbjCates.parentId != null){
            long parentId = kbjCates.parentId;
             /*判断是否选择 有效性，输入执行if，未输入执行else */
            if(kbjCates.valid.equals("0") || kbjCates.valid.equals("1")) {
                System.out.println("services.valid.if" + kbjCates.valid);
                boolean isSelectValid = isSelected(kbjCates.valid);
                 /*判断是否选择 爬取对象，输入执行if，未输入执行else */
                if(kbjCates.isCrawlTarget.equals("0") || kbjCates.isCrawlTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawlTarget);
                    return supplyAsync(() -> {
                        /*所有检索条件都填写*/
                        return kbjCategoryRepo.find(isSelectValid, page, sortBy, order, kbjCates.name, parentId, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*爬取对象 条件未填写检索*/
                        return kbjCategoryRepo.find(isSelectValid, page, sortBy, order, kbjCates.name, parentId);
                    }, executionContext);
                }
            } else {
                if(kbjCates.isCrawlTarget.equals("0") || kbjCates.isCrawlTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawlTarget);
                    return supplyAsync(() -> {
                        /*有效性 条件未填写检索*/
                        return kbjCategoryRepo.find(page, sortBy, order, kbjCates.name, parentId, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*爬取对象、有效性 条件未填写检索*/
                        return kbjCategoryRepo.find(page, sortBy, order, kbjCates.name, parentId);
                    }, executionContext);
                }
            }
        } else {
            /*判断是否输入父分类Id，输入执行if，未输入执行else */
            if(kbjCates.valid.equals("0") || kbjCates.valid.equals("1")) {
                boolean isSelectValid = isSelected(kbjCates.valid);
                /*判断是否选择 爬取对象，输入执行if，未输入执行else */
                if(kbjCates.isCrawlTarget.equals("0") || kbjCates.isCrawlTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawlTarget);
                    return supplyAsync(() -> {
                        /*父分类Id 条件未填写检索*/
                        return kbjCategoryRepo.find(isSelectValid, page, sortBy, order, kbjCates.name, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*父分类Id、爬取对象 条件未填写检索*/
                        return kbjCategoryRepo.find(isSelectValid, page, sortBy, order, kbjCates.name);
                    }, executionContext);
                }
            } else {
                if(kbjCates.isCrawlTarget.equals("0") || kbjCates.isCrawlTarget.equals("1")) {
                    boolean isSelectCrawleTarget = isSelected(kbjCates.isCrawlTarget);
                    return supplyAsync(() -> {
                        /*父分类Id、有效性 条件未填写检索*/
                        return kbjCategoryRepo.find(page, sortBy, order, kbjCates.name, isSelectCrawleTarget);
                    }, executionContext);
                } else {
                    return supplyAsync(() -> {
                        /*父分类Id、爬取对象、有效性 条件未填写检索*/
                        return kbjCategoryRepo.find(page, sortBy, order, kbjCates.name);
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
        category.isCrawlTarget = kbjCateform.bIsCrawlTarget;
        category.valid = kbjCateform.bValid;
        category.priority = 99;
        return supplyAsync(() -> {
            KbjCategory parentCate = kbjCategoryRepo.find(kbjCateform.parentId);
            category.parent = parentCate;
            return kbjCategoryRepo.insert(category);
        }, executionContext);
    }

    /**
     *
     * @param id
     * @return
     */
    public CompletionStage<KbjCategory> find(Long id) {
        return supplyAsync(() -> {
            return kbjCategoryRepo.find(id);
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
        category.isCrawlTarget = kbjCateform.bIsCrawlTarget;
        category.valid = kbjCateform.bValid;
        return supplyAsync(() -> {
            KbjCategory parentCate = kbjCategoryRepo.find(kbjCateform.parentId);
            category.parent = parentCate;
            return kbjCategoryRepo.update(category);
        }, executionContext);
    }

    /**
     * 异步取得父分类
     * @return
     * @author daiqingyi
     * @date 2017-12-07
     */
    public CompletionStage<Map<String, String>> getParents() {
            Map<String, String> options = kbjCategoryRepo.getParents();
            return supplyAsync(()-> {
                return options;
            }, httpExecutionContext.current());
    }

    /**
     * 非异步取得父分类
     * @return
     * @author daiqingyi
     * @date 2017-12-07
     */
    public Map<String, String> getParent() {
        Map<String, String> options = kbjCategoryRepo.getParents();
        return options;
    }
}
