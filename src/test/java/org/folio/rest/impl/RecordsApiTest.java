package org.folio.rest.impl;

import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.support.RestVerticleDeployer;
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
  public void shouldFail() {
    assertThat(false, is(true));
  }
}
