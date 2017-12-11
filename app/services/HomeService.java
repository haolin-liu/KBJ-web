package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Product;
import models.entities.DailyPrice;
import play.libs.Json;
import repository.DailyPriceRepo;
import repository.DatabaseExecutionContext;
import solr.SolrI;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class HomeService {

    private final DailyPriceRepo dailyPriceRepo;
    private final DatabaseExecutionContext executionContext;
    private static SolrI solr;

    @Inject
    public HomeService(DailyPriceRepo dailyPriceRepo,
                       SolrI solr,
                       DatabaseExecutionContext executionContext
                       ) {
        this.dailyPriceRepo = dailyPriceRepo;
        this.solr = solr;
        this.executionContext = executionContext;
    }

    public CompletionStage<ArrayNode> getBargains(String cate) {
        return supplyAsync(() -> {
            List<DailyPrice> bargainPrices = dailyPriceRepo.getBargains(cate);
            ArrayNode bargainsJson = Json.newArray();
            for(DailyPrice bargainPrice: bargainPrices) {
                ObjectNode bargainJson = Json.newObject();
                Product bargain = solr.query("", bargainPrice.skuid);
                JsonNode productJson = Json.toJson(bargain);
                bargainJson.set("product", productJson);
                bargainsJson.add(bargainJson);
            }
            //TODO ST
            ObjectNode bargains = Json.newObject();
            bargains.put("leafId", cate);
            bargains.putArray("products").addAll(bargainsJson);
            // END
            return bargainsJson;
        }, executionContext);
    }
}
