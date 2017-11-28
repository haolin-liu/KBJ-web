package services;

import io.ebean.PagedList;
import models.entities.MallCategory;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.MallCategoryRepository;
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
    private final MallCategoryRepository mallCategoryRepository;
    private final DatabaseExecutionContext executionContext;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public MallCategoryService(FormFactory formFactory,
                               MallCategoryRepository mallCategoryRepository,
                               DatabaseExecutionContext executionContext,
                               HttpExecutionContext httpExecutionContext) {
        this.formFactory = formFactory;
        this.mallCategoryRepository = mallCategoryRepository;
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
    public CompletionStage<PagedList<MallCategory>> getMallCategorySearch(String name, String mall, int chose, int page, int valid) {
        String flg;
        String validFlg;
        if (chose == 1 && valid == 1) {
            return supplyAsync(() -> {
                return mallCategoryRepository.findAll(name, mall, page, 10);
            }, executionContext);
        } else if (chose == 1 && valid == 2) {
            flg = "";
            validFlg = "1";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else if (chose == 1 && valid == 3) {
            flg = "";
            validFlg = "0";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        }else if (chose == 2 && valid == 1) {
            flg = "1";
            validFlg = "";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else if (chose == 2 && valid == 2) {
            flg = "1";
            validFlg = "1";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else if (chose == 2 && valid == 3) {
            flg = "1";
            validFlg = "0";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else if (chose == 3 && valid == 1) {
            flg = "0";
            validFlg = "";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else if (chose == 3 && valid == 2) {
            flg = "0";
            validFlg = "1";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        } else {
            flg = "0";
            validFlg = "0";
            return supplyAsync(() -> {
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }, executionContext);
        }
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
                                                                          String name, String mall,
                                                                          int chose, int page, int valid) {
        return supplyAsync(() -> {
            return mallCategoryRepository.update(data);
        }, executionContext).thenApplyAsync((v) -> {
            String flg;
            String validFlg;
            if (chose == 1 && valid == 1) {
                return mallCategoryRepository.findAll(name, mall, page, 10);
            } else if (chose == 1 && valid == 2) {
                flg = "";
                validFlg = "1";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 1 && valid == 3) {
                flg = "";
                validFlg = "0";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 2 && valid == 1) {
                flg = "1";
                validFlg = "";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 2 && valid == 2) {
                flg = "1";
                validFlg = "1";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 2 && valid == 3) {
                flg = "1";
                validFlg = "0";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 3 && valid == 1) {
                flg = "0";
                validFlg = "";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else if (chose == 3 && valid == 2) {
                flg = "0";
                validFlg = "1";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            } else {
                flg = "0";
                validFlg = "0";
                return mallCategoryRepository.find(name, mall, flg, page, 10, validFlg);
            }
        }, httpExecutionContext.current());
    }

    /**
     * 数据库插入
     * @param mallCategory
     * @return
     */
    public CompletionStage<Long> getMallCategorySave(MallCategory mallCategory) {
         return supplyAsync(() -> {
            return mallCategoryRepository.insert(mallCategory);
        }, executionContext);
    }

    /**
     * 返回一个空的Optional
     * @return
     */
    public CompletionStage<Optional<Long>> getMallCategoryBadSave() {
        return supplyAsync(() -> {
            return mallCategoryRepository.empty();
        }, executionContext);
    }

    /**
     * 单条更新时数据查询
     * @param id
     * @return
     */
    public CompletionStage<MallCategory> getMallCategoryEditOne(Long id) {
        return supplyAsync(() -> {
            return mallCategoryRepository.searchOne(id);
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
            return mallCategoryRepository.updateOne(id, mallCategory);
        }, executionContext);
    }
}
