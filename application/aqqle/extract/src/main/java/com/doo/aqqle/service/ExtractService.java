package com.doo.aqqle.service;

import com.doo.aqqle.annotation.IndexerLog;
import com.doo.aqqle.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ExtractService {

    private final GoodsRepository goodsRepository;

    private final AsyncTaskService asyncTaskService;

    private List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();

    @IndexerLog
    public void index(String type) throws IOException {

        File file = new File("/data/static/");
        FileUtils.cleanDirectory(file);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String directory = "/data/static/"+ LocalDateTime.now().format(dateTimeFormatter).toString();

        System.out.println(directory);
        IndexFileService.createDirectory(directory);
//
        long count = goodsRepository.count();
        int chunk = 500;
        double endPage = Math.ceil(count / chunk);

        for (int i = 0; i < endPage + 1; i++) {
            CompletableFuture<Integer> completableFuture = asyncTaskService.task(i, chunk, directory);
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
