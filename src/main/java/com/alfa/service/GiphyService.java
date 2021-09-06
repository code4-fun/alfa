package com.alfa.service;

import com.alfa.client.GiphyClient;
import com.alfa.model.Gifs;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
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
   * Returns random gif from http://giphy.com from the category broke or rich
   * dependig on the exchange rate of the currency provided in path.
   * @param currency currency under consideration (3-letter string, for instance: "AUD")
   * @return gif
   * @throws IOException throws IOException
   */
  public ResponseEntity<byte[]> getGif(String currency) throws IOException {
    Gifs gifs = giphyClient.getGif(api_key, openExchangeRatesService.brokeOrRich(currency));
    String id = gifs.getData().get(new SecureRandom().nextInt(50)).getId();
    InputStream input = new URL("https://i.giphy.com/media/" + id + "/giphy.webp").openStream();
    byte[] bytes = input.readAllBytes();
    return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(bytes);
  }
}