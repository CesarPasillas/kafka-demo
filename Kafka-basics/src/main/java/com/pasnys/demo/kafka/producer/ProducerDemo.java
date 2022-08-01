package com.pasnys.demo.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a demo of a Kafka Producer.
 * To execute the class you need to create a Topic "demo_java" you can crete it with the next command line:
 *  kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic demo_java --partitions 3 --replication-factor 1
 *
 *  Remember that after use the producer  (KafkaProducer) you need to flush and close it.
 */
public class ProducerDemo {

    //A Logger is created to log all the messages to the log/console
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("Hello world!");

        //Create Producer Properties
        Properties properties = new Properties();
        // This property is to indicate where kafka broker is started - (--bootstrap localhost:9092)
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        /*
            The next two properties are used to transform the java objects into binary for Kafka
            -We use the StringSerializer.class.getName() as a key serializer
         */
        //This property is for the key
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //This is for the value
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        //Create the Producer using the KafkaProducer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //Create a producer record, this class Allows sending data to Kafka Topic
        //for the example we use the topic name : "demo_java" and the message : "hello World"
        //You need to create the Topic before to tun this Class
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "hello World");

        //Send data - asynchronous
        producer.send(producerRecord);

        /*
        When we finished using the Producer we need to flush and close it to avoid open producers
         */
        //flush data - synchronous
        producer.flush();

        //close the producer
        producer.close();

    }
}
