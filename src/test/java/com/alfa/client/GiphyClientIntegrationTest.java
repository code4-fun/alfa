package com.alfa.client;

import static com.alfa.client.GiphyMocks.setupMockGiphyResponse;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alfa.model.Gifs;
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
public class GiphyClientIntegrationTest {
  @Autowired
  @Qualifier("mockGiphyService")
  private WireMockServer mockGiphyService;

  @Autowired
  private GiphyClient giphyClient;

  @BeforeEach
  void setUp() throws IOException {
    setupMockGiphyResponse(mockGiphyService);
  }

  @Test
  public void whenGetGifsThenGifsShouldBeReturned() {
    assertFalse(giphyClient.getGif(null, null).getData().isEmpty());
  }

  @Test
  public void whenGetGifsThenTheCorrectGifsShouldBeReturned() {
    assertTrue(giphyClient.getGif(null, null).getData()
        .containsAll(asList(
            new Gifs.Urls("5885nYOgBHdCw"),
            new Gifs.Urls("lptjRBxFKCJmFoibP3"),
            new Gifs.Urls("LdOyjZ7io5Msw"),
            new Gifs.Urls("l3V0B6ICVWbg8Xi5q"),
            new Gifs.Urls("l1J9EZEsT79Bbe16E"),
            new Gifs.Urls("YTRUPHI7fXK6s")
        ))
    );
  }
}