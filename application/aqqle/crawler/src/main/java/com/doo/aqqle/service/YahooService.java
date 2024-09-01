package com.doo.aqqle.service;


import com.doo.aqqle.element.Site;
import com.doo.aqqle.factory.SiteFactory;
import com.doo.aqqle.factory.YahooFactory;
import com.doo.aqqle.repository.Stock;
import com.doo.aqqle.repository.StockRepository;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YahooService extends AqqleService implements AqqleCrawler {

    public YahooService(StockRepository stockRepository) {
        super(stockRepository);
    }

    private final int CRAWLING_COUNT = 25;

    @Override
    public void execute() {
        Site site = SiteFactory.getSite(new YahooFactory());

        for (var i = 1; i < 200; i++) {
            int start = i > 1 ? i * CRAWLING_COUNT: 1;
            String listUrl = site.getUrl(start, CRAWLING_COUNT);
            try {
                Document listDocument = Jsoup.connect(listUrl)
                        .timeout(5000)
                        .get();

                Elements tableTr = listDocument.select(site.getListCssSelector());

                List<Tuple2<String, String>> tableTrs = tableTr.stream()
                        .map(x -> Tuple.of(
                                x.select("span.symbol").text(),
                                x.select("span.longName").text()
                        ))
                        .collect(Collectors.toList());

                tableTrs.forEach(tuple -> {
                    stockRepository.save(Stock.builder()
                            .company(tuple._2)
                            .companyCode(tuple._1)
                            .exchange("")
                            .startDate(LocalDateTime.now().toString())
                            .endDate(LocalDateTime.now().toString())
                            .type("BUY")
                            .use("N")
                            .build());
                    System.out.println("Symbol: " + tuple._1);
                    System.out.println("Long: " + tuple._2);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
