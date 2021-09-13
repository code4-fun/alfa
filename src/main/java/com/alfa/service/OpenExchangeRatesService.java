package com.alfa.service;

import com.alfa.client.OpenExchangeRatesClient;
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
  private final OpenExchangeRatesClient openExchangeRatesClient;

  @Value("${opposite_currency}")
  private String opposite_currency;

  @Value("${rates_app_id}")
  private String app_id;

  /**
   * Determines if the exchange rate of the given currency on given date against
   * the opposite_currency set up in the applcation.yml file is higher than the
   * yesterday's exchange rate.
   * @param currency currency under consideration (3-letter string, for instance: "AUD")
   *                provided in the URL.
   * @param date date
   * @return "rich" if today's currency rate is higher than the yesterday's rate,
   * "broke" otherwise.
   */
  public String brokeOrRich(String currency, LocalDate date) {
    Double currencyRateToday = openExchangeRatesClient
        .getRateOnDate(date.toString(), app_id)
        .getRates()
        .get(currency);

    Double oppositeCurrencyRateToday = openExchangeRatesClient
        .getRateOnDate(date.toString(), app_id)
        .getRates()
        .get(opposite_currency);

    Double currencyRateYesterday = openExchangeRatesClient
        .getRateOnDate(date.minus(1, ChronoUnit.DAYS).toString(), app_id)
        .getRates()
        .get(currency);

    Double oppositeCurrencyRateYesterday = openExchangeRatesClient
        .getRateOnDate(date.minus(1, ChronoUnit.DAYS).toString(), app_id)
        .getRates()
        .get(opposite_currency);

    return (oppositeCurrencyRateToday/currencyRateToday >
        oppositeCurrencyRateYesterday/currencyRateYesterday) ? "rich" : "broke";
  }
}