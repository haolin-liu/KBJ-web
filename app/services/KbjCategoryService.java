package services;

import io.ebean.PagedList;
import models.entities.KbjCategory;
import models.forms.KbjCategoryForm;
import org.jetbrains.annotations.Contract;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.KbjCategoryRepo;

import javax.inject.Inject;
import java.util.*;
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
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public KbjCategoryService(DatabaseExecutionContext executionContext,
                              KbjCategoryRepo kbjCategoryRepo,
                              HttpExecutionContext httpExecutionContext) {
        this.executionContext = executionContext;
        this.kbjCategoryRepo = kbjCategoryRepo;
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

    public CompletionStage<Void> addKbjCate(KbjCategoryForm  kbjCateform) {
        KbjCategory category = new KbjCategory();
        category.name = kbjCateform.name;
        category.isCrawlTarget = kbjCateform.bIsCrawlTarget;
        category.valid = kbjCateform.bValid;
        category.priority = 99;
        return supplyAsync(() -> {
            KbjCategory parentCate = kbjCategoryRepo.find(kbjCateform.parentId);
            category.parent = parentCate;
            kbjCategoryRepo.insert(category);
            return null;
        }, executionContext);
    }

    public CompletionStage<KbjCategory> find(Long id) {
        return supplyAsync(() -> {
            return kbjCategoryRepo.find(id);
        }, executionContext);
    }

    public CompletionStage<Void> updKbjCate(KbjCategoryForm  kbjCateform){
        KbjCategory category = new KbjCategory();
        category.id = kbjCateform.id;
        category.name = kbjCateform.name;
        category.isCrawlTarget = kbjCateform.bIsCrawlTarget;
        category.valid = kbjCateform.bValid;
        return supplyAsync(() -> {
            KbjCategory parentCate = kbjCategoryRepo.find(kbjCateform.parentId);
            category.parent = parentCate;
            kbjCategoryRepo.update(category);
            return null;
        }, executionContext);
    }

    public CompletionStage<Map<String, String>> getParents() {
        return supplyAsync(()-> {
            Map<String, String> options = getParent();
            return options;
        }, httpExecutionContext.current());
    }

    public CompletionStage<PagedList<KbjCategory>> updPriority(Boolean isUpOrDown, Long id, Integer priority,
                                                               KbjCategoryForm kbjCates, String sortBy, String order) {
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

    public Map<String, String> getParent() {
        List<KbjCategory> list = kbjCategoryRepo.getParents();
        HashMap<String, String> options = new LinkedHashMap<>();
        for (KbjCategory c : list) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    }

    @Contract(pure = true)
    private Boolean isSelected(String selection) {
        return "0".equals(selection) ? false : ("1".equals(selection) ? true : null);
    }

}
