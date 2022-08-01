package com.pasnys.demo.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import java.util.*;

/**
 * This class is a demo for the Kafaka consumer
 *
 * Remember to have a consumer group called "my-second-application"
 * If you don't have it or created you can use the next command:
 *  kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo_java --group my-second-application
 */
public class ConsumerDemo {

    //A Logger is created to log all the messages to the log/console
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());


    public static void main(String[] args) {
        log.info("I am the Consumer");

        //The bootstrap is declared in a variable
        String BOOTSTRAP = "localhost:9092";
        //A groupId is declared
        String groupId = "my-second-application";
        //Variable for the topic
        String topic = "demo_java";

        //Create the consumer properties
        Properties pro = new Properties();
        // This property is to indicate where kafka broker is started - (--bootstrap localhost:9092)
        pro.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP);
         /*
            The next two properties are used to transform the java objects into binary for Kafka
            -We use the StringSerializer.class.getName() as a key serializer
         */
        //This property is for the key
        pro.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //This is for the value
        pro.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //This property is to set up the group id
        pro.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        /*This property is for auto offset reset, this property haas three values "none/earliest/latest"
         * earliest: read form the very beginning of the topic
         * latest: read from now of the topic
         * none: if not previous offset are found then even do not start
         */
        pro.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Create the kafka Consumer with the properties
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(pro);

        //No we create the subscriber consumer to one topic, we can use a pattern for example
        consumer.subscribe(Collections.singleton(topic));

        //If we can subscribe to multiple topic you can use Arrays.asList passing all the topics in to list
        //consumer.subscribe(Arrays.asList(topic));

        //Now we will poll for new data
        while(true){

            log.info("Polling  The data");
            /*In this line we indicate poll kafka and gets as many records as you can, but if you don't have any
                replay from Kafka, then I will wait up to 100 milliseconds to get some records.

                If we don't receive any records in these 100 milliseconds, then go to the next line of code and records
                will be an empty collection.
             */
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));


            //Then we can iterate our records
            for(ConsumerRecord<String, String> record : records){
                //(now we will get some information from what we read
                log.info("[Key: " + record.key() + ", Value: " + record.value() +
                        ", Partition: " + record.partition() + ", Offset: " + record.offset() + "]");
            }

        }


    }

}
