package com.alfa.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alfa.client.GiphyClient;
import com.alfa.model.Gifs;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application-test.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GiphyServiceTest {
  @Autowired
  private GiphyClient giphyClient;

  @Autowired
  GiphyService giphyService;

  private static WireMockServer wireMockServer;

  @DynamicPropertySource
  static void overrideWebClientBaseUrl(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("rates_url", wireMockServer::baseUrl);
    dynamicPropertyRegistry.add("gifs_url", wireMockServer::baseUrl);
  }

  @BeforeAll
  static void startWireMock() {
    wireMockServer = new WireMockServer(WireMockConfiguration
        .wireMockConfig()
        .dynamicPort());
    wireMockServer.start();
  }

  @AfterAll
  static void stopWireMock() {
    wireMockServer.stop();
  }

  @Test
  void getGifTest() throws IOException {
    Map<String, StringValuePattern> map = new HashMap<>();
    map.put("api_key", equalTo("24EoD8JiB5kDh11D2EPreqNIOCmEhMFh"));
    map.put("q", equalTo("broke"));

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/v1/gifs/search"))
            .withQueryParams(map)
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("payload/giphy.json"))
    );

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/historical/2021-09-08.json"))
            .withQueryParam("app_id", equalTo("65e7ecdca7914e50a9e8763c3a06d408"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("payload/rates_today.json"))
    );

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/historical/2021-09-07.json"))
            .withQueryParam("app_id", equalTo("65e7ecdca7914e50a9e8763c3a06d408"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("payload/rates_yesterday.json"))
    );

    ArrayList<Gifs.Urls> rich = giphyClient
        .getGifs("24EoD8JiB5kDh11D2EPreqNIOCmEhMFh", "broke").getData();

    assertTrue(rich.containsAll(asList(new Gifs.Urls("5885nYOgBHdCw"))));
    // The size of the list in giphy.json is equal to 1.
    // So here the verification takes place that the data are from local
    // file rather than from the actual API.
    assertEquals(1, rich.size());

    ResponseEntity<byte[]> gif = giphyService.getGif("AUD", LocalDate.of(2021, 9, 8));

    assertEquals(200, gif.getStatusCodeValue());
    assertEquals("image/gif", gif.getHeaders().get("Content-Type").get(0));
    assertEquals(-60, gif.getBody()[100]);
    assertEquals(-19, gif.getBody()[150]);
  }
}