package com.pasnys.demo.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * This class is a demo for the Kafaka consumer
 *
 * Remember to have a consumer group called "my-third-application"
 * If you don't have it or created you can use the next command:
 *  kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic demo_java --group my-third-application
 *
 *  With this consumer we expect a "Process finished with exit code 1"
 *
 *  For this class we need an extra configuration that allows to run several instances from our ConsumerDemoGroup at time
 *  other way this demo will not work
 *  Go to Select Run/debug Configuration > Edit configuration > select the Class ro edit the run configuration (ConsumerDemoGroups)
 *  > modify options > select allow multiple instances > click on Apply button.
 *
 *
 *
 */
public class ConsumerDemoGroupsPartitionsRebalance {

    //A Logger is created to log all the messages to the log/console
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoGroupsPartitionsRebalance.class.getSimpleName());


    public static void main(String[] args) {
        log.info("I am the Consumer");

        //The bootstrap is declared in a variable
        String BOOTSTRAP = "localhost:9092";
        //A groupId is declared
        String groupId = "my-third-application";
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
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(pro);


        //To implement the Graceful shutdown we need to get the reference to the current thread.
        final Thread mainThread = Thread.currentThread();

        //Now we will add the shutdown hook with the a New Thread
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                log.info("Detected a shutdown, let's exit calling the consumer.wakeup()...");
                /*The consumer.wakeup() menas that the next time the consumer is going to do consumer.poll instead if
                    working is going to throw an exception (WakeupException).
                    This consumer.wakeup() code is called so that this consumer.poll method does fo into an exception,
                    and we leave the while loop where the is the polling (Consumer.poll)
                 */
                consumer.wakeup();

                //join the main thread to allow the execution of the code in the main thread
                try{
                    mainThread.join();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        try{
            //No we create the subscriber consumer to one topic, we can use a pattern for example
            //consumer.subscribe(Collections.singleton(topic));

            //If we can subscribe to multiple topic you can use Arrays.asList passing all the topics in to list
            consumer.subscribe(Arrays.asList(topic));

            //Now we will poll for new data
            while(true){

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
        } catch(WakeupException we ) {
            log.info("Wakeup Exception!");
            //We will ignore the exception because is expected
        } catch(Exception e) {
            log.error("Unexpected exception");
        } finally {
            //we need to close the consumer after get any expcetion
            consumer.close();
            log.info("The consumer is now gracefully closed");
        }


    }

}
