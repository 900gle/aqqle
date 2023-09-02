package com.doo.aqqle.service;


import com.doo.aqqle.annotation.Timer;
import com.doo.aqqle.domain.Keywords;
import com.doo.aqqle.domain.KeywordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordsService {

    private final KeywordsRepository keywordsRepository;

    @Transactional
    @Timer
    public List<Keywords> getData() {
       return keywordsRepository.findAllByUseYn("Y");
    }

    @Transactional
    @Timer
    public void putData(Keywords k){
        keywordsRepository.save(Keywords.builder().keyword(k.getKeyword()).use("N").build());
    }


}
