package services;

import models.entities.DailyPrice;
import repository.DailyPriceRepo;
import repository.DatabaseExecutionContext;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class PriceService {

    private final DailyPriceRepo dailyPriceRepo;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public PriceService(DailyPriceRepo dailyPriceRepo, DatabaseExecutionContext executionContext) {
        this.dailyPriceRepo = dailyPriceRepo;
        this.executionContext = executionContext;
    }

    public CompletionStage<List<DailyPrice>> goodsPriceByDate(String mall, String skuid, String startDate, String endDate) {
        return supplyAsync(() -> {
            return dailyPriceRepo.getSkuidsByDate(mall, skuid, startDate, endDate);
        }, executionContext);
    }
}