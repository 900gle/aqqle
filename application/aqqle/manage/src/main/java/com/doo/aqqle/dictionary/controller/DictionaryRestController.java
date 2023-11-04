package com.doo.aqqle.dictionary.controller;


import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.service.DictionarysService;
import com.doo.aqqle.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(tags = "1. Dictionarys Apis")
@RequestMapping("manage")
@RequiredArgsConstructor
public class DictionaryRestController {

    private final DictionarysService dictionarysService;
    private final ResponseService responseService;

    @ApiOperation(value = "키워드 저장", notes = "키워드 저장")
    @CrossOrigin("*")
    @PostMapping("distionarys")
    public void postData(@ApiParam(value = "words") @RequestParam(value = "words", defaultValue = "", required = true) List<String> words) {
        dictionarysService.post(words);
    }

    @ApiOperation(value = "키워드 조회", notes = "키워드 조회")
    @CrossOrigin("*")
    @GetMapping("distionarys")
    public CommonResult getData() {
        return responseService.getSingleResult(dictionarysService.get());
    }

    @ApiOperation(value = "키워드 업데이트", notes = "키워드 업데이트")
    @CrossOrigin("*")
    @PutMapping("distionarys")
    public CommonResult putData(
            @ApiParam(value = "id") @RequestParam(value = "id", defaultValue = "", required = true) Long id,
            @ApiParam(value = "useYn") @RequestParam(value = "useYn", defaultValue = "", required = true) String useYn
    ) {
        return responseService.getSingleResult(dictionarysService.put(id, useYn));
    }



}
