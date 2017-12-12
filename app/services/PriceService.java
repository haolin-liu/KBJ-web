package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.DailyPrice;
import play.libs.Json;
import repository.DailyPriceRepo;
import repository.DatabaseExecutionContext;
import utils.ConfigUtil;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class PriceService {

    private final DailyPriceRepo dailyPriceRepo;
    private final ConfigUtil config;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public PriceService(DailyPriceRepo dailyPriceRepo,
                        ConfigUtil config,
                        DatabaseExecutionContext executionContext) {
        this.dailyPriceRepo = dailyPriceRepo;
        this.config = config;
        this.executionContext = executionContext;
    }

    public CompletionStage<JsonNode> getPrices(String mall, String skuid) {
        return supplyAsync(() -> {

            ObjectNode json = Json.newObject();
            ArrayNode xAxis = Json.newArray();
            ArrayNode arrPrice = Json.newArray();
            ArrayNode arrDate = Json.newArray();

            Date fromDate = getStartDate();
            Date toDate = new Date();

            List<DailyPrice> prices = dailyPriceRepo.getSkuidsByDate(mall, skuid, fromDate, toDate);

            int points = config.getPointsOfXaxisOfPriceHis();

            int step = (int)Math.ceil(prices.size() / (double)points);
            /*
            // labels of x axis
            // ----------------------
            Date first = new Date();
            if (prices.size() > 0) {
                first = prices.get(0).date;
            }
            Date last = new Date();
            int days = (int)((last.getTime() - first.getTime()) / (24 * 60 * 60 * 1000));

            List<String> labels = new ArrayList<>();
            if (days > points) {
                step = (int)Math.ceil(days / (double)points);
                while (points > 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(last);
                    cal.add(Calendar.DAY_OF_MONTH, -step);
                    last = cal.getTime();
                    labels.add(getStrFromDate(last));
                    points--;
                }

                // revert the list and convert it to an json array.
                Collections.reverse(labels);
                for (String l : labels) {
                    Logger.debug("===" + l);
                    xAxis.add(l);
                }
            } else {
                step = points;
                for (DailyPrice p : prices) {
                    xAxis.add(getStrFromDate(p.date));
                    Logger.debug("---" + p.date);
                }
            }
            */
            // ----------------------

            for (DailyPrice row : prices) {
                arrPrice.add(row.price);
                arrDate.add(getStrFromDate(row.date));
            }

            if (prices.size() == 0) {
                json.put("results", "null");
            } else {
                json.put("results", "ok");
                json.put("skuid", skuid);
                json.set("price", arrPrice);
                json.set("date", arrDate);
                // json.set("xAxis", xAxis);
                json.put("step", step);
            }

            return Json.toJson(json);
        }, executionContext);
    }

    private Date getStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -config.getOffsetMonthOfPriceHis());
        return cal.getTime();
    }

    private String getStrFromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}