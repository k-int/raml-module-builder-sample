package org.folio.support;

import io.vertx.core.json.JsonObject;
import org.folio.rest.RestVerticle;

import java.util.concurrent.CompletableFuture;

public class RestVerticleDeployer {
  private final VertxAssistant vertxAssistant;
  private final Integer httpPort;
  private String deployedVerticleId;

  public RestVerticleDeployer(VertxAssistant vertxAssistant, Integer httpPort) {
    this.vertxAssistant = vertxAssistant;
    this.httpPort = httpPort;
  }

  public CompletableFuture<Void> deploy() {
    final JsonObject config = new JsonObject().put("http.port", httpPort);

    return vertxAssistant.deployVerticle(RestVerticle.class, config)
      .thenApplyAsync(verticleId -> {
        deployedVerticleId = verticleId;
        return null;
      });
  }

  public CompletableFuture<Void> undeploy() {
    return vertxAssistant.undeployVerticle(deployedVerticleId);
  }
}
