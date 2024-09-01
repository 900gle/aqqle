package com.doo.aqqle.element;

import org.springframework.web.util.UriComponentsBuilder;

public class Yahoo extends Site<Integer, Integer> {

    private static final String URL = "https://finance.yahoo.com/markets/stocks/most-active/";

    @Override
    public String getUrl(Integer start, Integer count) {
        return UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("start", start)
                .queryParam("count", count)
                .build().toString();
    }
}



