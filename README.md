# Aqqle
[![Elastic Stack version](https://img.shields.io/badge/Elasticsearch-8.8.1-00bfb3?style=flat&logo=elastic-stack)]()
[![Elastic Stack version](https://img.shields.io/badge/kibana-8.8.1-00bfb3?style=flat&logo=elastic-stack)]()
[![Elastic Stack version](https://img.shields.io/badge/logstash-8.8.1-00bfb3?style=flat&logo=elastic-stack)]()
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Faqqle%2Faqqle&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
## What is Aqqle?
텍스트기반의 포털사이트 개인프로젝트 
[아빠는개발자](https://father-lys.tistory.com/category/Aqqle)의 블로그의 내용을 구현  
([개발잡부 900gle](https://ldh-6019.tistory.com/)의 es7 버전에서 es8 로 신규개발)  

### Development Stack

```yaml
OS: macOS
Languages:
  - Java: "18"
  - Python: "3.7.9"
Frameworks & Libraries:
  - TensorFlow: "2.14"
  - OpenCV: "4.5.0"
Databases:
  - MySQL
  - Redis
Message Brokers:
  - Kafka
Search & Logging:
  - Elasticsearch: "8.8.1"
  - Kibana: "8.8.1"
  - Logstash: "8.8.1"
Virtualization & Env Management:
  - Anaconda
  - Docker Compose
```

---

## - Architecture - [phase.1](https://father-lys.tistory.com/20)
[![phase1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F2jv52%2FbtsFRCYBS54%2FVihtdDGxZt889IlKghaUo0%2Fimg.png)](https://father-lys.tistory.com/53)

## - Project directory structure

> docker
> > elastic
>> -  elasticsearch
>> - kibana
>> - logstash
>
> >kafka
> 
> >redis
> 
> >mysql  

> application
> > aqqle
> > - api ( 검색용 API )
> > - common ( 공통파일 )
> > - consumer ( kafka consumer )
> > - crawler ( 데이터 크롤러 )
> > - indexer ( 색인배치 )
> > - extract ( DB to json file 배치 )
> > - libs ( OpenCV library )
> > - manage ( Admin API )
> > - producer ( kafka producer )
> > - web ( Aqqle web site )
>


> third_party
> >tf-embeddings
>>>api ( Tensorflow text embedding API )


## - Project discription

1. docker : Elastic stack 관련 DockerFile 및 플러그인 파일
* docker
    * elastic
        * [elasticsearch](https://ldh-6019.tistory.com/category/ElasticStack/Elasticsearch)
        * extensions
        * [kibana](https://ldh-6019.tistory.com/category/ElasticStack/Kibana)
        * logstash
    * [kafka](https://ldh-6019.tistory.com/category/Kafka)
    * [Redis](https://ldh-6019.tistory.com/category/Kafka)
    * [MySql](https://father-lys.tistory.com/70)

#### Elasticsearch pluin
>analysis-nori - nori 한글형태소분석 플러그인   
doo-plugin-8.8.1.zip - aqqle search plugin   
kr-danalyzer-8.8.1.zip - aqqle token filter    
payload-score-0.1.zip - payload score plugin
#### Elasticsearch dictionary
>{ES_HOME}/config/stopFilter.txt - 불용어 사전  
{ES_HOME}/config/synonymsFilter.txt - 동의어 사전  
{ES_HOME}/config/user_dictionary.txt - 사용자정의 사전


Usage
 ```
# docker-compose.yml 파일이 위한 경로로 이동 
$ cd ~/aqqle/docker/elastic    

# elasticsearch, kibana 빌드/실행   
$ docker compose up -d --build

# 실행중인 container 확인
$ dodcker ps -a
 
# 컨네이너 로그확인    
$docker logs <CONTAINER ID or NAME>

# 컨네이너 접속
$docker exec -id <NAME> /bin/bash

#container 정지/삭제  
$ docker compose stop
$ docker compose down

#image 확인/삭제
$ docker images
$ docker rmi <IMAGE_ID>
``` 

kafka Usage
```shell
docker compose -f kafka-full.yml up -d --build
```


1. java : aqqle 프로젝트
* java
    * aqqle : Project root
        * [api - 검색 API](https://father-lys.tistory.com/category/Java/API)
        * common - 공통파일
        * [crawler - 웹사이트의 정보를 크롤링]
          * [crawler - 쇼핑데이터](https://father-lys.tistory.com/22)
          * [crawler - 뉴스데이터](https://father-lys.tistory.com/22)
        * [extract - 크롤링 후 데이터 파일 생성](https://father-lys.tistory.com/category/Aqqle/EXTRACT)
        * [indexer - DB의 내용을 ES에 색인](https://father-lys.tistory.com/category/Aqqle/INDEXER)
        * [libs - OpenCV lib 파일](https://ldh-6019.tistory.com/category/OpneCV)
        * [manage - Admin 에서 사용될 API (크롤링키워드관리)](https://father-lys.tistory.com/category/Aqqle/MANAGE)
        * [producer - Kafka producer (카프카 데이터 전송)](https://ldh-6019.tistory.com/category/aqqle%20shopping/producer)
        * [consumer - Kafka consumer (카프카 데이터 소비)](https://ldh-6019.tistory.com/category/aqqle%20shopping/consumer)
        * [web - aqqle 웹사이트](https://ldh-6019.tistory.com/category/aqqle%20shopping/web)


1. third_party : tensorflow Text-embedding 모델을 사용하기 위한 API
   * third_party
     * tf-embedding
       * [app - Text embedding API](https://father-lys.tistory.com/category/Python/Text%20embeddings)

Usage
```
#aqqle 가상환경 실행
$ conda activate aqqle

#text embedding api 실행
$ python app/api.py
 ``` 
--- 

### API architecture
[![phase1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbassxZ%2FbtsMB1dGamS%2FogvFCuc8mrcAIlvCKiVN10%2Fimg.png)](https://father-lys.tistory.com/160)

 
### API cache strategy
[![phase1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbtPUnZ%2FbtszlGfAoQk%2Fdz18kEBgpeAkTOOQiKHFS1%2Fimg.png)](https://father-lys.tistory.com/55)

---
### Environment Preparation
<details>
  <summary>presetup</summary>

* [docker 설치](https://ldh-6019.tistory.com/10)
* [docker MySql설치](https://father-lys.tistory.com/70)
* [anaconda 설치](https://father-lys.tistory.com/136)
* [anaconda tensorflow 설치](https://ldh-6019.tistory.com/118?category=1043090)
* [docker redis 설치](https://father-lys.tistory.com/41)

</details> 