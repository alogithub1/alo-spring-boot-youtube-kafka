package com.mynotes.spring.cloud.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RecommendationController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/recommendations", method = RequestMethod.GET)
    @ResponseBody
    public Movie[] recommendations() {
        Movie[] result = restTemplate.getForObject("http://movie-service/movies", Movie[].class);

        invokeYoutube();

        return result;
    }

    public void invokeYoutube() {
        //restTemplate.getForObject("http://youtube-service/messages", String.class);
        restTemplate.postForLocation("http://yt-service/messages", "hello");
    }
}
