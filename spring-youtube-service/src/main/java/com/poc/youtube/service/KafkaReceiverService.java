package com.poc.youtube.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.poc.youtube.model.UTVideo;
import com.poc.youtube.util.JaxbHelper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The KafkaReceiverService class listens to Kafka topic and receives messages.
 * 
 * @version 1.0
 */
@Service
public class KafkaReceiverService {

	private static Logger logger = LoggerFactory.getLogger(KafkaReceiverService.class);
	static final String TOPIC_TEST_1 = "test_1";
	static final String TOPIC_TEST_2 = "test_2";

	@Autowired
	private JaxbHelper jaxbHelper;

	@Autowired
	private KafkaSenderService kafkaSenderService;

	private CountDownLatch latch = new CountDownLatch(1);

	ExecutorService executorService = Executors.newFixedThreadPool(5);

	public CountDownLatch getLatch() {
		return latch;
	}

	/**
	 * Listens to speficied Kafka topic.
	 * 
	 * @param cr ConsumerRecord - A key/value pair to be received from Kafka.
	 */
	@KafkaListener(topics = TOPIC_TEST_1)
	public void listenasString(ConsumerRecord<String, String> cr) {

		if (cr.value() != null) {
			// executorb.submit(() -> validateVideoTitle(cr.value()));
			logger.info("\n+++++ MESSAGE RECEIVED:: " + cr.value() + " +++++\n");
			executorService.submit(
					() -> kafkaSenderService.send("test_2", jaxbHelper.pojoToXml(validateVideoTitle(cr.value()))));

		}

		latch.countDown();
	}

	/**
	 * Unmarshalls given XML string into UTVideo object, validates the title for
	 * word "telecom" and if it contains then replaces it with word "telco".
	 * 
	 * @param message string which needs to be validated.
	 */
	public UTVideo validateVideoTitle(String message) {
		UTVideo video = jaxbHelper.xmlToPojo(message);

		if (video != null && video.getTitle() != null) {
			video.setTitle(video.getTitle().replaceAll("(?i)telecom", "telco"));
			logger.info("\n+++++ VIDEO TITLE VALIDATED +++++\n");
		}
		return video;
	}

}
