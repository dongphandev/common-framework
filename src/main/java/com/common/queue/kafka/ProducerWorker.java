package com.common.queue.kafka;

import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerWorker implements Runnable {
	private final KafkaProducer<String, KafkaMessageManager> producer;
	private final String topic;

	private int count;

	public ProducerWorker(String brokers, String topic, int count) {
		Properties prop = createProducerConfig(brokers);
		this.producer = new KafkaProducer<String, KafkaMessageManager>(prop);
		this.topic = topic;
		this.count = count;
	}

	private static Properties createProducerConfig(String brokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("acks", "all");
		props.put("request.timeout.ms", "60000");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "com.simplelibrary.framework.KafkaMessageSerializer");
		return props;
	}

	@Override
	public void run() {
		System.out.println("Produces 3 messages");
		for (int i = 0; i < this.count; i++) {
			KafkaMessageManager kafkaMessageManager = new KafkaMessageManager();
			kafkaMessageManager.setAddress("Message " + i);
			kafkaMessageManager.setDate(new Date());
			kafkaMessageManager.setId(i);
			producer.send(new ProducerRecord<String, KafkaMessageManager>(topic, kafkaMessageManager), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println(
							"Sent:" + kafkaMessageManager.toString() + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
				}
			});
		}
		// closes producer
		producer.close();

	}

}
