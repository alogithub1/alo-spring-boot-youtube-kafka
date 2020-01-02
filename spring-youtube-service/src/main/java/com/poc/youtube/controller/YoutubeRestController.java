package com.poc.youtube.controller;

import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.youtube.model.SearchListResponse;
import com.poc.youtube.model.UTSearchResult;
import com.poc.youtube.model.UTVideo;
import com.poc.youtube.service.KafkaSenderService;
import com.poc.youtube.service.YoutubeService;
import com.poc.youtube.util.JaxbHelper;

@RestController
public class YoutubeRestController {

	private static Logger logger = LoggerFactory.getLogger(YoutubeRestController.class);

	@Autowired
	public KafkaSenderService kafkaSenderService;

	@Autowired
	private JaxbHelper jaxbHelper;

	@Autowired
	private UTSearchResult uTSearchResult;

	@Autowired
	private com.poc.youtube.service.YoutubeService youtubeService;

	private CountDownLatch latch;
	public Long numberOfVideos = new Long(2);
	public String nextPageToken = null;

	ExecutorService executor = Executors.newFixedThreadPool(5);

	@GetMapping(value = { "/videos/{queryTerm}", "/videos/{queryTerm}/{numberOfVideos}",
			"/videos/{queryTerm}/{numberOfVideos}/{nextPageToken}" })
	public UTSearchResult findVideos(@PathVariable String queryTerm,
			@PathVariable(required = false) Long numberOfVideos, @PathVariable(required = false) String nextPageToken) {

		logger.info("\n+++++ executed search. +++++\n");
		return youtubeService.findVideos(queryTerm, numberOfVideos, nextPageToken);
	}

	@GetMapping(value = { "/vm1/{queryTerm}", "/vm1/{queryTerm}/{numberOfVideos}/{nextPageToken}" })
	public SearchListResponse vm1(@PathVariable(required = true) String queryTerm) {

		SearchListResponse slr = youtubeService.findVedoesAndMetaData(queryTerm, numberOfVideos, nextPageToken);
		logger.info("\n+++++ executed search. +++++\n" + slr);
		return slr;
	}

	@GetMapping(value = { "/vm2/{queryTerm}", "/vm3/{queryTerm}/{numberOfVideos}" })
	public SearchListResponse vm2(@PathVariable(required = true) String queryTerm,
			@PathVariable(required = false) Long numberOfVideos) {

		SearchListResponse slr = youtubeService.findVedoesAndMetaData(queryTerm, numberOfVideos, nextPageToken);
		logger.info("\n+++++ executed search. +++++\n" + slr);
		return slr;
	}

	@GetMapping(value = { "/vm3/{queryTerm}", "/vm3/{queryTerm}/{numberOfVideos}",
			"/vm3/{queryTerm}/{numberOfVideos}/{nextPageToken}" })
	public SearchListResponse vm3(@PathVariable(required = true) String queryTerm,
			@PathVariable(required = false) Long numberOfVideos, @PathVariable(required = false) String nextPageToken) {

		SearchListResponse slr = youtubeService.findVedoesAndMetaData(queryTerm, numberOfVideos, nextPageToken);
		logger.info("\n+++++ executed search. +++++\n" + slr);
		return slr;
	}

	@PostMapping("/messages-ut")
	public void sendMessage(@RequestBody String msg) {

		String queryTerm = "roland";
		Long numberOfVideos = new Long(5);
		String nextPageToken = null;
		try {
			poc(queryTerm, numberOfVideos, nextPageToken);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void poc(String queryTerm, Long numberOfVideos, String nextPageToken) throws Exception {

		AtomicInteger num = new AtomicInteger(0); // To track page number

		List<UTVideo> uTVideos;

		// Start search
		do {
			latch = new CountDownLatch(5); // latch to put a await before scrolling next page
			uTSearchResult = youtubeService.findVideos(queryTerm, numberOfVideos, nextPageToken);

			if (uTSearchResult != null) {

				uTVideos = uTSearchResult.getVideoList();
				nextPageToken = uTSearchResult.getNextPageToken(); // Parameter for next page search

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
