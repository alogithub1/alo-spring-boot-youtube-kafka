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

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.poc.youtube.model.UTSearchResult;
import com.poc.youtube.model.UTVideo;
import com.poc.youtube.repository.YoutubeRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YoutubeServiceImpl implements YoutubeService {

	private static Logger logger = LoggerFactory.getLogger(YoutubeServiceImpl.class);

	@Autowired
	private UTSearchResult uTSearchResult;

	@Autowired
	YoutubeRepository youtubeRepository;

	public UTSearchResult findVideos(String queryTerm, Long numberOfVideos, String nextPageToken) {

		SearchListResponse searchResponse = null;
		List<UTVideo> videosList = new ArrayList<UTVideo>();

		searchResponse = youtubeRepository.findVideos(queryTerm, numberOfVideos, nextPageToken);
		logger.info("\n+++++ executed search. +++++\n");

		List<SearchResult> searchResultList = null;
		;
		if (searchResponse != null) {
			searchResultList = searchResponse.getItems();
		}

		if (searchResultList != null) {
			videosList = getVideos(searchResultList.iterator());

			uTSearchResult.setVideoList(videosList);
			uTSearchResult.setNextPageToken(searchResponse.getNextPageToken());
		}

		return uTSearchResult;
	}

	private List<UTVideo> getVideos(Iterator<SearchResult> iteratorSearchResults) {

		List<UTVideo> uTVideos = new ArrayList<UTVideo>();

		if (!iteratorSearchResults.hasNext()) {
			logger.info("\n+++++ There aren't any results for your query. +++++\n");
		}

		// Iterate the search result to get individual video
		while (iteratorSearchResults.hasNext()) {
			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();
			UTVideo uTVideo = new UTVideo();

			if (rId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
				uTVideo.setId(rId.getVideoId());
				uTVideo.setTitle(singleVideo.getSnippet().getTitle());
				uTVideo.setUrl(thumbnail.getUrl());
				uTVideos.add(uTVideo);
			}
		}
		logger.info("\n+++++ Iterated search result and formed list of UTVideo. +++++\n");
		return uTVideos;
	}

	public SearchListResponse findVedoesAndMetaData(String queryTerm, Long numberOfVideos, String nextPageToken) {
		logger.info("\n+++++ executed search. +++++\n");
		return youtubeRepository.findVideos(queryTerm, numberOfVideos, nextPageToken);

	}

}
