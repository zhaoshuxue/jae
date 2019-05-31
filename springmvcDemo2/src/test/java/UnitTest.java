import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zsx.service.TestService;
import com.zsx.utils.EsClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/resources/applicationContext.xml",
        "file:src/main/resources/springMVC-servlet.xml"})
//		"file:src/main/resources/spring-hibernate.xml",
public class UnitTest {

    @Autowired
    TestService testService;

    @Test
    public void test1() throws Exception {
        Client client = null;
        try {
            client = EsClientFactory.getClient();
            System.out.println(client);

//            String index = ConfigUtil.getAttribute("es_person_index");
//
//            existIndex(client, index);

            createIndex(client);

            existIndex(client, "zhao");


//            XContentBuilder builder = jsonBuilder().startObject()
//                    .field("", "")
//                    .field("", "")
//                    .field("", "").endObject();

//            IndicesExistsRequest inExistsRequest = new IndicesExistsRequest("twitter");
//            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
//            System.out.println(indicesExistsResponse.isExists());

//                删除索引 , 不存在会报错，所以要先判断是否存在
//            DeleteIndexResponse twitter = client.admin().indices().prepareDelete("twitter").execute().actionGet();
//            System.out.println(twitter.isAcknowledged());


//            IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
//                    .setSource(jsonBuilder()
//                            .startObject()
//                            .field("user", "kimchy")
//                            .field("postDate", new Date())
//                            .field("message", "trying out Elasticsearch")
//                            .endObject()
//                    )
//                    .get();

//            System.out.println(response.getIndex());
//            System.out.println(response.getType());
//            System.out.println(response.getId());
//            System.out.println(response.getVersion());

            JSONArray array = new JSONArray();
            for (int i = 1; i < 5; i++) {
                JSONObject o = new JSONObject();
                o.put("id", new Long(new Random().nextInt(10000000)));
                o.put("name", UUID.randomUUID().toString());
                o.put("date", new Date());

                array.add(o);
            }
//            addData(client, "zhao", "person", "", array);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", new Long(3702733L));
            jsonObject.put("name", "更新后的数据");
            jsonObject.put("date", new Date());
//            updateData(client, "zhao", "person", "id", jsonObject);

//            deleteData(client, "zhao", "person", "AV-fsPbHXpxY9F10l0QP");


//            getDataById(client, "zhao", "person", "AV-fsPbHXpxY9F10l0QS");
//            getDataById(client, "zhao", "person", "2");

//            queryData(client, "zhao", "person", "2");


        } catch (Exception e) {
            // TODO: 2017/11/9
            e.printStackTrace();
        } finally {
            EsClientFactory.closeClient(client);
        }


    }

    private void createIndex(Client client) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", new Long(1L))
                .field("name", "123123")
                .field("date", new Date())
                .endObject();

        IndexResponse response = client.prepareIndex("zhao", "person", "2")
                .setSource(builder).get();

        System.out.println(response.getIndex());
        System.out.println(response.getType());
        System.out.println(response.getId());
        System.out.println(response.getVersion());

    }

    private void existIndex(Client client, String index) {
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        System.out.print("索引： " + index + "  是否存在:");
        System.out.println(indicesExistsResponse.isExists());
    }

    private void addData(Client client, String index, String type, String idName, JSONArray jsonArray) {
        BulkRequestBuilder builder = client.prepareBulk();
        for (Object o : jsonArray) {
            JSONObject json = JSONObject.parseObject(JSON.toJSONString(o));
            //没有指定idName 那就让Elasticsearch自动生成
            if (StringUtils.isBlank(idName)) {
                IndexRequestBuilder lrb = client
                        .prepareIndex(index, type)
                        .setSource(json);
                builder.add(lrb);
            } else {
                String idValue = json.getString(idName);
                IndexRequestBuilder lrb = client
                        .prepareIndex(index, type, idValue)
                        .setSource(json);
                builder.add(lrb);
            }
        }
        BulkResponse response = builder.execute().actionGet();
        if (response.hasFailures()) {
            System.out.println(response.getItems().toString());
            System.out.println("添加数据失败");
        } else {
            System.out.println("添加数据成功");
        }
    }

    private void updateData(Client client, String index, String type, String idName, JSONObject json) {
        BulkRequestBuilder builder = client.prepareBulk();
        String idValue = json.getString(idName);
        IndexRequestBuilder lrb = client
                .prepareIndex(index, type, idValue)
                .setSource(json);
        builder.add(lrb);
        BulkResponse response = builder.execute().actionGet();
        if (response.hasFailures()) {
            System.out.println(response.getItems().toString());
            System.out.println("更新数据失败");
        } else {
            System.out.println("更新数据成功");
        }
    }

    private void deleteData(Client client, String index, String type, String id) {
        DeleteResponse response = client.prepareDelete()
                .setIndex(index).setType(type)
                .setId(id)
                .execute().actionGet();

        System.out.println(response.status());
//        if (response.isFound()) {
//            System.out.println("删除成功");
//        } else {
//            System.out.println("删除失败");
//        }
    }

    private void getDataById(Client client, String index, String type, String id) {
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse response = client.get(getRequest).actionGet();
        if (response.isSourceEmpty()){
            System.out.println("查询结果为空");
        }else{
            System.out.println("有查询结果");
            Map<String, Object> map = response.getSource();
            System.out.println(JSON.toJSONString(map));
        }
    }

    private void queryData(Client client, String index, String type, String name){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(new QueryStringQueryBuilder("数据").field("name").autoGeneratePhraseQueries(true));
        queryBuilder.must(new QueryStringQueryBuilder(String.format("*%s*", "123")).field("name").autoGeneratePhraseQueries(true));
//        queryBuilder.must(new QueryStringQueryBuilder("数").field("name"));
//        queryBuilder.must(QueryBuilders.matchQuery("name", "数"));
//        queryBuilder.should(QueryBuilders.fuzzyQuery("name", "数"));
//        queryBuilder.should(QueryBuilders.matchPhraseQuery("name", "数"));
//        queryBuilder.must(QueryBuilders.rangeQuery("id").gt(1).lt(9999999)); // ok

//        System.out.println(queryBuilder.hasClauses()); // 需要先判断是否有查询

        SearchRequestBuilder builder = client.prepareSearch(index).setTypes(type)
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.matchQuery("name", "123123")) //
//                .setQuery(QueryBuilders.termQuery("name", "052278d9")) //
                .setQuery(queryBuilder) //

//                .setPostFilter(FilterBuilders.termFilter("name", "123123")) // 可以
//                .setPostFilter(FilterBuilders.termFilter("name", "3")) // 不行
                .setFrom(0)
                .setSize(5)
                .addSort("name", SortOrder.ASC) // 排序
                .addSort(SortBuilders.fieldSort("date").order(SortOrder.DESC))
                .setExplain(true); // 设置是否按查询匹配度排序

        SearchResponse response = builder.execute().actionGet();
        SearchHits hits = response.getHits();
        System.out.println("查询结果总数为： ");
        System.out.println(hits.getTotalHits());
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSource();
            System.out.println(JSON.toJSONString(source));
        }



    }

        @Test
    public void name() {

        String nihao = testService.getName("a1");
        System.out.println(nihao);


    }

}
