package com.doo.aqqle.controller;


import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.service.CacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "3. Cache Apis")
@RequestMapping("api/search")
@RequiredArgsConstructor
public class CacheRestController {

    private final CacheService cacheService;

    @CrossOrigin("*")
    @ApiOperation(value = "search", notes = "검색")
    @GetMapping("cache")
    public CommonResult getDatas(
            @ApiParam(value = "countryCode") @RequestParam(value = "countryCode", defaultValue = "KR", required = true) @Validated final String countryCode
    ) {
        return cacheService.getCaches(countryCode);
    }

}
