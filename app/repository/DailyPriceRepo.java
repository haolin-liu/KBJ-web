package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlRow;
import models.entities.DailyPrice;
import models.entities.DailyPriceView;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DailyPriceRepo {

    private final EbeanServer ebeanServer;

    @Inject
    public DailyPriceRepo(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public List<DailyPrice> getSkuidsByDate(String mall, String skuid, Date fromDate, Date toDate) {
        return ebeanServer.find(models.entities.DailyPrice.class)
                .where()
                .eq("mall", mall)
                .eq("skuid", skuid)
                .between("date", fromDate, toDate)
                .orderBy("date")
                .findList();
    }

    public List<DailyPriceView> getBargains1() {

        String sql = "select T1.skuid AS skuid " ;
        sql += " , T1.price AS cur_price ";
        sql += " , T2.price AS pre_price ";
        sql += " , (T2.price - T1.price)/T1.price AS percent ";
        sql += " FROM daily_price T1 ";
        sql += " INNER JOIN daily_price T2 ";
        sql += " ON T1.skuid = T2.skuid ";
        sql += " WHERE T1.date = CURDATE() ";
        sql += " AND T2.date = date_sub(curdate(),interval 1 day)";
        sql += " ORDER BY percent DESC ";
        sql += " , T1.price ASC";
        sql += " LIMIT 10";

        List<SqlRow> sqlRows =  ebeanServer.createSqlQuery(sql).findList();
        List<DailyPriceView> prices = new ArrayList<>();
        for (SqlRow sqlRow : sqlRows) {
            DailyPriceView price = new DailyPriceView();
            price.skuid = (String)sqlRow.get("skuid");
            price.cur_price = (Float) sqlRow.get("cur_price");
            price.pre_price = (Float)sqlRow.get("pre_price");
            price.reduce_percentage = (Float)sqlRow.get("percent");
            prices.add(price);
        }
        return prices;
    }

    public List<DailyPrice> getBargains(String cate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return ebeanServer.find(DailyPrice.class).where()
                .eq("kbj_cate_id", cate)
                .eq("date", df.format(new Date()))
                .order().desc("discount_rate")
                .setMaxRows(10)
                .findList();
    }
}
