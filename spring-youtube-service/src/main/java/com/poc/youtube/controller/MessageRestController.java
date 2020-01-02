package com.poc.youtube.controller;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.youtube.model.UTSearchResult;
import com.poc.youtube.model.UTVideo;
import com.poc.youtube.service.KafkaSenderService;
import com.poc.youtube.service.YoutubeService;
import com.poc.youtube.util.JaxbHelper;

@RestController
public class MessageRestController {

	private static Logger logger = LoggerFactory.getLogger(MessageRestController.class);

	@Autowired
	public KafkaSenderService kafkaSenderService;

	@Autowired
	private JaxbHelper jaxbHelper;

	@Autowired
	private UTSearchResult uTSearchResult;

	@Autowired
	private YoutubeService youtubeService;

	private CountDownLatch latch;

	ExecutorService executor = Executors.newFixedThreadPool(5);

	@PostMapping("/messages")
	public void sendMessage(@RequestBody String msg) {
		try {
			poc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void poc() throws Exception {

		AtomicInteger num = new AtomicInteger(0); // To track page number

		// Parameters for next page search
		String nextPageToken = null;
		String queryTerm = "Telecom";
		List<UTVideo> uTVideos;
		Long numberOfVideos = new Long(5);

		// Start search
		do {
			latch = new CountDownLatch(5); // latch to put a await before scrolling next page
			uTSearchResult = youtubeService.findVideos(queryTerm, numberOfVideos, nextPageToken);

			if (uTSearchResult != null) {

				uTVideos = uTSearchResult.getVideoList();
				nextPageToken = uTSearchResult.getNextPageToken(); // Parameters for next page search

				// Send the fetched videos to Kafka topic as XML string
				if (uTVideos != null) {
					for (UTVideo uTVideo : uTVideos) {
						if (uTVideo != null) {

							// Runnable, return void, nothing, submit and run the task async
							executor.submit(() -> kafkaSenderService.send("test_1", jaxbHelper.pojoToXml(uTVideo)));

							// sender.send("test_1", jaxbHelper.pojoToXml(uTVideo));
						}
					}
				}
			}

			logger.info("\n+++++ PAGE NUMBER:: " + num.incrementAndGet() + " +++++\n"
					+ "\n+++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n\n\n");

			// just to exit the loop at given page
			// if (num.get() == 3) {
			// System.exit(0);
			// }

			latch.await(5, TimeUnit.SECONDS); // wait for few seconds before scrolling next page
		} while (nextPageToken != null); // if just completed search result has next page token, then repeat search for
											// next page

		executor.shutdown();
	}

}

@Data
class Message {
	private String topic;
	private String msg;
}