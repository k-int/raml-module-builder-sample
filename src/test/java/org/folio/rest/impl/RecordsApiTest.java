package org.folio.rest.impl;

import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.support.Response;
import org.folio.support.RestAssuredClient;
import org.folio.support.RestVerticleDeployer;
import org.folio.support.UrlMaker;
import org.folio.support.VertxAssistant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordsApiTest {
  private static final String TENANT_ID = "test_tenant";
  private static final String USER_ID = "762ce50c-8d96-41d5-a5f5-e27780008a83";
  private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9eyJzdWIiOiJhZG1pbiIsInVzZXJfaWQiOiI3NjJjZTUwYy04ZDk2LTQxZDUtYTVmNS1lMjc3ODAwMDhhODMiLCJ0ZW5hbnQiOiJ0ZXN0X3RlbmFudCJ9BShwfHcKW3llcnwiZBMxC0AzWxASeGx9Y2ZcaxhUOm5oM3dFVD0=";

  private static final VertxAssistant vertxAssistant = new VertxAssistant();
  private static final Integer MODULE_PORT = NetworkUtils.nextFreePort();
  private static final RestVerticleDeployer deployer = new RestVerticleDeployer(
    vertxAssistant, MODULE_PORT);

  @BeforeClass
  public static void beforeAll()
    throws InterruptedException,
    ExecutionException,
    TimeoutException {

    vertxAssistant.start();

    deployer.deploy()
      .get(20, TimeUnit.SECONDS);
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
  public void shouldBeAbleToGetARecord() throws MalformedURLException {

    final RestAssuredClient client = new RestAssuredClient(TENANT_ID, USER_ID,
      TOKEN, recordsUrlMaker());

    final UUID id = UUID.randomUUID();

    final Response response = client.getById(id);

    assertThat(response.getStatusCode(), is(200));
    assertThat(response.getBodyAsJson().getString("id"), is(id.toString()));
    assertThat(response.getBodyAsJson().getString("name"), is("Example Record"));
  }

  private UrlMaker recordsUrlMaker() {
    return path -> new URL(
      String.format("http://localhost:%s/records%s",
        MODULE_PORT.toString(),
        path));
  }
}
