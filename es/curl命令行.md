## 查询

```

curl -XPOST 'http://localhost:9200/indexName/_search?pretty' -d '
{
  "query": { "match_all": {} },
  "size": 10
}'

```


```
curl -XPOST 'http://localhost:9200/qdp/room/_search?pretty' -d '
{
  "query": {
        "bool": {
            "must": [
                {
                    "term": {
                        "person_id": "idididid"
                    }
                },
                {
                    "term": {
                        "person_name": "赵钱孙"
                    }
                }
            ]
        }
  },
  "size": 10
}'
```


## 根据_id查询

```

curl -XGET 'http://localhost:9200/qdp/room/AWM6GLd-6WKvodMZygD-?pretty' -d '
{}'

```


## 根据_id修改

```

curl -XPUT 'http://localhost:9200/qdp/room/AWM6GLpH6WKvodMZygE4' -d '
{
	"person_id": "333",
	"room_id": "444",
	"person_name": "暗示法",
	"is_history": "0"
}'

```

## 删除，根据特定的ID删除文档。

```
$ curl -XDELETE 'http://localhost:9200/twitter/tweet/1'
```

- 删除索引（谨慎操作）

```
curl -XDELETE 'http://localhost:9200/index-test'
```





















































---
