package com.doo.aqqle.service;

import com.doo.aqqle.annotation.IndexerLog;
import com.doo.aqqle.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexerService {
    private final RestHighLevelClient client;

    private final GoodsRepository goodsRepository;

    private final AsyncTaskService asyncTaskService;

    private List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();

    @IndexerLog
    public void index(String type) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
        String indexName = "products-" + LocalDateTime.now().format(dateTimeFormatter).toString();

        try {
//            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
//            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

            String setting = getResourceData("setting.json");
            String mapping = getResourceData("mapping.json");

            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(setting, XContentType.JSON);
            request.mapping(mapping, XContentType.JSON);

            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

            String path = "/data/static/";
            File file = new File(path);

            String dir = Arrays.stream(file.list()).sorted().findFirst().get();

            File files = new File(path + dir);
            List<String> fileList = Arrays.stream(files.list()).collect(Collectors.toList());

            for (int i = 0; i < fileList.size(); i++) {
                CompletableFuture<Integer> completableFuture = asyncTaskService.task(indexName, path + dir + "/" + fileList.get(i));
                completableFutures.add(completableFuture);
            }

            for (CompletableFuture<Integer> future : completableFutures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            FlushRequest flushRequest = new FlushRequest(indexName);
            FlushResponse flushResponse = client.indices().flush(flushRequest, RequestOptions.DEFAULT);
            ForceMergeRequest forceMergeRequest = new ForceMergeRequest(indexName);
            ForceMergeResponse forceMergeResponse = client.indices().forcemerge(forceMergeRequest, RequestOptions.DEFAULT);
            IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();

            IndicesAliasesRequest.AliasActions aliasActionsAdd = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                    .index(indexName)
                    .alias("shop");

            indicesAliasesRequest.addAliasAction(aliasActionsAdd);

            AcknowledgedResponse indicesAliasesResponse =
                    client.indices().updateAliases(indicesAliasesRequest, RequestOptions.DEFAULT);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String getResourceData(String name) throws IOException {
        Resource resource = new ClassPathResource(name);
        InputStream inputStream = resource.getInputStream();
        byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
        return new String(byteData, StandardCharsets.UTF_8);
    }
}
