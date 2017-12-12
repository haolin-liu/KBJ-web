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

import static java.util.Objects.isNull;
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
        Long parentId = kbjCates.parentId;
        Boolean isCrawlTarget = isSelected(kbjCates.isCrawlTarget);
        Boolean valid = isSelected(kbjCates.valid);
        return supplyAsync(() -> {
            return kbjCategoryRepo.find(page, sortBy, order, kbjCates.name, parentId, isCrawlTarget, valid);
        }, executionContext);
    }

    /**
     * 根据传递的字符串进行型转
     * @param selection
     * @return 返回布尔型的值用于检索
     */
    @Contract(pure = true)
    private Boolean isSelected(String selection) {
        Boolean isSeleced;
        if ("0".equals(selection) || "1".equals(selection)) {
            if (selection.equals("0")) {
                isSeleced = false;
            } else {
                isSeleced = true;
            }
        } else {
            isSeleced = null;
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

    /**
     * 更新优先度
     * @param id
     * @param priority
     * @return
     * @author daiqingyi
     * @date 2017-12-11
     */
    public CompletionStage<PagedList<KbjCategory>> updPriority(Boolean isUpOrDown, Long id, Integer priority, KbjCategoryForm kbjCates, String sortBy, String order) {
        return supplyAsync(()-> {
            if (isUpOrDown){
                KbjCategory kbjCate = kbjCategoryRepo.find(priority + 1);
                if(!isNull(kbjCate)){
                    kbjCategoryRepo.updPriority(id, (priority + 1));
                    kbjCategoryRepo.updPriority(kbjCate.id, priority);
                }
            } else {
                KbjCategory kbjCate = kbjCategoryRepo.find(priority - 1);
                if (priority > 1){
                    kbjCategoryRepo.updPriority(id, (priority - 1));
                    kbjCategoryRepo.updPriority(kbjCate.id, priority);
                }
            }
            return Optional.empty();
        }, executionContext).thenApplyAsync((v) -> {
            Boolean isCrawlTarget = isSelected(kbjCates.isCrawlTarget);
            Boolean valid = isSelected(kbjCates.valid);
            return kbjCategoryRepo.find(kbjCates.page, sortBy, order, kbjCates.name, kbjCates.parentId, isCrawlTarget, valid);
        }, httpExecutionContext.current());
    }
}
