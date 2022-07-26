# kafka-demo
Demo of how to create Producers and consumers in Kafka


## What is the problem that solves Kafka?

Kafka solves the problem of create integrations between the systems, with Kafka we can send and receive messages (events, transactions, user interactions, etc.) y send it to data bases, analitics, email, audit, etc. 


## Why kafka
Kafka was created by LinkedIn, and it was created as an open source project. Now it's mainly maintained by big corporationssuch as Confluent, IBM, Cloudera, LinkedIn, and so on.

It's distributed, has a resilient architecture, and is fault tolerant. You can do Kafka maintenance without taking the whole system down.

Kafka has horizontal scalability, this means you can scale to 100s of brokers. Kafka can scale to millions of messages per second. THis is the case of Twitter.

Kafka is really high performance the latency is somethime measured in less than 10 milliseconds, this is why we call Kafka a real time system.


## Kafka: Use cases

How is Kafka used?, well is used as:
- Message System
- Activity Tracking system
- To Gather metrics from many different locations
- To gather application logs
- Stream processing (with Kafka Streams API)
- De-coupling of system dependencies
- Integration with Spark, Flink, Storm, Hadoopm and many other Big Data Technologies

A big example is Netflix that use the recomendation in real time to recommend tv shows.

Kafka Is only a Transport Mechanism, wich allows huge data flows in your company.


## Kafka Topics

the topics are a particular stream of data within your Kafka cluster. A Kafka cluster can hace many topics. THis topicas can be named for example logs, purchases, twitter_tweets, trucks_gps, and so on.

A topic can be similar to a table in the database, but without all the constrains, because you can send whatever you want to Kafka topic, there is no data verification.

You can have many topics as you want in you Kafka cluster, the topics are identified by its *name*  

### What is a broker?

In Kafka a broker means a Kafka cluster that consist in one o more servers denominates as Kafka brokers. Each broker are identified by an ID (integer) and contains certains partitions of a topic, no necessary all.
