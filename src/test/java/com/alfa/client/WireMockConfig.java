package com.alfa.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {
  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer mockGiphyService() {
    return new WireMockServer(9988);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer mockRatesService() {
    return new WireMockServer(9977);
  }
}