package com.pasnys.demo.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * This class is a demo of a Kafka Producer with a Callback.
 *
 * To execute the class you need to create a Topic "demo_java" you can crete it with the next command line:
 *  kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic demo_java --partitions 3 --replication-factor 1
 *
 *  Remember that after use the producer  (KafkaProducer) you need to flush and close it.
 */
public class ProducerwithKeysDemo {

    //A Loggger is create to log all the messages to the log/console
    private static final Logger log = LoggerFactory.getLogger(ProducerwithKeysDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a Kafka Producer with Callback");

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

        //To test the callback we will send several messages using a for loop
        for (int i = 0; i < 10; i++){

            //We will create the keys for our producer
            String topic = "demo_java";
            String value = "hello world! : " + i;
            String key = "id_" + i;

            //Create a producer record, this class Allows sending data to Kafka Topic
            //for the example we use the topic name : "demo_java" and the message : "hello World"
            //You need to create the Topic before to tun this Class
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

            //Send data - asynchronous
            // the callback is sent here

            producer.send(producerRecord, (
                    (metadata, exception) -> {
                        //This method executes every time a record is successfully sent or an exception is thrown.

                        if(exception == null){
                            //the record was successfully sent

                            // The log displays the metadata of the message if we run the class we can see how the partition changes
                            log.info("Received new Metadata: \n" +
                                    "Topic: " + metadata.topic() + "\n" +
                                    "key:" + producerRecord.key() + "\n" +
                                    "Partition: " + metadata.partition() + "\n" +
                                    "Offset: " + metadata.offset() + "\n" +
                                    "Timestamp: " + metadata.timestamp());
                        } else {
                            log.error("Error while producer", exception);
                        }
            }));

            /*
             * For the test we need to simulate some batches this code will be to test send the data to different partitions
             * If we remove the try block the messages are sent very quick so all the messages goes to the same partition
             */
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }

            /*producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    //This method executes every time a record is succesfully sent or an exception is thrown.

                    if(exception == null){
                        //the record was successfully sent

                        // The log displays the metadata of the message if we run the class we can see how the partition changes
                        log.info("Received new Metadata: \n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Partition: " + metadata.partition() + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp());
                    } else {
                        log.error("Error while producer", exception);
                    }
                }
            });*/

        }

        /*
         * By default, the partitioner is the class org.apache.kafka.clients.producer.internals.DefaultPartitioner.
         * We can change it or configure it too by set up the class.
         */


        /*
        When we finished using the Producer we need to flush and close it to avoid open producers
         */
        //flush data - synchronous
        producer.flush();

        //close the producer
        producer.close();

    }
}
