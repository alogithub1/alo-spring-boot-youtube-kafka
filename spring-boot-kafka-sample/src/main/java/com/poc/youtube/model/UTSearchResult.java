package com.poc.youtube.model;

import java.util.List;
import org.springframework.stereotype.Component;
import com.google.api.services.youtube.YouTube;

/**
 * The UTSearchResult class POJO which is used to return youtube search result,
 * next page token, search object which can be re-used for searching next page.
 * 
 * @version 1.0
 */
@Component
public class UTSearchResult {

	YouTube.Search.List searchList;
	List<UTVideo> videoList;
	String nextPageToken;

	public UTSearchResult() {
	}

	public YouTube.Search.List getSearchList() {
		return searchList;
	}

	public void setSearchList(YouTube.Search.List searchList) {
		this.searchList = searchList;
	}

	public List<UTVideo> getVideoList() {
		return videoList;
	}

	public void setVideoList(List<UTVideo> videoList) {
		this.videoList = videoList;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

}