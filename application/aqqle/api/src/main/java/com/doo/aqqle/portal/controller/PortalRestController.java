package com.doo.aqqle.portal.controller;


import com.doo.aqqle.model.request.LocationRequest;
import com.doo.aqqle.model.request.ShopRequest;
import com.doo.aqqle.service.PortalService;
import com.doo.aqqle.model.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "5. Portal Apis")
@RequestMapping("api/portal")
@RequiredArgsConstructor
public class PortalRestController {

    private final PortalService service;

    @CrossOrigin("*")
    @ApiOperation(value = "search", notes = "검색")
    @GetMapping("search")
    public CommonResult getDatas(
    ) {
        LocationRequest locationRequest = new LocationRequest();
        ShopRequest shopRequest = new ShopRequest();
        return service.portalInfos(shopRequest, locationRequest);
    }
}
