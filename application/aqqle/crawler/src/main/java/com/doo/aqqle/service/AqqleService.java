package com.doo.aqqle.service;

import com.doo.aqqle.domain.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AqqleService {
    protected final ProductsRepository productsRepository;

//    protected final KeywordsService keywordsService;
}
