package com.doo.aqqle.controller;


import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.service.GoodsService;
import com.doo.aqqle.service.LocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "2. Locations Apis")
@RequestMapping("api/search")
@RequiredArgsConstructor
public class LocationRestController {

    private final LocationService locationService;

    @CrossOrigin("*")
    @ApiOperation(value = "search", notes = "검색")
    @GetMapping("location")
    public CommonResult getDatas(
            @ApiParam(value = "countryCode") @RequestParam(value = "countryCode", defaultValue = "KR", required = true) @Validated final String countryCode
    ) {
        return locationService.getLocations(countryCode);
    }

}
