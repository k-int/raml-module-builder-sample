package org.folio.support;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Response {
  private final Integer statusCode;
  private final String body;
  private final String location;

  Response(Integer statusCode, String body) {
    this(statusCode, body, null);
  }

  Response(Integer statusCode, String body, String location) {
    this.statusCode = statusCode;
    this.body = body;
    this.location = location;
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

  public String getLocation() {
    return location;
  }
}
