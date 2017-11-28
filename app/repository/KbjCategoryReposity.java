package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.entities.KbjCategory;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * @author daiqingyi
 * @date 2017-11-27
 */
public class KbjCategoryReposity {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public KbjCategoryReposity(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public CompletionStage<List<KbjCategory>> find() {
        return supplyAsync(() -> {
            return ebeanServer.find(KbjCategory.class).findList();
        } , executionContext);
    }
}
