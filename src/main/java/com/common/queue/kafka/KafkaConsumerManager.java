package com.common.queue.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerManager {

	private static final String KAFKA_SERVER_URL = "localhost:9092";

	private static Consumer<String, String> createConsumer() {

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "test3");
		props.put("enable.auto.commit", "false");
		//props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		return consumer;
	}

	public static void readMessage() {
		Consumer<String, String> consumer = createConsumer();
		consumer.subscribe(Arrays.asList("topic2"));
		System.out.println("*******End sending***********");
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(10000000);
			for (ConsumerRecord<String, String> record : records)
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}

	}

	public static void main(String... args) throws Exception {
		// readMessage();
         ConcurrentHashMap<String, String> s = new ConcurrentHashMap<>();
		int numConsumers = 6;
		String groupId = "test";
		List<String> topics = Arrays.asList("topic3");
		ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
		AtomicInteger count = new AtomicInteger(0);
		final List<ConsumerWorker> consumers = new ArrayList<>();
		for (int i = 0; i < numConsumers; i++) {
			ConsumerWorker consumer = new ConsumerWorker(i, groupId, topics,s, count);
			consumers.add(consumer);
			executor.submit(consumer);
		}
		System.out.println("Start reading_----------");
	}
}
