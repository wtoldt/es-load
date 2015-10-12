Elasticsearch Load
====

A Spring Boot application to parse Global Historic Climatology Network Daily (GHCND) data into json format and index into Elasticsearch.

What is GHCND?
----
[GHCND](https://www.ncdc.noaa.gov/oa/climate/ghcn-daily/) is the most popular data set offered at the [National Centers for Environmental Information (NCEI)](http://www.ncdc.noaa.gov). I chose this data set because it has useful elements (weather observations like temperature and rain) and a simple frequency (one observation per day) to work with.
Refer to the [GHCN readme](http://www1.ncdc.noaa.gov/pub/data/ghcn/daily/readme.txt) for more details on exactly how the GHCN data works.
**TL;DR:** ghcnd-stations.txt has the list of stations in the GHCN, each station has an id (ex: USC00045352), name, location, elevation, and other stuff. Each station will have a dly file with all the data for that station (ex: USC00045352.dly).

Usage
----
+ Make sure to [download Elasticsearch](https://www.elastic.co/products/elasticsearch) and run it.
+ Import project into Spring Tool Suite
+ Maven > Update project
+ Run as Spring Boot application

Included with the project is a small subset (15 stations) of the GHCND data set. The default configuration will run GHCND stations loader, then GHCND data loader, the order is important. GHCND stations loader will create and index station objects for each station in test-data/stations.txt. GHCND data loader will get all stations from Elasticsearch and parse each station file. 

Planned features
----
+ Make GHCND data load multi-threaded.
+ Currently a data day observation is one observation for one element for one day. I want to refactor so a data day is one observation for all the elements observed by the station for one day.
+ Could potentially support more data sets.
