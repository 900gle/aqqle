package com.doo.aqqle.controller;


import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "1. Goods Apis")
@RequestMapping("api/search")
@RequiredArgsConstructor
public class ShopRestController {

    private final GoodsService goodsService;

    @CrossOrigin("*")
    @ApiOperation(value = "search", notes = "검색")
    @GetMapping("shop")
    public CommonResult getDatas(
            @ApiParam(value = "검색어") @RequestParam(value = "searchWord", defaultValue = "나이키", required = true) @Validated final String searchWord
    ) {
        return goodsService.getProducts(searchWord);
    }

}
