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

import com.google.api.services.youtube.model.SearchListResponse;
import com.poc.youtube.model.UTSearchResult;

public interface YoutubeService {

	public UTSearchResult findVideos(String queryTerm, Long numberOfVideos,
			String nextPageToken);
	
	public SearchListResponse findVedoesAndMetaData(String queryTerm, Long numberOfVideos,
			String nextPageToken);

}
