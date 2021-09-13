package com.alfa.client;

import com.alfa.model.Gifs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "giphy-client", url = "${gifs_url}")
public interface GiphyClient {
  @GetMapping(path = "/v1/gifs/search", params = {"api_key", "q"},
      produces = "application/json")
  Gifs getGifs(@RequestParam("api_key") String api_key,
               @RequestParam("q") String q);
}