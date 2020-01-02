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

package com.poc.youtube.repository;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.poc.youtube.config.Auth;
import com.poc.youtube.model.UTVideo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class YoutubeRepositoryImpl implements YoutubeRepository {

	private static final String PROPERTIES_FILENAME = "youtube.properties";
	private static YouTube youtube;
	private static Logger logger = LoggerFactory.getLogger(YoutubeRepositoryImpl.class);

	@Override
	public com.google.api.services.youtube.model.SearchListResponse findVideos(String queryTerm, Long numberOfVideos,
			String nextPageToken) {

		if (numberOfVideos == null || numberOfVideos < 1) {
			numberOfVideos = new Long(5);
		}

		SearchListResponse searchResponse = null;
		Properties properties = new Properties();
		YouTube.Search.List sList;

		try {
			InputStream in = YoutubeRepositoryImpl.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
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

			sList = youtube.search().list("id,snippet");

			sList.setKey(apiKey);
			sList.setQ(queryTerm);

			sList.setType("video");

			sList.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken");
			sList.setMaxResults(numberOfVideos);

			if (nextPageToken != null) { // this is for searching next page
				sList.setPageToken(nextPageToken);
			}

			nextPageToken = null; // ensure token is null before starting search
			searchResponse = sList.execute();
			logger.info("\n+++++ executed search. +++++\n");

		} catch (GoogleJsonResponseException e) {
			System.err.println(
					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return searchResponse;
	}

}
