package controllers.gui;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.DailyPrice;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repository.DailyPriceRepo;
import services.PriceService;
import utils.ConfigUtil;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class DailyPriceController extends Controller {

    private final HttpExecutionContext httpExecutionContext;
    private final PriceService priceService;

    @Inject
    public DailyPriceController(HttpExecutionContext httpExecutionContext,
                                PriceService priceService) {
        this.httpExecutionContext = httpExecutionContext;
        this.priceService = priceService;
    }

    public CompletionStage<Result> priceTrend(String mall, String skuid) {
        return priceService.getPrices(mall, skuid).thenApplyAsync(json-> {
            return ok(Json.toJson(json));
        }, httpExecutionContext.current());
    }

}