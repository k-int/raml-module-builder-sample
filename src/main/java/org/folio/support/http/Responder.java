package org.folio.support.http;

import static io.vertx.core.Future.succeededFuture;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class Responder {
  public static Responder respondTo(Handler<AsyncResult<Response>> handler) {
    return new Responder(handler);
  }

  private final Handler<AsyncResult<Response>> handler;

  private Responder(Handler<AsyncResult<Response>> handler) {
    this.handler = handler;
  }

  public void respondWith(Response response) {
    handler.handle(succeededFuture(response));
  }
}
