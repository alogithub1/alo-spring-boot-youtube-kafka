package com.poc.youtube.controller;

import lombok.Data;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.api.services.youtube.model.SearchListResponse;
import com.poc.youtube.model.UTSearchResult;
import com.poc.youtube.model.UTVideo;
import com.poc.youtube.service.KafkaSenderService;
import com.poc.youtube.service.YoutubeService;
import com.poc.youtube.util.JaxbHelper;

@RestController
public class MessageRestController {

	private static Logger logger = LoggerFactory.getLogger(MessageRestController.class);

	@Autowired
	public KafkaSenderService sender;

	@Autowired
	private JaxbHelper jaxbHelper;

	@Autowired
	private UTSearchResult uTSearchResult;

	@Autowired
	private YoutubeService youtubeService;

	private CountDownLatch latch;

	@Autowired
	RestTemplate restTemplate;

	ExecutorService executor = Executors.newFixedThreadPool(5);

	@RequestMapping(value = { "/find-v/{searchText}" }, method = RequestMethod.GET,
	produces = { "application/json", "application/xml" })
	public @ResponseBody ResponseEntity<UTSearchResult> getV(@PathVariable String searchText) {

		String theUrl = "http://yt-service/videos/" + searchText;
		// ResponseEntity<String> en = restTemplate.exchange(theUrl,
		// HttpMethod.GET, null, String.class);

		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity<SearchListResponse> entity = new
		// HttpEntity<SearchListResponse>(null, headers);

		ResponseEntity<UTSearchResult> rs = restTemplate.exchange(theUrl, HttpMethod.GET, null, UTSearchResult.class);

		return rs;
	}
	
	@RequestMapping(value = { "/find-v2/{searchText}/{numberOfVideos}/{nextPageToken}" }, method = RequestMethod.GET,
	produces = { "application/json", "application/xml" })
	public @ResponseBody ResponseEntity<UTSearchResult> getV2(@PathVariable String searchText,
			@PathVariable(required = false) Long numberOfVideos, @PathVariable(required = false) String nextPageToken) {


		String theUrl = "http://yt-service/videos/" + searchText + "/" + numberOfVideos + "/" + nextPageToken;
		// ResponseEntity<String> en = restTemplate.exchange(theUrl,
		// HttpMethod.GET, null, String.class);

		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity<SearchListResponse> entity = new
		// HttpEntity<SearchListResponse>(null, headers);

		ResponseEntity<UTSearchResult> rs = restTemplate.exchange(theUrl, HttpMethod.GET, null, UTSearchResult.class);

		return rs;
	}
	

	@RequestMapping(value = { "/find-videos/{searchText}" }, method = RequestMethod.GET,
			produces = { "application/json", "application/xml" })
	public ResponseEntity<SearchListResponse> getVideos(@PathVariable String searchText) {

		String theUrl = "http://yt-service/vm1/" + searchText;
		// ResponseEntity<String> en = restTemplate.exchange(theUrl,
		// HttpMethod.GET, null, String.class);

		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity<SearchListResponse> entity = new
		// HttpEntity<SearchListResponse>(null, headers);

		ResponseEntity<SearchListResponse> rs = restTemplate.exchange(theUrl, HttpMethod.GET, null, SearchListResponse.class);
		return rs;
	}

	@RequestMapping(value = { "/find-videos-n/{searchText}/{numberOfVideos}" }, method = RequestMethod.GET,
			produces = { "application/json", "application/xml" })
	public ResponseEntity<SearchListResponse> getNVideos(@PathVariable String searchText,
			@PathVariable(required = false) Long numberOfVideos) {

		String theUrl = "http://yt-service/vm2/" + searchText + "/" + numberOfVideos;
		ResponseEntity<SearchListResponse> response = restTemplate.exchange(theUrl, HttpMethod.GET, null, SearchListResponse.class);
		logger.info("+++++ executed search. +++++\n");
		return response;
	}

	@RequestMapping(value = { "/find-videos-next/{searchText}/{numberOfVideos}/{nextPageToken}" }, method = RequestMethod.GET,
			produces = { "application/json", "application/xml" })
	public ResponseEntity<SearchListResponse> getNVideosNextPage(@PathVariable String searchText,
			@PathVariable(required = false) Long numberOfVideos, @PathVariable(required = false) String nextPageToken) {

		String theUrl = "http://yt-service/vm3/" + searchText + "/" + numberOfVideos + "/" + nextPageToken;
		ResponseEntity<SearchListResponse> response = restTemplate.exchange(theUrl, HttpMethod.GET, null, SearchListResponse.class);
		logger.info("+++++ executed search. +++++\n");

		return response;
	}

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
		com.google.api.services.youtube.YouTube.Search.List searchList = null;
		String queryTerm = "Telecom";
		List<UTVideo> uTVideos;

		// Start search
		do {
			latch = new CountDownLatch(5); // latch to put a await before scrolling next page
			uTSearchResult = youtubeService.findVideos(queryTerm, nextPageToken, searchList);

			if (uTSearchResult != null) {

				uTVideos = uTSearchResult.getVideoList();
				nextPageToken = uTSearchResult.getNextPageToken(); // Parameters for next page search
				searchList = uTSearchResult.getSearchList(); // Parameters for next page search

				// Send the fetched videos to Kafka topic as XML string
				if (uTVideos != null) {
					for (UTVideo uTVideo : uTVideos) {
						if (uTVideo != null) {

							// Runnable, return void, nothing, submit and run the task async
							executor.submit(() -> sender.send("test_1", jaxbHelper.pojoToXml(uTVideo)));

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