package com.common.queue.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducerManager {

	private static final String KAFKA_SERVER_URL = "localhost:9092";

	private static Producer<String, KafkaMessageManager> createProducer() {

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		Producer<String, KafkaMessageManager> producer = new KafkaProducer<>(props);
		return producer;
	}

	public static void sendMessage(int x) {

		ExecutorService executor = Executors.newFixedThreadPool(3);

		final List<ProducerWorker> producerWorkers = new ArrayList<ProducerWorker>();
		for (int i = 0; i < 1; i++) {
			ProducerWorker producerWorker = new ProducerWorker(KAFKA_SERVER_URL, "topic3", x);
			producerWorkers.add(producerWorker);
			executor.submit(producerWorker);
		}
	}
	
	public static void sendMessage(Map<String, KafkaMessageManager> messages) {
		Producer<String, KafkaMessageManager> x = createProducer();
		
	}

	
}
