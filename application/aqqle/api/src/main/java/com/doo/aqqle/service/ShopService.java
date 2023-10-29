package com.doo.aqqle.service;


import com.doo.aqqle.HostUrl;
import com.doo.aqqle.component.TextEmbedding;
import com.doo.aqqle.component.query.LocationSearchQuery;
import com.doo.aqqle.component.query.ShopSearchQuery;
import com.doo.aqqle.component.response.DataResponse;
import com.doo.aqqle.dto.TextEmbeddingDTO;
import com.doo.aqqle.model.CommonResult;
import com.doo.aqqle.model.request.LocationRequest;
import com.doo.aqqle.model.request.ShopRequest;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ResponseService responseService;
    private final ShopSearchQuery query;
    private final LocationSearchQuery locaton;
    private final DataResponse response;

    public CommonResult getProducts(ShopRequest request) {

        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            String[] includeFields = new String[]{};
            String[] excludeFields = new String[]{"feature_vector"};
            searchSourceBuilder.fetchSource(includeFields, excludeFields);

            FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = null;

            Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "1", new HashMap<>());
            if ("Y".equals(request.getSimilarity())) {
                Vector<Double> vectors = TextEmbedding.getVector(
                        TextEmbeddingDTO.builder()
                                .tensorApiUrl(HostUrl.EMBEDDING.getUrl())
                                .keyword(request.getSearchWord()).build()
                );
                Map<String, Object> map = new HashMap<>();
                map.put("query_vector", vectors);
                script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "cosineSimilarity(params.query_vector, 'feature_vector') + 1.0", map);
            }

            filterFunctionBuilders = query.getShopFunctionScoreQueryBuilder(script);

            FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(
                    query.getShopQueryBuilder(request.getSearchWord()),
                    filterFunctionBuilders
            );

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setDistance("5");
            locationRequest.setCountryCode("KR");
            locationRequest.setSize(10);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("LOCATION", response.dataResponse(locaton.getDistanceBuilder(locationRequest), locationRequest));
            resultMap.put("ITEM", response.shopDataResponse(functionScoreQueryBuilder, request));

            return responseService.getSingleResult(resultMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseService.getFailResult();
    }

}
