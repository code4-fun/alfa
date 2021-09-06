package com.alfa.client;

import com.alfa.model.ExchangeRates;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value="open-exchange-rates-client", url = "${rates_url}")
public interface OpenExchangeRatesClient {
  @GetMapping(value = "{date}.json", params = "app_id", produces = "application/json")
  ExchangeRates getRateOnDate(@PathVariable("date") String date, @RequestParam("app_id") String app_id);
}