package com.doo.aqqle.service;

import com.doo.aqqle.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AqqleService {
    protected final StockRepository stockRepository;

//    protected final KeywordsService keywordsService;
}
