package org.folio.rest.impl;

import io.vertx.core.json.JsonObject;
import org.folio.support.Response;
import org.folio.support.RestAssuredClient;
import org.folio.support.RestVerticleDeployer;
import org.folio.support.VertxAssistant;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.folio.support.InterfaceUrls.Records;
import static org.folio.support.JsonArrayHelper.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

//TODO: Tests for validation
public class RecordsApiTest {
  private static final String TENANT_ID = "test_tenant";
  private static final String USER_ID = "762ce50c-8d96-41d5-a5f5-e27780008a83";
  private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9eyJzdWIiOiJhZG1pbiIsInVzZXJfaWQiOiI3NjJjZTUwYy04ZDk2LTQxZDUtYTVmNS1lMjc3ODAwMDhhODMiLCJ0ZW5hbnQiOiJ0ZXN0X3RlbmFudCJ9BShwfHcKW3llcnwiZBMxC0AzWxASeGx9Y2ZcaxhUOm5oM3dFVD0=";

  private static final VertxAssistant vertxAssistant = new VertxAssistant();
  private static final RestVerticleDeployer deployer = new RestVerticleDeployer(
    vertxAssistant);
  private static RestAssuredClient client;

  @BeforeClass
  public static void beforeAll()
    throws InterruptedException,
    ExecutionException,
    TimeoutException {

    vertxAssistant.start();

    deployer.deploy()
      .get(20, TimeUnit.SECONDS);

    client = new RestAssuredClient(TENANT_ID, USER_ID, TOKEN,
      Records.urlsBasedUpon(deployer.getLocation()));
  }

  @AfterClass
  public static void afterAll()
    throws InterruptedException,
    ExecutionException,
    TimeoutException {

    deployer.undeploy()
      .thenApplyAsync(v -> vertxAssistant.stop())
      .get(20, TimeUnit.SECONDS);
  }

  @Before
  public void beforeEach() throws MalformedURLException {
    client.delete();
  }

  @Test
  public void shouldBeAbleToCreateRecord() throws MalformedURLException {
    final Response response = client.postToCreate(exampleRecord());

    final JsonObject createdRecord = response.getBodyAsJson();

    assertThat(createdRecord.getString("id"), is(notNullValue()));
    assertThat(createdRecord.getString("name"), is("Example Record"));
  }

  @Test
  public void shouldBeAbleToCreateRecordWithId() throws MalformedURLException {
    final UUID providedId = UUID.randomUUID();

    final Response response = client.postToCreate(
      new JsonObject()
        .put("id", providedId.toString())
        .put("name", "Example Record"));

    final JsonObject createdRecord = response.getBodyAsJson();

    assertThat(createdRecord.getString("id"), is(providedId.toString()));
    assertThat(createdRecord.getString("name"), is("Example Record"));
  }

  @Test
  public void shouldBeAbleToGetCreatedRecord() throws MalformedURLException {
    final Response createResponse = client.postToCreate(exampleRecord());

    final UUID id = createResponse.getId();

    final Response response = client.get(id);

    final JsonObject createdRecord = response.getBodyAsJson();

    assertThat(createdRecord.getString("id"), is(id.toString()));
    assertThat(createdRecord.getString("name"), is("Example Record"));
  }

  @Test
  public void shouldNotBeAbleToGetUnknownRecord() throws MalformedURLException {
    final UUID id = UUID.randomUUID();

    final Response getResponse = client.attemptGet(id);

    assertThat(getResponse.getStatusCode(), is(404));
    assertThat(getResponse.getBody(), is("Not Found"));
  }

  @Test
  public void shouldBeAbleToGetAllRecords() throws MalformedURLException {
    client.postToCreate(exampleRecord("First record"));
    client.postToCreate(exampleRecord("Second record"));
    client.postToCreate(exampleRecord("Third record"));

    final Response response = client.get();

    final JsonObject wrappedRecords = response.getBodyAsJson();

    assertThat(wrappedRecords.getInteger("totalRecords"), is(3));

    final List<JsonObject> records = toList(wrappedRecords, "records");

    assertThat(records.size(), is(3));

    records.forEach(record -> {
      assertThat("Should have id property", record.containsKey("id"), is(true));
      assertThat("id property should not be empty", record.getString("id"), is(notNullValue()));
    });

    final List<String> names = records.stream()
      .map(record -> record.getString("name"))
      .collect(Collectors.toList());

    assertThat(names, containsInAnyOrder(
      "First record",
      "Second record",
      "Third record"));

  }

  @Test
  public void shouldBeAbleToDeleteRecord() throws MalformedURLException {
    final Response createResponse = client.postToCreate(exampleRecord());

    final UUID id = createResponse.getId();

    client.delete(id);
  }

  @Test
  public void shouldNotBeAbleToGetDeletedRecord() throws MalformedURLException {
    final Response createResponse = client.postToCreate(exampleRecord());

    final UUID id = createResponse.getId();

    client.delete(id);

    final Response getResponse = client.attemptGet(id);

    assertThat(getResponse.getStatusCode(), is(404));
    assertThat(getResponse.getBody(), is("Not Found"));
  }

  @Test
  public void shouldBeAbleToAttemptToDeleteUnknownRecord() throws MalformedURLException {
    final UUID id = UUID.randomUUID();

    final Response deleteResponse = client.attemptDelete(id);

    assertThat(deleteResponse.getStatusCode(), is(404));
    assertThat(deleteResponse.getBody(), is("Not Found"));
  }

  private JsonObject exampleRecord() {
    return exampleRecord("Example Record");
  }

  private JsonObject exampleRecord(String name) {
    return new JsonObject()
      .put("name", name);
  }
}
