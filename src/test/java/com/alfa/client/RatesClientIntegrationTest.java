package com.alfa.client;

import static com.alfa.client.RatesMocks.setupMockRatesResponse;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WireMockConfig.class })
public class RatesClientIntegrationTest {
  @Autowired
  @Qualifier("mockRatesService")
  private WireMockServer mockRatesService;

  @Autowired
  private OpenExchangeRatesClient openExchangeRatesClient;

  @BeforeEach
  void setUp() throws IOException {
    setupMockRatesResponse(mockRatesService);
  }

  @Test
  public void whenGetRatesThenRatesShouldBeReturned() {
    assertFalse(openExchangeRatesClient.getRateOnDate(null, null).getRates().isEmpty());
  }

  @Test
  public void whenGetRatesThenTheCorrectRatesShouldBeReturned() {
    assertTrue(openExchangeRatesClient.getRateOnDate(null, null).getRates().keySet()
        .containsAll(asList(
            "AUD", "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS"
        ))
    );
  }
}