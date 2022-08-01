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

The topics are a particular stream of data within your Kafka cluster. A Kafka cluster can hace many topics. This topicas can be named for example logs, purchases, twitter_tweets, trucks_gps, and so on.

A topic can be similar to a table in the database, but without all the constraints, because you can send whatever you want to Kafka topic, there is no data verification.

You can have many topics as you want in you Kafka cluster, the topics are identified by its **name**. This **Kafka topics** support any kind of message formats: JSON, Avro, text file, binary, etc.

The sequence of the messages in a topic is called data streams. This is why Kafka is called a **data streaming platform**, because you make data stream through the topics.

We mention that the topics are similar like a table, but you cannot query topics, instead, Kafka uses:
- *Kafka Producers* to send data and
- *Kafka Consumers* to read the data form a topics.

The Kafka topics are immutables, this means that once the data is written into a partition, it cannot be changed (deleted, updated).

In a Kafka topic we can have many partitions as wwe want.

### Partitions and Offsets

Topics are split in partitions, for example 100 partitions. this partitions begins with 0. The messageswithin each partition are ordered, so my first message will be in the partition 0 and they will have the id 0, if other message comes will have the id 1 and so on for the new messages.

This is the same case when we gp and write data into the partition 1 and so on.

When a message are written in the partitions get an ID incremental starting from 0. These IDs are called **Offsets**.

Once the data is written to a partition, it cannot be changed.
Data is kept only for a limited time (default is one week - this can be configurable).

The offsets only have a meaning for a specific partition. 
- E.g. the offset 3 in partition 0 doesn't represent the same data as offset in partition 1.
- Offsets are not re-used even if previous messages have been deleted.

The order of the messages is guaranteed only within a partition (not a cross partitions).

The Data is assigned randomly to a partition unless a key is provided.

## Producer

Producers write data to topics (remember the topics ara made of partitions), to send data to Kafka we need the Producers. The Producers know in advance in which partition the message is going to be written (and which kafka broker has it), not in the end. 

In the case Kafka server that has a partition failure, the Producers know how to automatically recover. 

The producers have load balancing, because the producers are ging to send data across all the partitions based on some mechanism, and this is why Kafka scales, it's because we have many partitions within a topic and each partition is going to receive messages from one or more producers.

### Producer: Message keys

The Producers have message keys in the message. The Producers can choose to send a key with the message (string, number, binary, etc.) this key it's optional. You have two cases:
- **If the key is null** then the data is going to send round-robin. 
  - This means that this will be sent to partition =, partition 1, and so on, and this is how we get load balancing.
  - Key equals to null means that the key was not provided in the producer message.
- **If the key is not null** then all messages for that key will always go to the same partition (hashing).
  - This means that the key has some value, it could be again string, number, binary, etc.

Kafka Producers have a very important property in Kafka, that is that all messages that share the same key will always end up written to the same partition, thanks to the **Hashing strategy**.

A key are tipically sent if you need message ordering for a specific field (ex:truck_id)

### Producer: Kafka Messages anatomy

The Kafka messages when are created by the producers has the next elements
- Key : can be null or with a format
- Value: that contains the message content, it can be null as well the key, but usually is not null.
- Compression Type: Then we can add compression mechanism onto our messages. So do we want them to be smaller? If so, we can specify a compression mechanism, for example gzip, snappy, lz4 or zstd.
- Headers: The message headers can be optional, which are a list of key value pairs.
- Partition + Offsets: this is the partition where the message will be sent as well as its Offsets.
- TimeStamp: it can be set by the system or the user.

Once the producer has the message body, is sent to Kafka for storage.

### Producer: Kafka Messages Serializer

How the messages are created?

Kafka have something called Kafka Message Serializer. 

- Kafka only accepts series of bytes as an input from producers, and it will be sent bytes as an output to consumers.
- Message Serialization means transforming objects / data into bytes. When we construct messages, they're not bytes.
- To serialize if Kafka uses the value and the key to serialize. After the  key and value have the binary representations the message is now ready to be sent into Kafka.
- So Kafka producers come with **common Serializers** that help you do this transformation. 
  - String (includes a JSON as representation of the String).
  - Integers. 
  - Floats. 
  - Avro. 
  - Protobuf and so on.

### Producer: Kafka Message Key Hashing

A kafka partitioner is a code logic that takes a record (message) and determines to which partition to send it into.

**Key Hashing** is the process of determining the mapping of a key to a partition. In the default kafka partitiones, the keys are hashed using the **murmur2 Algorithm**, with the formula below:
```java
targetPartition = Math.abs(Utils.murmur2(keyBaytes)) % (numPartitions -1)
```

## Consumer

The Consumers read data from a Topic that is identified by name. 

The consumers implemente the Pull model: that means that the consumers are going to request data from Kafka brokers, the servers, and they will respond back.

The consumers automatically know which broker to read from.

In the case of the broker failures, consumers know how to recover.

The data is read in order from low to high offset **within each partitions**. The consumer first is going to read data in order from the topic A partition 0 from the offset 0 to all the way to the offset that the partition has. But there is no guarantees that the partitions will be between the  partitions. The only order that we have in within the partition (offsets).

### Consumer: Deserializer

**Deserializer** indicates how to transform bytes into objects / data. So we have the key and a value that are in binary format, which corresponds to the data in your Kafka message. And then we need to transform them to read them adn put them in to an object that our programming language can use.

The Deserializer are used on the value and the key of the message. The consumer need to know in advance the format of the message to use the correct common Deserializer:
- String (includes a JSON as representation of the String).
- Integers.
- Floats.
- Avro.
- Protobuf and so on.

The Serialization / deserialization type must not change during a topic life cycle, you need to create a new Topic and producer instead.

### Consumer: Groups

All teh consumers in an application read data as a consumer groups.
Each consumer within a group read from exclusive partitions.

if you have more consumers tha partitions, some consumers will be inactive, those inactive consumers it will be inactive and will not help the other consumers and it's ok.

### Consumer: Multiple Consumers on one Topic

In Kafka is acceptable to have multiple consumer groups on the same topic. When we are going to use the consumer group we identify the group using the _**Consumer property**_ **group.id**.

### Consumer: Offsets

Kafka stores the offsets at which a consumer group has been reading.

The offsets committed are in Kafka topic named **__consumer_offsets**; the double underscore in teh beginning indicates that is a internal Kafka Topic.

When a consumer is a group has processed data retrieved from Kafka, it should be periodically committing the offsets; the Kafka broker will write to **__consumer_offsets**, not the group itself.

If a consumer dies, it will be able to read back from where it left off thanks to the committed consumer offsets!

### Consumer: Delivery Semantics

By default, java Consumers will automatically commit offsets (at least once).

There are 3 delivery semantics if you choose to commit manually:
- At least once (usually preferred):
  - Offsets are committed after the message is processed.
  - if the processing goes wrong, the message will be read again.
  - This can result in duplicate processing of messages. Make sure your processing is Idempotent; Processing your messages won't impact your systems.
- At most  once:
  - Offsets are committed as soon as messages are received
  - If the processing goes wrong, some messages will be lost (they won't be read again).
- Exactly once:
  - For Kafka to Kafka workflows: use the Transactional API (easy with Kafka Streams API)
  - For Kafka to external System workflows: use an idempotent consumer

## Kafka Brokers

A Kafka cluster is composed of multiple brokers (servers).

Each broker is identified with its ID (integer), for example will we have the Broker 101, Broker 102 and Broker 103 in our cluster.

Each broker contains certain topics partitions, that's means that your data is going to be distributed across all brokers.

After connecting to any broker (called a bootstrap broker), you will be connected to the entire cluster (Kafka clients have smart mechanics for that).

A good number to get started is 3 brokers, but some big clusters have 100 brokers.

### Kafka Brokers: Brokers and topics

Example of Topic-A with 3 partitions and Topic-B with 2 partitions. And then we have three Kafka brokers, Broker 101, 102, and then 103. So, Broker 101 is going to have the Topic-A, Partition 0, then Broker 102 is going to have the Topic-A, Partition 2, and this is not a mistake. And then Broker 103 is having Topic-A, Partition 1. As we can se, the topic partitions are ging to be spread out across all brokers in whatever order.

And then for Topic-B, then we have Topic-B, Partition 1 on Broker 101, and Topic-B, Partition 0 on Broker 102.

**NOTE:** data is distributed, and Broker 103 doesn't have any Topic B data.

### Kafka Brokers: Discovery Mechanism

Every Kafka broker is also called a **"bootstrap server"**.

That means that you only need to connect to one broker, and the Kafka clients will know how to be connected to the entire cluster (smart clients).

Each broker knows about all brokers, topics and partitions this is known as metadata.

## Topic replication factor

Topic should have a replication factor > 1 (usually between2 and 3 when are in production). 

This way if a broker is down, another broker can serve the data.

For **Example we have Topic-A, it has two partitions and a replication factor of two**. So we have three Kafka brokers, and we're going to place partition zero of Topic-A onto broker 101, partition one of Topic-A onto broker 102. So this is the initial. And then because we have a replication factor of two then we're going to have a copy of partition zero onto broker 102 with a replication mechanism, and a copy of partition one onto broker 103 with again, a replication mechanism. So as we can see here, we have four, obviously, units of data because we have two partitions, and replication factor of two. We can see that brokers are replicating data from other brokers. 

**Example we Lost a Broker 102**: What if we lose broker 102? Okay, well, as we can see, we have broker 101, and 103 still up and they can still serve the data. So partition zero and partition one are still available within our cluster and this is why we have a replication factor. So in case of replication factor of two, to make it very simple, you can lose one broker and be fine.

## Concept of Leader for a Partition

At any time only ONE broker can be a leader for a given partition.

Producers can only send data to the broker that is leader of a partition. Example broker 101 is the leader of partition zero, and broker 102 is the leader of partition one, but broker 102 is a replica of partition zero, and broker 103 is a replica of partition one.

The other brokers will replicate the data.

Therefore, each partition has one leader and multiple ISR (In-sync replica). If the data is replicated well then they are synchronized in terms of the data replication.

## Default Producer & Consumer behaviour with leaders

Kafka Producers can only write to teh leader broker for a partition.

## Kafka Consumers Replica Fetching (Kafka v2.4+)

Since Kafka 2.4, it is possible to configure consumers to read from the closest replica. Why?, becasue this may help to improve latency, and also decrease network costs if using the cloud.

##Producer Acknowledgements (acks)

The producers can choose to receive acknowledgement of data writes:
- **acks=0**: Producer won't wait for acknowledgement (possible data loss).
- **acks=1**: Producer will wait for leader acknowledgment (limited data loss)
- **acks=all**: Leader + replicas acknowledgment (no data loss)

##Kafka Topic Durability

For a topic replication factor of 3, topic data durability can withstand 2 broker loss.

As a rule, for replication Factor of N, you can permanently lose up to N-1 brokers and still recover your data.

##Zookeper

Zookeer manages brokers (keeps a list of them) and is a software.

Zookeeper helps in performing leader election for partitions.

Zookeepr send notifications to Kafka in case of changes (e.g. new Topic, broker dies, broker comes up, delete topics, etc...)

Kafka 2.X can't work without Zookeper

Kafka 3.x can work without Zookeeper (KIp-500) - using Kafka Raft instead.

Kafka 4.x will not have Zookeper anymore.

Zookeeper by desing operates with an idd number of servers (1,3,5,7), butr never more than 7 servers.

Zookeeper has a leader (writes) the rest of the servers are followers (reads)

Zookeeper does NOT store consumer offsets with Kafka > v0.10

###Zookeper: Should we use Zookkepr?

If you are managing Kafka Brokers, the answer is yes. Until Kafka 4.0 is out while waiting for Kafka without Zookeper to be production-ready.

##Kafka KRaft

in 2020, Kafka start to wor to remove Zookeeper.

And when the community will remove zookeeper, Apache Kafka will be able to scale to millions of partitions and become easier to maintain and set up. It will improve its stability. It will be easier to monitor, support and administer. And will have a single security model for the whole system. It's also going to be much easier to start Kafka and you're going to get a faster shutdown and recovery time.


#Starting Kafka With Conduktor

Locally Kafka will start and it will be accessible in the **localhost (127.0.0.1)**.

The instructions provided by Conduktor are in the next url: https://www.conduktor.io/kafka/starting-kafka

We recommend starting Kafka with [Conduktor](https://www.conduktor.io/download) if you're having issues even after troubleshooting.

You need to log in to the Conduktor to get access to Conduktor app.

Once you have your access to Conduktor app you need to start Kafka using the option:
- **+ Start local Kafka cluster**
- Introduce the **Cluster Name** for example _**Kafka Course**_
- Introduce the **Cluster Version**: **3.1.0**. Then we click the **Download button**. (At the moment that we follow the training we use the 3.1.0 version.)
- You can choose a color to easy identify you cluster.
- Finally, you must click the **START CLUSTER button**.

If you want to quit kafka you only need to close it o r exit form Conduktor.

#Setup Kafka binaries: Mac OS X

you can follow the next [instructions from Conduktor](https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-mac)

- You don't need Kafka
- you can start running Kafka CLI commands
- You need the Java Version JDK version 11
- Download Kafka from [Apache Kafka](https://kafka.apache.org/downloads) and extract in your Mac. 
- -Set up the $PATH environment variables for easy access to the Kafka binaries

You can use brew instead to do it manually.

You need to download the latest version 3.2.0 with the latest Binary downloads Scala 2.13 (in this is the lastest version).

Copy the folder of the binaries to another location, for teh tutorial we want to use the personal folder.

Setup the environment variable $PATH

###Look for the file .zshrc

(In some cases this will be a bash)

we need to look for the next path: **_kafka_2.13-3.2.0/bin/_**. The version maybe can change so be carefuk about it.

Use the next comand

```shell
nano .zshrc
```

using **nano** you will open the GNU nano editor. 

In the editor add the next path:

````shell
PATH="$PATH:~/kafka_2.13-3.2.0/bin"
````
remember that the **~** is the path where kafka is stored. then save it and open a new terminal to verify if the $PATH is correct. You should have something similar like this:

````shell
user@MacBook ~ % echo $PATH
/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/Users/user/kafka_2.13-3.2.0/bin
````

Once is verified the $PATH variable with our Kafka bins locations we proceed to execute the next command to test it.

````shell
kafka-topics.sh
````

If all is correctly set up, we will get a response similar like this:
````shell
Create, delete, describe, or change a topic.
Option                                   Description                            
------                                   -----------                            
--alter                                  Alter the number of partitions,        
                                           replica assignment, and/or           
                                           configuration for the topic.         
--at-min-isr-partitions                  if set when describing topics, only    
                                           show partitions whose isr count is   
                                           equal to the configured minimum.     
--bootstrap-server <String: server to    REQUIRED: The Kafka server to connect  
  connect to>                              to.                                  
--command-config <String: command        Property file containing configs to be 
  config property file>                    passed to Admin Client. This is used 
                                           only with --bootstrap-server option  
                                           for describing and altering broker   
                                           configs.                             
--config <String: name=value>            A topic configuration override for the 
                                           topic being created or altered. The  
                                           following is a list of valid         
                                           configurations:                      
                                         	cleanup.policy                        
                                         	compression.type                      
                                         	delete.retention.ms                   
                                         	file.delete.delay.ms                  
                                         	flush.messages                        
                                         	flush.ms                              
                                         	follower.replication.throttled.       
                                           replicas                             
                                         	index.interval.bytes                  
                                         	leader.replication.throttled.replicas 
                                         	local.retention.bytes                 
                                         	local.retention.ms                    
                                         	max.compaction.lag.ms                 
                                         	max.message.bytes                     
                                         	message.downconversion.enable         
                                         	message.format.version                
                                         	message.timestamp.difference.max.ms   
                                         	message.timestamp.type                
                                         	min.cleanable.dirty.ratio             
                                         	min.compaction.lag.ms                 
                                         	min.insync.replicas                   
                                         	preallocate                           
                                         	remote.storage.enable                 
                                         	retention.bytes                       
                                         	retention.ms                          
                                         	segment.bytes                         
                                         	segment.index.bytes                   
                                         	segment.jitter.ms                     
                                         	segment.ms                            
                                         	unclean.leader.election.enable        
                                         See the Kafka documentation for full   
                                           details on the topic configs. It is  
                                           supported only in combination with --
                                           create if --bootstrap-server option  
                                           is used (the kafka-configs CLI       
                                           supports altering topic configs with 
                                           a --bootstrap-server option).        
--create                                 Create a new topic.                    
--delete                                 Delete a topic                         
--delete-config <String: name>           A topic configuration override to be   
                                           removed for an existing topic (see   
                                           the list of configurations under the 
                                           --config option). Not supported with 
                                           the --bootstrap-server option.       
--describe                               List details for the given topics.     
--disable-rack-aware                     Disable rack aware replica assignment  
--exclude-internal                       exclude internal topics when running   
                                           list or describe command. The        
                                           internal topics will be listed by    
                                           default                              
--help                                   Print usage information.               
--if-exists                              if set when altering or deleting or    
                                           describing topics, the action will   
                                           only execute if the topic exists.    
--if-not-exists                          if set when creating topics, the       
                                           action will only execute if the      
                                           topic does not already exist.        
--list                                   List all available topics.             
--partitions <Integer: # of partitions>  The number of partitions for the topic 
                                           being created or altered (WARNING:   
                                           If partitions are increased for a    
                                           topic that has a key, the partition  
                                           logic or ordering of the messages    
                                           will be affected). If not supplied   
                                           for create, defaults to the cluster  
                                           default.                             
--replica-assignment <String:            A list of manual partition-to-broker   
  broker_id_for_part1_replica1 :           assignments for the topic being      
  broker_id_for_part1_replica2 ,           created or altered.                  
  broker_id_for_part2_replica1 :                                                
  broker_id_for_part2_replica2 , ...>                                           
--replication-factor <Integer:           The replication factor for each        
  replication factor>                      partition in the topic being         
                                           created. If not supplied, defaults   
                                           to the cluster default.              
--topic <String: topic>                  The topic to create, alter, describe   
                                           or delete. It also accepts a regular 
                                           expression, except for --create      
                                           option. Put topic name in double     
                                           quotes and use the '\' prefix to     
                                           escape regular expression symbols; e.
                                           g. "test\.topic".                    
--topic-id <String: topic-id>            The topic-id to describe.This is used  
                                           only with --bootstrap-server option  
                                           for describing topics.               
--topics-with-overrides                  if set when describing topics, only    
                                           show topics that have overridden     
                                           configs                              
--unavailable-partitions                 if set when describing topics, only    
                                           show partitions whose leader is not  
                                           available                            
--under-min-isr-partitions               if set when describing topics, only    
                                           show partitions whose isr count is   
                                           less than the configured minimum.    
--under-replicated-partitions            if set when describing topics, only    
                                           show under replicated partitions     
--version                                Display Kafka version. 
````

##Star Zookeeper and Kafka

I recommend to use two different shell windows for this process.

To start zookeeper use the next command in the root where kafka is.

````shell
zookeeper-server-start.sh
````

if we got the next message, is because we don't have the **zookeeper.properties** file, and we need to set it up.

This file we can find in the next path: **~/kafka_2.13-3.2.0/config/zookeeper.properties**

to use this zookeeper.properties file we need to execute the zookeeper-server-start.sh command along with the path for the file

````shell
zookeeper-server-start.sh ~/kafka_2.13-3.2.0/config/zookeeper.properties
````

once zookeeper is up, in the other window we need to execute the **kafka-server-start.sh** with the server.properties to start kafka.
the server.properties is **~/kafka_2.13-3.2.0/config/server.properties**

````shell
kafka-server-start.sh ~/kafka_2.13-3.2.0/config/server.properties
````

Congratulation now you have Kafka running!

**NOTE:** don't close any window, because closing it Akfka is going to Stop

##Setup Kafka with brew

First install [homwbrew](https://docs.brew.sh/Installation) follow the installation process 

##Start Kafka without Zookeeper

Starting Kafka without Zookeeper (KRaft mode)


These lectures have been included as a demo if you are curious about trying out this mode, to see the future of Apache Kafka. Note: the KRaft mode is not ready for production yet.

If you have successfully completed the previous section, you can skip these lectures and go straight into the next section.

### Start Kafka in KRaft mode

KRaft is in early access mode as of Apache Kafka 2.8 and should be used in development only. Is not suitable for production

#Kafka CLI

They come bundled with the Kafka binaries. If you set up $PATH variable correctly, then you should be able to invoke the CLI form anywhere on your computer.

**Note** we will use the **--bootstrap-server** option everywhere, not the --zookeeper:

Correct:
 ````shell
Kafka-topics --bootstrap-server localhost:9092
```` 

Incorrect: 
````shell
Kafka-topics --zookeeper localhost:2181
````

##Kafka CLI: List topics
````shell
Kafka-topics.sh --bootstrap-server localhost:9092 --list
````

if the comand don't return nothing is because we don't have any topic.

##Kafka CLI: Create topics

TO create a topic we need to use the next command

Kafka-topics.sh --bootstrap-server localhost:9092 --create --topic [Provide topic name]

````shell
Kafka-topics.sh --bootstrap-server localhost:9092 --create --topic first_topic
````

THis command will create a topic with one partition.

**warning** the use of the underscore and period maybe collide, they are the same in Kafka.

###Kafka CLI: Create topics with more partitions

To create a topic with more partitions than one partition we need to use the next command:

````shell
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic second_topic --partitions 3
````

Ww will create a topic with 3 partitions but one replication factor, that is the number by default.

###Kafka CLI: Create topics specifying the partitions and the replication factor

To create a topic with 3 partitions and the replication factor of 2 we need to use the next command:

````shell
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic three_topic --partitions 3 --replication-factor 2
````

if we got hte below message is because we cannot have more replication than brokers in our cluster:

```shell
[2022-07-31 15:33:45,105] ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: 
  Replication factor: 2 larger than available brokers: 1.
```

For the learning we need to work with one replication factor, so we must change the replication factor to 1

````shell
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic three_topic --partitions 3 --replication-factor 1
````

###Kafka CLI: Describe topics

To describe the topics you need to use the next command

```shell
kafka-topics.sh --bootstrap-server localhost:9092 --describe
```

if you want to describe one especific topi you should use the next command:

```shell
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic first_topic
```

###Kafka CLI: Delete topics

**WARNING WARNING WARNING WARNING**

If you use windows without WSL2 delete topics may crash your kafka.

TO delete topics you need to use the next command:

kafka-topics --bootstrap-server localhost:9092 --delete --topic [topic name]

````shell
kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic first_topic
````

##Kafka CLI: Console Producer CLI

We can start to produce data into our Kafka topics, with key and without.

We will use the kafka-console-producer.sh 

We need a kafka topic with a partition of 3.
```shell
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic first_topic --partitions 3 
```

now with the kafka-console-producer.sh  we will create a producer using the next command:

```shell
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic
```

The we will send some data like:

```shell
>Hello World
>My name is Aran from Altimetirk
>I am learning Kafka
>^C%
```

To retrieve the data is with the consumer. 

If you want to specify a producer property you need to use the --producer-property flag with the acks property (acks=all).

```shell
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all
```

With this property we can set different level of acknowledgment for your rights. we will review it later.

we can add some data just for learning and fun:

```shell
>Some message that us acked
>just for fun
>we're learning
>^C%  
```

we need to be aware that if we send messages into a topic that don't exist, we will get a warning saying that the leader don't exist. This is a retrievable exception, so if there ocurrs again they will try again and again until succeeds.

```shell
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic other_topic 
>hello world
[2022-07-31 16:05:17,293] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {other_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2022-07-31 16:05:17,395] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 5 : {other_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
>another message
>^C%    
```

let's describe to verify how was the topic created:

```shell
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic other_topic
Topic: other_topic	TopicId: zzo79joaQuiCJ2-BNsaVJQ	PartitionCount: 1	ReplicationFactor: 1	Configs: segment.bytes=1073741824
Topic: other_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

the topic was created with the default options, usin fthe autocreation mechanism.

###Kafka CLI: Console Producer CLI with keys

To produce with keys we need to use the property parse.key=true and the property key.separator=:

The we can send data to Kafka:

```shell
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic --property parse.key=true --property key.separator=:
>example key:example value
>user_id_1234:Aran
```

if you introduce only a value without a key you will get an Exception:

```shell
>hello World!
org.apache.kafka.common.KafkaException: No key separator found on line number 3: 'hello World!'
	at kafka.tools.ConsoleProducer$LineMessageReader.parse(ConsoleProducer.scala:374)
	at kafka.tools.ConsoleProducer$LineMessageReader.readMessage(ConsoleProducer.scala:349)
	at kafka.tools.ConsoleProducer$.main(ConsoleProducer.scala:50)
	at kafka.tools.ConsoleProducer.main(ConsoleProducer.scala)
```

##Kafka CLI: Console Consumer CLI

Now we will read from a topic with a consumer, we can consume from the tail or from the begging of the topic.

We will use the kafka-console-consumer.sh

````shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic fisrt_topic
````

and nothing happend, because when we use a console consumer is ging to read at the end of the topic and then is going to read all the messages after that point. SO we need to use another shell terminal and produce some messages (data).

If we want to reads form the begging we need to specify the option --from-beginning

```shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --from-beginning
Hello World
I ma learning Kafka
it's working now
just for fun
holi
garbed in gree
My name is Aran form Altimetrik
Some message that us acked
we're learning
example value
Aran
Hello Hello
it's working
```

You need to be careful whe yo uuse this opition becasue it will start to read from the begging and imagine if you have a lot of data.
If you can see the data is not comming in the order that we send, remember that all is stored in a round robin order. If you have only one partition the data will come in order.

you can display the key, values and timestamp in consumer using the next properties:
```shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true --from-beginning
```

##Kafka Consumer in Group

we will use the kafka-console-consumer.sh and the --group parameter


### Replace "kafka-consumer-groups.sh" by "kafka-consumer-groups" or "kafka-consumer-groups.bat" based on your system # (or bin/kafka-consumer-groups.sh or bin\windows\kafka-consumer-groups.bat if you didn't setup PATH / Environment variables)

#### documentation for the command
```shell
kafka-consumer-groups.sh
```

#### list consumer groups
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

#### describe one specific group
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-second-application
```

#### describe another group
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-first-application
```

#### start a consumer
```shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --group my-first-application
```

#### describe the group now
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-first-application
```

#### describe a console consumer group (change the end number)
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group console-consumer-10592
```

#### start a console consumer
```shell
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --group my-first-application
```

#### describe the group again
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-first-application
```

When we use try to read from the beginning using groups we will star from the last offset was committed not from the beginning.

##Kafka Consumer Group CLI
We will use the kafka-consumer-groups

List consumer groups

```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

List specific consumer group
```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list --group my-first-consumer-group 
```

##Kafka Resetting Offsets

we will use the kafka-consumer-groups.sh and the flag  --reset-offsets, --to-earliest and --execute, we can use --all-topics or --topic

```shell
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-consumer-group --reset --to-earliest --execute --all-topics
```


```shell
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-consumer-group --reset --to-earliest --execute --all-topics 
```

**NOTE:** do not reset offsets while the consumer is running


```shell
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-consumer-group --reset --shift-by 2 --execute --all-topics
```

we can do also by -2 or 2 to go forward ir backward

#Kafka project setup - Creating a kafka Project

You need to java JDK 11 and is highly recommended having IntelliJ. We will use Gradle instead of Maven.
Steps:
- Open intelliJ and create a new project, selecting the Java project and Gradle.
- Add the name and the artifact Coordinates
- Once the project is created and open, delete the source directory "src".
- Create a new module, right-click on the project > new > module...
  - THe module can have the name of Kafka-basics for example.
- The module must be gradle and java.
- Add the name of the module and the artifact coordinates.
- NOTE: IntelliJ will continue creating the source folder, but don't use it.
- Open the module build.gradle, not the main project build.gradle.
  - Delete all the dependencies will be not required.  
  - Add the next dependencies:
```json
  // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
  implementation 'org.apache.kafka:kafka-clients:3.2.0'

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation 'org.slf4j:slf4j-api:1.7.36'

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation 'org.slf4j:slf4j-simple:1.7.36'
```
- Build the project
- Inside the Kafka-basics module create a Producer class

##Create a Kafka Producer in Java

Follow the below class, to know how to produce message from java 

````java
com.pasnys.demo.kafka.producer.ProducerDemo.java
```` 

The next class uses Callback

````java
com.pasnys.demo.kafka.producer.ProducerCallbacksDemo.java
```` 

The next class uses Callback and keys

````java
com.pasnys.demo.kafka.producer.ProducerwithKeysDemo.java
````

##Create a Kafka Consumer in Java

The below class to know how to consumer a message from java

````java
com.pasnys.demo.kafka.consumer.ConsumerDemo.java
````

This Consumer adds a gracefully shutdown

```java
com.pasnys.demo.kafka.consumer.ConsumerDemoWithGracefullyShutDown.java
```

This consumer uses Groups plus the gracefully shutdown

```java
com.pasnys.demo.kafka.consumer.ConsumerDemoGroups.java
```


###Change the build and running from Gradle to IntelliJ

IS good is you change the Build and running to IntelliJ instead of Gradle

- IntelliJ IDEA > Preferences > build, Execution, Deployment > Build Tools > Gradle > Build and run using 
  - select the Intellj IDEA option.

#Consumer Groups and Partition Rebalance

Moving partitions between consumers is called a rebalance. This can occur when you have consumers joining and leaving a group, partitions are going to move. This happens, for example if an administrator adds new partition into a topic.

How do these partitions get assignes to the consumers and what happens? Well based on these strategies the outcomes can be different, and you may be surprised.

##Eager Rebalance

The Eager Rebalance is the first strategy is and ti's one of the default behaviour.

The Eager Rebalance works like this:
- If a consumer joins to the group.
- Then all the consumers are going to stop, this why is called eager.
- and then they will give up their membership of partitions. This means that no consumer is reading form no partitions.
- Then all consumers are going to rejoin the group they were in and get a new partition assignments. This menas that all these consumers are now going to get new partitions assigned to them like this.
- This is a quite Random, and during a short period of time then the entire consumer group has stopped processing.
- Is also called Stop the world event.
- Problems of this Strategy:
  - There is not guaranteed that your consumers are going to get back the partitions that they used to have.
  - We don't want to stop the consumers to stop consuming.


##Cooperative Rebalance (Incremental Rebalance)

This is recent in Kafka and is called Incremental rebalanace. 

The strategy is reassigning a a small subset of the partitions from one customer to another, and the consumers that we do not have any reassigned partitions they can still process the data uninterrupted, and it can go through several iterations to find a stable assignment, hence the name of incremental, and avoids "stop-the-world" events where all the consumers stop processing data.

**How to use the cooperative Rebalance (Incremental)?**

Well in Kafka Consumer there is a setting called the partition assignment strategy (partition.assignment.strategy) by default is the RangeAssignor.

partition.assignment.strategy values:
- **RangeAssignor**: assign partitions on a per-topic basis (can lead to imbalance)
- **RoundRobin**: assign partitions across all topics in round-robin fashion, optimal balance. Because the consumers will have plus minus one the same number of partitions. This is an Eager type of assignment.
- **StickyAssignor**: is balanced like RoundRobin in the beginning, and then minimises partition movements when a consumer join / leave the group in order to minimise movements.

**NOTE**: These three strategies are eager strategies. That's means that every time you use them is a world event and it's going to breaking your consumer group for a little of, for a few sec. And if your consumer group is big then it can take a while to reassign all the partitions.

Newer cooperative rebalance mechanism for the partition.assignment.strategy values:
- **CooperativeStickyAssignor**: this strategy is identical to StickyAssignor but supports cooperative rebalances and therefore consumers can keep on consuming from the topic. but this time it supports the cooperative protocol and therefore the consumers can keep on consuming from the topic if the partition hasn't been moved for them. Until this moment this is the best strategy. 

Kafka 3.o has a new default assignor RageAssignor, CooperativeStickyAssignor, This means that will only use the RangeAssignor by default but if you remove it then it will use the CooperativeStickyAssignor by just rolling.

**Kafka Conect** is a cooperative rebalance enabled by default.

**Kafka Streams** turned on by default using StreamsPartitionAssignor.

##Static Group Membership

When a consumer leaves a group, its partitions are revoked and re-assigned. 
If joins back, it will have a new "member ID" and a new partition assigned.

If you specify group.instance.id it makes the consumer a static member. Upon leaving, the consumer has up to session.timeout.ms to join back and get back its partitions (else they will be re-assigned), without triggering a rebalance. THis is helpful when consumers maintain local state and cache (to avoid re-building the cache).

you can check a consumer with the cooperative strategy in the class

```java
com.pasnys.demo.kafka.consumer.ConsumerDemoGroupsCooperative.java
```

##Kafka Consumer - Auto Offset Commit Behavior

In Java COnsumer API, offset are regularly committed, and this enables at least once reading scenarios by default (under certain conditions).

**When are the offsets going to be committed?**

The offsets are committed when you call the .poll() method and auto.commit.interval.ms has elapsed.

Example: auto.commit.interval.ms=5000 and **enable.auto.commit=true**  will commit every 5 seconds.

**NOTE:** So makes sure that your messages are all succcessfully processed before you call poll() method again.

IJn the case that you disable the autocommit enable.auto.commit=false. You will need to process a separate thread, and then from time-to-time call the commitSync() method or the commitAsync() method with the correct offsets manually (this is an advanced feature).