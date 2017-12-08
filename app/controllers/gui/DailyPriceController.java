package controllers.gui;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.DailyPrice;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repository.DailyPriceRepo;
import services.PriceService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class DailyPriceController extends Controller {

    private final DailyPriceRepo dailyPriceRepo;
    private final HttpExecutionContext httpExecutionContext;
    private final PriceService priceService;

    @Inject
    public DailyPriceController(
            DailyPriceRepo dailyPriceRepo
            ,HttpExecutionContext httpExecutionContext
            ,PriceService priceService) {
        this.dailyPriceRepo = dailyPriceRepo;
        this.httpExecutionContext = httpExecutionContext;
        this.priceService = priceService;
    }

    public CompletionStage<Result> priceTrend(String mall, String skuid) {
        String startDate = getStartDate();
        String endDate = getNow();

        return priceService.goodsPriceByDate("jd", skuid, startDate, endDate).thenApplyAsync(goods -> {
            ObjectNode json = Json.newObject();
            ArrayNode arrPrice = json.arrayNode();
            ArrayNode arrDate = json.arrayNode();
            List<DailyPrice> goodsPrice = new ArrayList<>();

            if (goods.size() < 6) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -3);
                Date start = calendar.getTime();//first

                long xInterval = 15 * 24 * 60 * 60 * 1000;
                long sta = start.getTime() - xInterval;

                for (int i = 0; i < 6 - goods.size();i++) {
                    Date point = new Date(sta);
                    arrDate.add(transDate(point));
                    arrPrice.add("");
                    sta -= xInterval;
                }
            }

            for (int i = 0; i < goods.size(); i++) {
                if (goodsPrice.size() == 0) {
                    goodsPrice.add(goods.get(0));
                } else if (i + 1 < goods.size()) {
                    if (!(goods.get(i).price.equals(goods.get(i - 1).price) && goods.get(i).price.equals(goods.get(i + 1).price))) {
                        goodsPrice.add(goods.get(i));
                    }
                } else if (i == goods.size() - 1) {
                    goodsPrice.add(goods.get(i));
                }
            }

            if (goodsPrice.isEmpty()) {
                json.put("results", "null");
            } else {
                for (DailyPrice row: goodsPrice) {
                    String priceDate = transDate(row.date);
                    arrDate.add(priceDate);
                    arrPrice.add(row.price);
                }
                json.put("results", "ok");
                json.put("skuid", skuid);
                json.putArray("price").addAll(arrPrice);
                json.putArray("date").addAll(arrDate);
            }
            return ok(Json.toJson(json));
        }, httpExecutionContext.current());
    }

    public String getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date formNow3Month = calendar.getTime();
        String startDate = dateFormat(formNow3Month);
        return startDate;
    }

    public String getNow() {
        Date date = new Date();
        String now = dateFormat(date);
        return now;
    }

    public String dateFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateForm = simpleDateFormat.format(date);
        return dateForm;
    }

    public String transDate (Date date) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd");
        String str = simpleDate.format(date);
        return str;
    }
}