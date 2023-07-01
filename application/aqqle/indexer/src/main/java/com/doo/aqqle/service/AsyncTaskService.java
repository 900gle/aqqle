package com.doo.aqqle.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskService {





    @Async("executor")
    public CompletableFuture<Integer> task(int i) {


        System.out.println(i);

        List<String> list = new ArrayList<>();
        list.add("asdf");


        return CompletableFuture.supplyAsync(list::size);
    }



//
//
//    @Async("executor")
//    public CompletableFuture<Integer> task(int i) {
//
//
//        System.out.println("aaaaaaa");
//
//        int size  = 1 + i;
//
//        return CompletableFuture.supplyAsync(size);
//    }

}
