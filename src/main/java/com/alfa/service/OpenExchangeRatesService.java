package com.alfa.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenExchangeRatesService {
  @NonNull
  private final com.alfa.client.OpenExchangeRatesClient openExchangeRatesClient;

  @Value("${rates_app_id}")
  private String app_id;

  @Value("${opposite_currency}")
  private String opposite_currency;

  /**
   * Determines if the today's exchange rate of the given currency against the opposite_currency
   * set up in the applcation.yml file is higher than the yesterday's exchange rate.
   * @param currency currency under consideration (3-letter string, for instance: "AUD") provided
   *                 in the URL.
   * @return "rich" if today's currency rate is higher than the yesterday's rate, "broke" otherwise.
   */
  public String brokeOrRich(String currency) {
    Double currencyRateToday = openExchangeRatesClient
        .getRateOnDate(LocalDate.now().toString(), app_id)
        .getRates()
        .get(currency);

    Double oppositeCurrencyRateToday = openExchangeRatesClient
        .getRateOnDate(LocalDate.now().toString(), app_id)
        .getRates()
        .get(opposite_currency);

    Double currencyRateYesterday = openExchangeRatesClient
        .getRateOnDate(LocalDate.now().minus(1, ChronoUnit.DAYS).toString(), app_id)
        .getRates()
        .get(currency);

    Double oppositeCurrencyRateYesterday = openExchangeRatesClient
        .getRateOnDate(LocalDate.now().minus(1, ChronoUnit.DAYS).toString(), app_id)
        .getRates()
        .get(opposite_currency);

    return (oppositeCurrencyRateToday/currencyRateToday > oppositeCurrencyRateYesterday/currencyRateYesterday) ? "rich" : "broke";
  }
}