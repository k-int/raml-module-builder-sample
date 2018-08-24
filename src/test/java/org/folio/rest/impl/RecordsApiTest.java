package org.folio.rest.impl;

import io.vertx.core.json.JsonObject;
import org.folio.rest.RestVerticle;
import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.support.VertxAssistant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordsApiTest {
  private static final VertxAssistant vertxAssistant = new VertxAssistant();
  private static final Integer MODULE_PORT = NetworkUtils.nextFreePort();
  private static String verticleId;

  @BeforeClass
  public static void beforeAll()
    throws InterruptedException,
    ExecutionException,
    TimeoutException {

    vertxAssistant.start();

    final JsonObject config = new JsonObject().put("http.port", MODULE_PORT);

    verticleId = vertxAssistant.deployVerticle(RestVerticle.class, config)
      .get(20, TimeUnit.SECONDS);
  }

  @AfterClass
  public static void afterAll()
    throws InterruptedException,
    ExecutionException,
    TimeoutException {

    vertxAssistant.undeployVerticle(verticleId)
      .thenApplyAsync(v -> vertxAssistant.stop())
      .get(20, TimeUnit.SECONDS);
  }

  @Test
  public void shouldFail() {
    assertThat(false, is(true));
  }
}
