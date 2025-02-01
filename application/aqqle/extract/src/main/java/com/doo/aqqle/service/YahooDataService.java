package com.doo.aqqle.service;

import com.doo.aqqle.annotation.Timer;
import com.doo.aqqle.repository.StockDataRepository;
import com.doo.aqqle.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service("YahooDataService")
public class YahooDataService extends ExtractService implements AqqleExtract {

    private final YahooDataTaskService yahooDataTaskService;
    private List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();

    public YahooDataService(StockRepository stockRepository, StockDataRepository stockDataRepository, YahooDataTaskService yahooDataTaskService) {
        super(stockRepository, stockDataRepository);
        this.yahooDataTaskService = yahooDataTaskService;
    }

    @Override
    @Timer
    public void execute() {

        String type = "yahoo";
        String extractPath = "/data/" + type + "/static/";

        File file = new File(extractPath);

        if (file.exists() && file.isDirectory()) {
            try {
                FileUtils.cleanDirectory(file);
            } catch (IOException e) {
                e.getStackTrace();
            }

        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String directory = extractPath + LocalDateTime.now().format(dateTimeFormatter).toString();

        System.out.println(directory);
        IndexFileService.createDirectory(directory);

        long count = stockDataRepository.count();
        int chunk = 500;
        double endPage = Math.ceil(count / chunk);

        for (int i = 0; i < endPage + 1; i++) {
            CompletableFuture<Integer> completableFuture = yahooDataTaskService.task(i, chunk, directory);
            completableFutures.add(completableFuture);
        }

        for (CompletableFuture<Integer> future : completableFutures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }
}

