package com.alfa.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alfa.client.OpenExchangeRatesClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application-test.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenExchangeRatesServiceTest {
  @Autowired
  private OpenExchangeRatesClient openExchangeRatesClient;

  @Autowired
  private OpenExchangeRatesService openExchangeRatesService;

  private static WireMockServer wireMockServer;

  @DynamicPropertySource
  static void overrideWebClientBaseUrl(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("rates_url", wireMockServer::baseUrl);
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
  void testOpenExchangeClient() {
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

    Map<String, Double> rates_today = openExchangeRatesClient
        .getRateOnDate("2021-09-08", "65e7ecdca7914e50a9e8763c3a06d408").getRates();
    Map<String, Double> rates_yesterday = openExchangeRatesClient
        .getRateOnDate("2021-09-07", "65e7ecdca7914e50a9e8763c3a06d408").getRates();

    assertTrue(rates_today.keySet()
        .containsAll(asList("AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD")));

    assertTrue(rates_yesterday.keySet()
        .containsAll(asList("AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD")));

    // Exchange rates from X to W were removed from the files rates-today.json and
    // rates-yesterday.json but will be in the response from the real API.
    // So here the verification takes place that the rates came from local files rather
    // than from the actual API.
    assertFalse(rates_today.keySet()
        .containsAll(asList("XAF", "XAG", "XAU", "XCD", "XDR", "XOF", "XPD", "XPF",
            "XPT", "YER", "ZAR", "ZMW", "ZWL")));

    assertFalse(rates_yesterday.keySet()
        .containsAll(asList("XAF", "XAG", "XAU", "XCD", "XDR", "XOF", "XPD", "XPF",
            "XPT", "YER", "ZAR", "ZMW", "ZWL")));

    String broke = openExchangeRatesService.brokeOrRich("AUD", LocalDate.of(2021, 9, 8));
    assertEquals("broke", broke);
  }
}