复合查询

- 

_search  get方法

- post方式

index/type
_search

{
  "from": 1,
  "size": 131,
  "query": {
    "match_all": {}
  }
}


## 复合查询

http://192.168.32.115:9200/索引名/type
_search    POST方式

```
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "organ_id": "AA16032813533600019"
          }
        },
        {
          "term": {
            "room_sign": "f512"
          }
        }
      ]
    }
  }
}
```

## 删除 根据_id

http://192.168.32.115:9200/索引名/type/_id
    DELETE方式

```
{}
```
