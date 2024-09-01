package com.doo.aqqle.service;


import com.doo.aqqle.domain.ProductsRepository;
import com.doo.aqqle.element.Site;
import com.doo.aqqle.factory.SiteFactory;
import com.doo.aqqle.factory.YahooFactory;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class YahooService extends AqqleService implements AqqleCrawler {

    public YahooService(ProductsRepository productsRepository) {
        super(productsRepository);
    }
    private final int CRAWLING_LIMIT = 100;

    @Override
    public void execute() {
        Site site = SiteFactory.getSite(new YahooFactory());
        String listUrl = site.getUrl(1, 10);
        try {
            Document listDocument = Jsoup.connect(listUrl)
                    .timeout(5000)
                    .get();

            Elements tableTr = listDocument.select("table.markets-table>tbody>tr");

            List<Tuple2<String, String>> tableTrs = tableTr.stream()
                    .map(x -> Tuple.of(
                            x.select("span.symbol").text(),
                            x.select("span.longName").text()
                    ))
                    .collect(Collectors.toList());

            tableTrs.forEach(tuple -> {
                System.out.println("Symbol: " + tuple._1);
                System.out.println("Long: " + tuple._2);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//
//    public void getData(CrawlerDto crawlerDto) {
//
//        List<Keywords> keywords = keywordsService.getData();
//
//        keywords.stream().forEach(obj ->
//                {
//
//                    try {
//                        int i = 0;
//                        while (true) {
//                            Thread.sleep(2000); //1초 대기
//
//                            String listUrl = "https://search.shopping.naver.com/search/all.nhn?origQuery=" + obj.getKeyword() + "&pagingIndex=" + i + "&pagingSize=40&productSet=model&viewType=list&sort=rel&frm=NVSHMDL&query=" + obj.getKeyword();
//
//                            Document listDocument = Jsoup.connect(listUrl)
//                                    .timeout(5000)
//                                    .get();
//
//                            Elements urls = listDocument.select("div.thumbnail_thumb_wrap__1pEkS>a");
//
//                            List<String> detailUrl = urls
//                                    .stream()
//                                    .map(x -> x.attr("abs:href"))
//                                    .collect(Collectors.toList());
//
//                            detailUrl.forEach(url -> {
//                                try {
//                                    Thread.sleep(1000); //1초 대기
//
//                                    System.out.println(url);
//                                    Document document = Jsoup.connect(url)
//                                            .timeout(5000)
//                                            .get();
//
//                                    Elements title = document.select("div.top_summary_title__15yAr>h2");
//                                    Elements price = document.select("em.lowestPrice_num__3AlQ-");
//                                    Elements brand = document.select("div.top_info_inner__1cEYE>span:first-child>em");
//                                    Elements category = document.select("div.top_breadcrumb__yrBH6 a");
//                                    Elements image = document.select("div.image_thumb__20xyr>img");
//
//                                    String[] categoryArray = category.text().split(" ");
//
//                                    Map<Integer, String> categoryLists = new HashMap<>();
//                                    for (var j = 0; j < categoryArray.length; j++) {
//                                        categoryLists.put(j, Optional.ofNullable(categoryArray[j]).orElse(null));
//                                    }
//
//                                    productsRepository.save(Products.builder()
//                                                    .keyword(crawlerDto.getKeyword())
//                                                    .name(title.text())
//                                                    .price(price.text().equals("") ? 0 : Integer.parseInt(price.text().replace(",", "")))
//                                                    .brand(brand.text())
//                                                    .category(category.text())
//                                                    .category1(categoryLists.get(0))
//                                                    .category2(categoryLists.get(1))
//                                                    .category3(categoryLists.get(2))
//                                                    .category4(categoryLists.get(3))
//                                                    .category5(categoryLists.get(4))
//                                                    .image(image.attr("src"))
////                                            .imageVector(ImageToVectorOpenCV.getVector(image.attr("src")).toString())
//                                                    .type("C")
//                                                    .build()
//                                    );
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                } catch (InterruptedException ie) {
//                                    ie.printStackTrace();
//                                }
//                            });
//
//                            if (i > CRAWLING_LIMIT) {
//                                break;
//                            }
//                            i++;
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException i) {
//
//                    }
//                }
//        );
//
//
//    }


}
