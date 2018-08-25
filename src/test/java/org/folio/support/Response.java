package org.folio.support;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Response {
  private final Integer statusCode;
  private final String body;

  Response(Integer statusCode, String body) {
    this.statusCode = statusCode;
    this.body = body;
  }


  public Integer getStatusCode() {
    return statusCode;
  }

  public String getBody() {
    return body;
  }

  public JsonObject getBodyAsJson() {
    return new JsonObject(getBody());
  }

  public UUID getId() {
    return UUID.fromString(getBodyAsJson().getString("id"));
  }
}
