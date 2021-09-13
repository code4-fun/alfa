package com.alfa.controller;

import com.alfa.service.GiphyService;
import java.io.IOException;
import java.time.LocalDate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OnlyController {
  @NonNull
  private final GiphyService giphyService;

  @GetMapping("{currency}")
  public ResponseEntity<byte[]> getGif(@PathVariable("currency") String currency)
      throws IOException {
    return giphyService.getGif(currency, LocalDate.now());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public String return500(Exception ex) {
    return "Something went wrong.";
  }
}