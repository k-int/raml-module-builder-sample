package org.folio.rest.impl;

import io.vertx.core.json.JsonObject;
import org.folio.support.Response;
import org.folio.support.RestAssuredClient;
import org.folio.support.RestVerticleDeployer;
import org.folio.support.VertxAssistant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.folio.support.InterfaceUrls.Records;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

  @Test
  public void shouldBeAbleToCreateRecord() throws MalformedURLException {
    final Response response = client.postToCreate(new JsonObject()
      .put("name", "Example Record"));

    final JsonObject createdRecord = response.getBodyAsJson();

    assertThat(createdRecord.getString("id"), is(notNullValue()));
    assertThat(createdRecord.getString("name"), is("Example Record"));
  }

  @Test
  public void shouldBeAbleToGetCreatedRecord() throws MalformedURLException {
    final UUID id = UUID.randomUUID();

    final Response response = client.getById(id);

    assertThat(response.getBodyAsJson().getString("id"), is(id.toString()));
    assertThat(response.getBodyAsJson().getString("name"), is("Example Record"));
  }
}
