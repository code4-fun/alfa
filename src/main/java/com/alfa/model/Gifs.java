package com.alfa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Gifs {
  private ArrayList<Urls> data;

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Urls{
    private String id;
  }
}