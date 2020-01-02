/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.poc.youtube.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.poc.youtube.config.Auth;
import com.poc.youtube.model.UTSearchResult;
import com.poc.youtube.model.UTVideo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * The YoutubeService class searches for Youtube videos for a given search
 * text and returns the matching result.
 * 
 * @version 1.0
 */
@Service
public class YoutubeService {

	private static final String PROPERTIES_FILENAME = "youtube.properties";
	private static final long NUMBER_OF_VIDEOS_RETURNED = 3;
	private static YouTube youtube;
	private static Logger logger = LoggerFactory.getLogger(YoutubeService.class);

	@Autowired
	private UTSearchResult uTSearchResult;

	/**
	 * Finds matching Youtube videos based on the given search parameters.
	 * 
	 * @param queryTerm     The text which needs to be searched for.
	 * @param nextPageToken The next page token string if the previous search has
	 *                      nextpage token.
	 * @param uTSearchList  The YouTube.Search.List if its for next page search.
	 * @return A UTSearchResult object which has a) matching UTVideo list, b) next
	 *         page token if the search result has next page token, c)
	 *         YouTube.Search.List for next page search.
	 */
	public UTSearchResult findVideos(String queryTerm, String nextPageToken, YouTube.Search.List uTSearchList) {

		Properties properties = new Properties();
		List<UTVideo> videosList = new ArrayList<UTVideo>();

		try {
			InputStream in = YoutubeService.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
			properties.load(in);

		} catch (IOException e) {
			System.err.println(
					"There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}

		try {

			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest request) throws IOException {
				}
			}).setApplicationName("youtube-cmdline-search-sample").build();

			String apiKey = properties.getProperty("youtube.apikey");

			if (uTSearchList == null) { // search for the first page
				uTSearchList = youtube.search().list("id,snippet");

				uTSearchList.setKey(apiKey);
				uTSearchList.setQ(queryTerm);

				uTSearchList.setType("video");

				uTSearchList.setFields(
						"items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken");
				uTSearchList.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			} else { // search for the next pages
				if (nextPageToken != null) {
					uTSearchList.setPageToken(nextPageToken);
				}
			}

			nextPageToken = null; // ensure token is null before starting search
			SearchListResponse searchResponse = uTSearchList.execute();
			logger.info("\n+++++ executed search. +++++\n");
			
			List<SearchResult> searchResultList = searchResponse.getItems();

			if (searchResultList != null) {
				videosList = getVideos(searchResultList.iterator());

				uTSearchResult.setVideoList(videosList);
				uTSearchResult.setNextPageToken(searchResponse.getNextPageToken());
				uTSearchResult.setSearchList(uTSearchList);

			}

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return uTSearchResult;
	}

	/**
	 * Iterates the search result, maps each record to UTVideo object
	 * 
	 * @param iteratorSearchResults The Iterator of
	 *                              com.google.api.services.youtube.model.SearchResult
	 * @return A list of UTVideo .
	 */
	private List<UTVideo> getVideos(Iterator<SearchResult> iteratorSearchResults) {

		List<UTVideo> uTVideos = new ArrayList<UTVideo>();

		if (!iteratorSearchResults.hasNext()) {
			logger.info("\n+++++ There aren't any results for your query. +++++\n");
		}

		//Iterate the search result to get individual video
		while (iteratorSearchResults.hasNext()) {
			SearchResult aVideo = iteratorSearchResults.next();
			ResourceId rId = aVideo.getId();
			UTVideo uTVideo = new UTVideo();

			if (rId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = aVideo.getSnippet().getThumbnails().getDefault();
				uTVideo.setId(rId.getVideoId());
				uTVideo.setTitle(aVideo.getSnippet().getTitle());
				uTVideo.setUrl(thumbnail.getUrl());
				uTVideos.add(uTVideo);
			}
		}
		logger.info("\n+++++ Iterated search result and formed list of UTVideo. +++++\n");
		return uTVideos;
	}

}
