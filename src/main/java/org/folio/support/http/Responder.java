package org.folio.support.http;

import static io.vertx.core.Future.succeededFuture;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class Responder {
  public void respondWith(
    Handler<AsyncResult<Response>> asyncResultHandler,
    Response response) {

    asyncResultHandler.handle(succeededFuture(response));
  }
}
