package com.doo.aqqle.service;


import com.doo.aqqle.HostUrl;
import com.doo.aqqle.annotation.Timer;
import com.doo.aqqle.component.ElementComponent;
import com.doo.aqqle.component.TextEmbedding;
import com.doo.aqqle.domain.Keywords;
import com.doo.aqqle.dto.TextEmbeddingDTO;
import com.doo.aqqle.element.Site;
import com.doo.aqqle.repository.GoodsNaver;
import com.doo.aqqle.repository.GoodsNaverRepository;
import com.doo.aqqle.repository.Stock;
import com.doo.aqqle.repository.StockRepository;
import com.doo.aqqle.utils.FileDownload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    private final int CRAWLING_LIMIT = 100;

    @Timer
    public void getData(String type) {

        List<Stock> stockList = stockRepository.findAllByUseYn("Y");


        stockList.stream().forEach(x-> {

            String listUrl = "https://query1.finance.yahoo.com/v7/finance/download/"+x.getCompanyCode()+"?period1=1585621480&period2=1717243880&interval=1d&events=history&includeAdjustedClose=true";


            try {
                FileDownload.downloadFile(listUrl, "/data/stock/");
                System.out.println("파일 다운로드 완료.");
            } catch (IOException e) {
                System.out.println("파일 다운로드 중 오류 발생: " + e.getMessage());
            }

        });







//        https://query1.finance.yahoo.com/v7/finance/download/VFS?period1=1685621480&period2=1717243880&interval=1d&events=history&includeAdjustedClose=true
//        https://query1.finance.yahoo.com/v7/finance/download/NVDA?period1=917015400&period2=1717244542&interval=1d&events=history&includeAdjustedClose=true
//        https://query1.finance.yahoo.com/v7/finance/download/TSLA?period1=1277818200&period2=1717244467&interval=1d&events=history&includeAdjustedClose=true







    }


}
