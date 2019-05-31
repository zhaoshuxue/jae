## 

```
source /etc/profile
```


---

```
docker run --name elk2 -t -i -p 59102:9100 -p 59202:9200 -p 59302:9300 elk:1.1 /bin/bash


docker run --name elk3 -t -i -p 59103:9100 -p 59203:9200 -p 59303:9300 elk:1.1 /bin/bash
```


## 还是网站下载吧



https://nodejs.org/en/download/
下载后是tar.xz 格式文件，用7z解压成tar



```
tar -xvf node-v8.9.0-linux-x64.tar
```

注意没有z

（tar -xvf node-v8.9.0-linux-x64.tar.xz）

直接把 /data/node-v8.9.0-linux-x64/bin 放到环境变量里即可



验证：

```
node -v 

npm -v
```

## 安装git

```
apt-get install git
```


## 

```
git clone https://github.com/mobz/elasticsearch-head.git
```
```
cd elasticsearch-head
npm install
npm run start
open http://localhost:9100/
```

> 如果速度较慢或者安装失败，可以使用国内镜像:

> 
```
npm install -g cnpm --registry=https://registry.npm.taobao.org
```

```
root@5c5b375a16c9:/data/elasticsearch-head# npm run start

> elasticsearch-head@0.0.0 start /data/elasticsearch-head
> grunt server

sh: 1: grunt: not found
npm ERR! file sh
npm ERR! code ELIFECYCLE
npm ERR! errno ENOENT
npm ERR! syscall spawn
npm ERR! elasticsearch-head@0.0.0 start: `grunt server`
npm ERR! spawn ENOENT
npm ERR! 
npm ERR! Failed at the elasticsearch-head@0.0.0 start script.
npm ERR! This is probably not a problem with npm. There is likely additional logging output above.

npm ERR! A complete log of this run can be found in:
npm ERR!     /root/.npm/_logs/2017-11-08T04_07_56_228Z-debug.log

```

## 需要安装grunt

- 安装grunt-cli
```
npm install -g grunt-cli
```
- 安装grunt
```
npm install grunt --save-dev
```

- 根据提示安装grunt插件
```
npm install grunt-contrib-clean --save-dev
```

```
root@5c5b375a16c9:/data/elasticsearch-head# npm run start

> elasticsearch-head@0.0.0 start /data/elasticsearch-head
> grunt server

>> Local Npm module "grunt-contrib-clean" not found. Is it installed?
>> Local Npm module "grunt-contrib-concat" not found. Is it installed?
>> Local Npm module "grunt-contrib-watch" not found. Is it installed?
>> Local Npm module "grunt-contrib-connect" not found. Is it installed?
>> Local Npm module "grunt-contrib-copy" not found. Is it installed?
>> Local Npm module "grunt-contrib-jasmine" not found. Is it installed?
```

# 最后直接把需要安装的命令写出来吧

```

npm install grunt --save-dev
npm install -g grunt-cli
npm install grunt-contrib-clean --save-dev
npm install grunt-contrib-concat --save-dev
npm install grunt-contrib-watch --save-dev
npm install grunt-contrib-connect --save-dev
npm install grunt-contrib-copy --save-dev
npm install grunt-contrib-jasmine --save-dev

```


## 安装es

下载https://www.elastic.co/downloads/elasticsearch


## 修改Elasticsearch配置文件

编辑elasticsearch-5.1.1/config/elasticsearch.yml,加入以下内容：

```
network.host: 0.0.0.0
```

```
http.cors.enabled: true
http.cors.allow-origin: "*"
```


## 修改Gruntfile.js

打开elasticsearch-head-master/Gruntfile.js，找到下面connect属性，新增hostname: ‘0.0.0.0’:

```
connect: {
        server: {
            options: {
                hostname: '0.0.0.0',
                port: 9100,
                base: '.',
                keepalive: true
            }
        }
    }   

```



## 启动
```
bin/elasticsearch
```
会提示

can not run elasticsearch as root

```
因为安全问题elasticsearch 不让用root用户直接运行，所以要创建新用户
第一步：liunx创建新用户  adduser zhao    然后给创建的用户加密码 passwd zhao    输入两次密码。
第二步：切换刚才创建的用户 su zhao  然后执行elasticsearch  会显示Permission denied 权限不足。
第三步：给新建的zhao赋权限，chmod 777 *  这个不行，因为这个用户本身就没有权限，肯定自己不能给自己付权限。所以要用root用户登录付权限。
第四步：root给zhao赋权限，chown -R zhao /你的elasticsearch安装目录。
然后执行成功。
```


## 报错：
ERROR: bootstrap checks failed
system call filters failed to install; check the logs and fix your configuration or disable system call filters at your own risk



```
原因：
这是在因为Centos6不支持SecComp，而ES5.2.0默认bootstrap.system_call_filter为true进行检测，所以导致检测失败，失败后直接导致ES不能启动。

解决：
在elasticsearch.yml中配置bootstrap.system_call_filter为false，注意要在Memory下面:
bootstrap.memory_lock: false
bootstrap.system_call_filter: false

可以查看issues
https://github.com/elastic/elasticsearch/issues/22899


```


## ERROR: bootstrap checks failed
max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]

```
临时设置：sudo sysctl -w vm.max_map_count=262144
永久修改：
修改/etc/sysctl.conf 文件，添加 “vm.max_map_count”设置
并执行：sysctl -p
```







