package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.entities.DailyPrice;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import java.util.List;


public class DailyPriceRepo {

    private final EbeanServer ebeanServer;

    @Inject
    public DailyPriceRepo(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public List<DailyPrice> getSkuidsByDate(String mall, String skuid, String startDate, String endDate) {
        return ebeanServer.find(models.entities.DailyPrice.class).where()
                .eq("skuid", skuid)
                .eq("mall", mall)
                .between("date" ,startDate, endDate)
                .orderBy("date").findList();
    }
}
