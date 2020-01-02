package com.poc.youtube.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.poc.youtube.SpringBootKafkaSampleApplication;

/**
 * The KafkaSenderService class sends message as string to Kafka topic.
 * 
 * @version 1.0
 */
@Service
public class KafkaSenderService {

	private static Logger logger = LoggerFactory.getLogger(SpringBootKafkaSampleApplication.class);
	static final String TOPIC_TEST_1 = "test_1";
	static final String TOPIC_TEST_2 = "test_2";

	private final KafkaTemplate<String, String> template;

	/**
	 * Constructor for the class.
	 * 
	 * @param template KafkaTemplate for String and String values.
	 */
	public KafkaSenderService(KafkaTemplate<String, String> template) {
		this.template = template;
	}

	/**
	 * Sends given message to the given Kafka topic.
	 * 
	 * @param topic Name of the topic where the message is sent to.
	 * @param msg   The message being sent to the given topic.
	 */
	public void send(String topic, String msg) {
		this.template.send(topic, msg);
		logger.info("\n+++++ MESSAGE SENT:: " + msg + " to topic:: " + topic + " +++++\n");
	}

}
