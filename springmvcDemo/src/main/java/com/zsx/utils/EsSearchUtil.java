package com.zsx.utils;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by QDHL on 2017/6/6.
 */
public class EsSearchUtil {

    private static final Logger log = LoggerFactory.getLogger(EsSearchUtil.class);
    //if you don't set size, elasticsearch will set default size=10, so we should set customized default size
    private static final int DEFAULT_SIZE = 1000;

    /**
     * 分页筛选并排序
     *
     * @param filterBuilder
     * @param page
     * @param sortBuilders
     * @param indexTypeName
     * @return
     * @throws Exception
     */
    public static SearchHits searchHitsAndOrder(FilterBuilder filterBuilder, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, Paging page, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            SearchRequestBuilder searchReqBuilder = client.prepareSearch(indexName).setTypes(indexTypeName);
            setQueryParam(searchReqBuilder, filterBuilder, queryBuilder);

            if (sortBuilders != null && sortBuilders.size() > 0) {
                for (SortBuilder sortBuilder : sortBuilders) {
                    searchReqBuilder.addSort(sortBuilder);
                }
            }
            if (page != null) {
                int perPage = getDefCountPerPage(page.getPageSize());
                searchReqBuilder.setFrom((page.getPage() - 1) * page.getPageSize()).setSize(perPage);
            } else {
                searchReqBuilder.setSize(DEFAULT_SIZE);
            }
            SearchHits searchHits = searchReqBuilder.execute().actionGet().getHits();
            int totalHitCount = (int) searchHits.getTotalHits();
            if (page == null && totalHitCount > DEFAULT_SIZE) {
                searchReqBuilder.setSize(totalHitCount);
                searchHits = searchReqBuilder.execute().actionGet().getHits();
            }
            return searchHits;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            EsClientFactory.closeClient(client);
        }
    }

    private static void setQueryParam(SearchRequestBuilder searchRequestBuilder, FilterBuilder filterBuilder, QueryBuilder queryBuilder) {
        if (filterBuilder != null) {
            boolean add = true;
            if (filterBuilder instanceof BoolFilterBuilder) {
                BoolFilterBuilder boolFilterBuilder = (BoolFilterBuilder) filterBuilder;
                add = boolFilterBuilder.hasClauses();
            }
            if (add) {
                searchRequestBuilder.setPostFilter(filterBuilder);
            }
        }
        if (queryBuilder != null) {
            boolean add = true;
            if (queryBuilder instanceof BoolQueryBuilder) {
                BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder) queryBuilder;
                add = boolQueryBuilder.hasClauses();
            }
            if (add) {
                searchRequestBuilder.setQuery(queryBuilder);
            }
        }
    }

    /**
     * 获取桶聚合
     *
     * @param termsBuilder
     * @param indexName
     * @param indexTypeName
     * @return
     * @throws Exception
     */
    public static Aggregations searchAggregations(QueryBuilder queryBuilder, TermsBuilder termsBuilder, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            SearchRequestBuilder searchReqBuilder = client.prepareSearch(indexName).setTypes(indexTypeName);
            if (queryBuilder != null) {
                searchReqBuilder.setQuery(queryBuilder);
            }
            if (termsBuilder != null) {
                searchReqBuilder.addAggregation(termsBuilder);
            }
            Aggregations aggs = searchReqBuilder.execute().actionGet().getAggregations();
            return aggs;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            EsClientFactory.closeClient(client);
        }
    }

    public static boolean upsertRecord(String keyColumn, String keyValue, XContentBuilder xContentBuilder, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            IndexRequest indexRequest = new IndexRequest(indexName, indexTypeName, keyValue)
                    .source(xContentBuilder);
            UpdateRequest updateRequest = new UpdateRequest(indexName, indexTypeName, keyValue)
                    .doc(xContentBuilder)
                    .upsert(indexRequest);
            client.update(updateRequest).get();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            EsClientFactory.closeClient(client);
            xContentBuilder.close();
        }
    }


    /**
     * 查询总数
     *
     * @param filterBuilder
     * @param indexTypeName
     * @return
     * @throws Exception
     */
    public static Long searchCount(FilterBuilder filterBuilder, QueryBuilder queryBuilder, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        long count = 0l;
        try {
            client = EsClientFactory.getClient();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(indexTypeName);
            setQueryParam(searchRequestBuilder, filterBuilder, queryBuilder);
            count = searchRequestBuilder.execute().actionGet().getHits().getTotalHits();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            count = -1l;
        } finally {
            EsClientFactory.closeClient(client);
            return count;
        }
    }

    public static void deleteIndex(String indexName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(indexName).execute().actionGet();
            log.info("删除索引：{},{}", indexName, deleteIndexResponse.isAcknowledged());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            EsClientFactory.closeClient(client);
        }
    }

    /**
     * 获取一个文档
     *
     * @param keyColumn
     * @param keyValue
     * @param indexName
     * @param indexTypeName
     * @return
     * @throws Exception
     */
    public static SearchHit searchById(String keyColumn, String keyValue, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            SearchHits searchHits = client.prepareSearch(indexName).setTypes(indexTypeName)
                    .setPostFilter(FilterBuilders.termFilter(keyColumn, keyValue))
                    .execute()
                    .actionGet().getHits();
            return searchHits.getAt(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            EsClientFactory.closeClient(client);
        }

    }


    /**
     * 删除文档
     *
     * @param id
     * @param indexTypeName
     * @throws Exception
     */
    public static boolean deleteIndexById(String id, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            DeleteResponse response = client.prepareDelete(indexName, indexTypeName, id)
                    .execute()
                    .actionGet();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            EsClientFactory.closeClient(client);
        }
    }

    /**
     * 条件删除文档
     *
     * @param queryBuilder
     * @param indexTypeName
     * @throws Exception
     */
    public static boolean deleteIndexByQuery(QueryBuilder queryBuilder, String indexName, String indexTypeName) throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            DeleteByQueryResponse response = client.prepareDeleteByQuery(indexName).setTypes(indexTypeName).setQuery(queryBuilder)
                    .execute()
                    .actionGet();
            return true;
        } catch (Exception e) {
            log.error("delete documents by condition error", e);
            return false;
        } finally {
            EsClientFactory.closeClient(client);
        }
    }

    public static void execute(XContentBuilder contentBuilder, String indexName, String indexTypeName) throws Exception {
        PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(indexTypeName).source(contentBuilder);
        Client client = EsClientFactory.getClient();
        try {
            PutMappingResponse mappingResponse = client.admin().indices().putMapping(mapping).actionGet();
        } finally {
            client.close();
        }
    }

    public static void updateSettings(Settings settings, String indexName) throws Exception {
        UpdateSettingsRequest request = new UpdateSettingsRequest(settings, indexName);
        Client client = EsClientFactory.getClient();
        try {
            client.admin().indices().updateSettings(request).actionGet();
        } finally {
            client.close();
        }
    }

    public static void initIndex(String indexName) throws Exception {
        EsClientFactory.getClient().admin().indices().prepareCreate(indexName).execute().actionGet();
    }


    public static Boolean existIndex(String indexName) throws Exception {
        IndicesExistsResponse existsResponse = EsClientFactory.getClient().admin().indices().prepareExists(indexName).execute().actionGet();
        return existsResponse.isExists();
    }

    public static Boolean existType(String indexName, String indexTypeName) throws Exception {
        TypesExistsResponse existsResponse = EsClientFactory.getClient().admin().indices().prepareTypesExists(indexName).setTypes(indexTypeName).execute().actionGet();
        return existsResponse.isExists();
    }

    private static int getDefCountPerPage(int size) {
//        int maxNum = Integer.parseInt(ConfigUtil.getProp("return_max_num"));
        int maxNum = 50;
        if (size <= 0 || size > maxNum) {
            return maxNum;
        }
        return size;
    }
}
