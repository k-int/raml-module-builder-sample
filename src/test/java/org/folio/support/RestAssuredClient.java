package org.folio.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.json.JsonObject;

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

  public Response get(UUID id) throws MalformedURLException {
    return from(
      get(individualRecordUrl(id))
        .statusCode(200)
        .extract().response());
  }

  public Response attemptGet(UUID id) throws MalformedURLException {
    return from(
      get(individualRecordUrl(id))
        .extract().response());
  }

  public Response postToCreate(JsonObject representation) throws MalformedURLException {
    return from(post(rootUrl(), representation)
      .statusCode(201)
      .extract().response());
  }

  public Response attemptPostToCreate(JsonObject representation) throws MalformedURLException {
    return from(post(rootUrl(), representation)
      .extract().response());
  }

  public Response delete(UUID id) throws MalformedURLException {
    return from(delete(individualRecordUrl(id))
      .statusCode(204)
      .extract().response());
  }

  public Response attemptDelete(UUID id) throws MalformedURLException {
    return from(delete(individualRecordUrl(id))
      .extract().response());
  }

  public Response delete() throws MalformedURLException {
    return from(delete(rootUrl())
      .statusCode(204)
      .extract().response());
  }

  public Response get() throws MalformedURLException {
    return from(
      get(urlMaker.combine(""))
        .statusCode(200)
        .extract().response());
  }

  public Response replace(UUID id, JsonObject representation)
    throws MalformedURLException {

    return from(put(individualRecordUrl(id), representation)
      .statusCode(204)
      .extract().response());
  }

  public Response attemptReplace(UUID id, JsonObject representation)
    throws MalformedURLException {

      return from(put(individualRecordUrl(id), representation)
        .extract().response());
  }

  private ValidatableResponse post(
    URL url,
    JsonObject representation) {

    return given()
      .log().all()
      .spec(defaultHeaders())
      .spec(timeoutConfig())
      .body(getBody(representation))
      .when().post(url)
      .then()
      .log().all();
  }

  private ValidatableResponse put(URL url, JsonObject representation) {
    return given()
      .log().all()
      .spec(defaultHeaders())
      .spec(timeoutConfig())
      .body(getBody(representation))
      .when().put(url)
      .then()
      .log().all();
  }

  private ValidatableResponse delete(URL url) {
    return given()
      .log().all()
      .spec(defaultHeaders())
      .spec(timeoutConfig())
      .when().delete(url)
      .then()
      .log().all();
  }

  private ValidatableResponse get(URL url) {
    return given()
      .log().all()
      .spec(defaultHeaders())
      .spec(timeoutConfig())
      .when().get(url)
      .then()
      .log().all();
  }

  private RequestSpecification defaultHeaders() {
    return new RequestSpecBuilder()
      .addHeader(TENANT.getValue(), tenantId)
      .addHeader(TOKEN.getValue(), token)
      .addHeader(USER_ID.getValue(), userId)
      .addHeader(REQUEST_ID.getValue(), "from-tests")
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
    return new Response(
      response.statusCode(),
      response.body().print(),
      response.header("location"));
  }

  private URL rootUrl() throws MalformedURLException {
    return urlMaker.combine("");
  }

  private URL individualRecordUrl(UUID id) throws MalformedURLException {
    return urlMaker.combine(String.format("/%s", id));
  }

  private String getBody(JsonObject representation) {
    if(representation == null) return "";

    return representation.encodePrettily();
  }
}
