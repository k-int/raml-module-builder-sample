package org.folio.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.specification.RequestSpecification;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.folio.support.OkapiHeader.REQUEST_ID;
import static org.folio.support.OkapiHeader.TENANT;
import static org.folio.support.OkapiHeader.TOKEN;
import static org.folio.support.OkapiHeader.USER_ID;

public class RestAssuredClient {
  private final String tenantId;
  private final String userId;
  private final String token;
  private final UrlMaker urlMaker;

  public RestAssuredClient(
    String tenantId,
    String userId,
    String token,
    UrlMaker urlMaker) {
    this.tenantId = tenantId;
    this.userId = userId;
    this.token = token;
    this.urlMaker = urlMaker;
  }

  public Response getById(UUID id) throws MalformedURLException {
    return from(get(urlMaker.combine(String.format("/%s", id))));
  }

  private io.restassured.response.Response get(URL url) {
    return given()
      .log().all()
      .spec(defaultHeaders("from-tests"))
      .spec(timeoutConfig())
      .when().get(url)
      .then()
      .log().all()
      .extract().response();
  }

  private RequestSpecification defaultHeaders(String requestId) {
    return new RequestSpecBuilder()
      .addHeader(TENANT.getValue(), tenantId)
      .addHeader(TOKEN.getValue(), token)
      .addHeader(USER_ID.getValue(), userId)
      .addHeader(REQUEST_ID.getValue(), requestId)
      .setAccept("application/json, text/plain")
      .setContentType("application/json")
      .build();
  }

  private RequestSpecification timeoutConfig() {
    return new RequestSpecBuilder()
      .setConfig(RestAssured.config()
        .httpClient(HttpClientConfig.httpClientConfig()
          .setParam("http.connection.timeout", 5000)
          .setParam("http.socket.timeout", 5000))).build();
  }

  private Response from(io.restassured.response.Response response) {
    return new Response(response.statusCode(), response.body().print());
  }
}
