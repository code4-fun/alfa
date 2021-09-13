package com.alfa.service;

import com.alfa.client.GiphyClient;
import com.alfa.model.Gifs;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GiphyService {
  @Value("${giphy_api_key}")
  private String api_key;

  @NonNull
  private final GiphyClient giphyClient;

  @NonNull
  private final OpenExchangeRatesService openExchangeRatesService;

  /**
   * Returns a random gif from http://giphy.com from the category of either rich
   * or broke depending on the exchange rate of the given currency raised or dropped
   * on a specific date comparing to yesterday.
   * @param currency currency under consideration (3-letter string, for instance: "AUD")
   * @param date date
   * @return gif
   * @throws IOException exception
   */
  public ResponseEntity<byte[]> getGif(String currency, LocalDate date) throws IOException {
    Gifs gifs = giphyClient.getGifs(api_key, openExchangeRatesService.brokeOrRich(currency, date));
    int size = gifs.getData().size();
    String id = gifs.getData().get(new SecureRandom().nextInt(size)).getId();
    InputStream input = new URL("https://i.giphy.com/media/" + id + "/giphy.webp").openStream();
    byte[] bytes = input.readAllBytes();
    return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(bytes);
  }
}