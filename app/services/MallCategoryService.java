package services;

import io.ebean.PagedList;
import models.entities.MallCategory;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.MallCategoryRepo;
import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 *
 * @author liu
 * @date 2017/12/04
 */
public class MallCategoryService {
    private final FormFactory formFactory;
    private final MallCategoryRepo mallCategoryRepo;
    private final DatabaseExecutionContext executionContext;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public MallCategoryService(FormFactory formFactory,
                               MallCategoryRepo mallCategoryRepo,
                               DatabaseExecutionContext executionContext,
                               HttpExecutionContext httpExecutionContext) {
        this.formFactory = formFactory;
        this.mallCategoryRepo = mallCategoryRepo;
        this.executionContext = executionContext;
        this.httpExecutionContext = httpExecutionContext;

    }

    /**
     * 数据库查询
     * @param name
     * @param mall
     * @param chose
     * @param page
     * @return
     */
    public CompletionStage<PagedList<MallCategory>> getMallCategorySearch(String id, String name, String mall, int page, int chose, int valid, int bind) {
        Boolean isCrawlTarget;
        Boolean validFlg;
        Boolean isBound;

        if(chose == 1)  {
            isCrawlTarget = null;
        } else if (chose == 2) {
            isCrawlTarget = true;
        } else {
            isCrawlTarget = false;
        }

        if(valid == 1)  {
            validFlg = null;
        } else if (valid == 2) {
            validFlg = true;
        } else {
            validFlg = false;
        }

        if(bind == 1)  {
            isBound = null;
        } else if (bind == 2) {
            isBound = true;
        } else {
            isBound = false;
        }

        return supplyAsync(() -> {
            return mallCategoryRepo.find(id, name, mall, isBound, isCrawlTarget, validFlg, page, 10);
        }, executionContext);
    }

    /**
     * 数据库更新
     * @param data
     * @param name
     * @param mall
     * @param chose
     * @param page
     * @return
     */
    public CompletionStage<PagedList<MallCategory>> getMallCategoryUpdate(Map<String, String[]> data,
                                                                          String id, String name, String mall,
                                                                          int chose, int page, int valid, int bind) {
        return supplyAsync(() -> {
            return mallCategoryRepo.update(data);
        }, executionContext).thenApplyAsync((v) -> {
            Boolean isCrawlTarget;
            Boolean validFlg;
            Boolean isBound;

            if(chose == 1)  {
                isCrawlTarget = null;
            } else if (chose == 2) {
                isCrawlTarget = true;
            } else {
                isCrawlTarget = false;
            }

            if(valid == 1)  {
                validFlg = null;
            } else if (valid == 2) {
                validFlg = true;
            } else {
                validFlg = false;
            }

            if(bind == 1)  {
                isBound = null;
            } else if (bind == 2) {
                isBound = true;
            } else {
                isBound = false;
            }

            return mallCategoryRepo.find(id, name, mall, isBound, isCrawlTarget, validFlg, page, 10);

        }, httpExecutionContext.current());
    }

    /**
     * 数据库插入
     * @param mallCategory
     * @return
     */
    public CompletionStage<Long> getMallCategorySave(MallCategory mallCategory) {
         return supplyAsync(() -> {
            return mallCategoryRepo.insert(mallCategory);
        }, executionContext);
    }

    /**
     * 返回一个空的Optional
     * @return
     */
    public CompletionStage<Optional<Long>> getMallCategoryBadSave() {
        return supplyAsync(() -> {
            return mallCategoryRepo.empty();
        }, executionContext);
    }

    /**
     * 单条更新时数据查询
     * @param id
     * @return
     */
    public CompletionStage<MallCategory> getMallCategoryEditOne(Long id) {
        return supplyAsync(() -> {
            return mallCategoryRepo.find(id);
        }, executionContext);
    }

    /**
     * 单条数据更新
     * @param id
     * @param mallCategory
     * @return
     */
    public CompletionStage<Optional<Long>> getMallCategoryUpdateOne(Long id, MallCategory mallCategory) {
        return supplyAsync(() -> {
            return mallCategoryRepo.updateOne(id, mallCategory);
        }, executionContext);
    }
}
