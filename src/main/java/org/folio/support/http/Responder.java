package org.folio.support.http;

import static io.vertx.core.Future.succeededFuture;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class Responder {
  private final Handler<AsyncResult<Response>> handler;

  public Responder(Handler<AsyncResult<Response>> handler) {
    this.handler = handler;
  }

  public void respondWith(Response response) {
    handler.handle(succeededFuture(response));
  }
}
