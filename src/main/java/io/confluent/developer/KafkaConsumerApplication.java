package io.confluent.developer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerApplication {

    private volatile boolean keepConsuming = true;
    private ConsumerRecordsHandler<String, String> recordsHandler;
    private Consumer<String, String> consumer;

    public KafkaConsumerApplication(final Consumer<String, String> consumer,
                                    final ConsumerRecordsHandler<String, String> recordsHandler) {
        this.consumer=consumer;
        this.recordsHandler=recordsHandler;
    }

    public void runConsume(final Properties consumerProps) {
        try {
            System.out.println("\n"+consumerProps.getProperty("input.topic.name")+"\n");
            consumer.subscribe(Collections.singletonList(consumerProps.getProperty("input.topic.name")));
            while(keepConsuming) {
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
                System.out.println("\n"+consumerRecords.toString()+"\n");
                recordsHandler.process(consumerRecords);
            }
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        keepConsuming=false;
    }

    public static Properties loadProperties(String fileName) throws IOException {
        final Properties props=new Properties();
        final FileInputStream input = new FileInputStream(fileName);
        props.load(input);
        input.close();
        return props;
    }

    public static void main(String[] args) throws Exception {
        if(args.length<1) {
            throw new IllegalArgumentException( "This program takes one argument: the path to an environment configuration file.");
        }

        System.out.println("\n"+args[0]+"\n");
        final Properties consumerAppProps = KafkaConsumerApplication.loadProperties(args[0]);
        final String filePath = consumerAppProps.getProperty("file.path");
        System.out.println("\n"+filePath+"\n");
        final Consumer<String, String> consumer=new KafkaConsumer<String, String>(consumerAppProps);
        final ConsumerRecordsHandler <String, String> recordsHandler = new FileWritingRecordsHandler(Paths.get(filePath));
        final KafkaConsumerApplication consumerApplication=new KafkaConsumerApplication(consumer, recordsHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(consumerApplication::shutdown));

        consumerApplication.runConsume(consumerAppProps);
    }
}
