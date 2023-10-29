package com.doo.aqqle.shop.controller;


import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.model.request.LocationRequest;
import com.doo.aqqle.model.request.ShopRequest;
import com.doo.aqqle.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "1. Shop Apis")
@RequestMapping("api/search")
@RequiredArgsConstructor
public class ShopRestController {

    private final ShopService service;

    @CrossOrigin("*")
    @ApiOperation(value = "search", notes = "검색")
    @GetMapping("shop")
    public CommonResult getDatas(
            @ModelAttribute ShopRequest request

    ) {
        return service.getProducts(request);
    }

}
