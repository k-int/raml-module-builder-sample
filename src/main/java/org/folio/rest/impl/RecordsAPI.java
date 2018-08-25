package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.resource.RecordsResource;
import org.folio.rest.tools.utils.OutStream;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.vertx.core.Future.succeededFuture;

public class RecordsAPI implements RecordsResource {
  private static final Map<String, Record> records = new HashMap<>();

  @Override
  public void getRecords(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>>
      asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(succeededFuture(notImplemented()));
  }

  @Override
  public void postRecords(
    String lang,
    Record entity,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    //TODO: Allow creation with client supplied ID
    entity.setId(UUID.randomUUID().toString());

    records.put(entity.getId(), entity);

    OutStream stream = new OutStream();
    stream.setData(entity);

    //TODO: Generate a location
    asyncResultHandler.handle(succeededFuture(
      PostRecordsResponse.withJsonCreated("", stream)));
  }

  @Override
  public void getRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    if(records.containsKey(recordId)) {
      asyncResultHandler.handle(succeededFuture(
        GetRecordsByRecordIdResponse.withJsonOK(
          records.get(recordId))));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        GetRecordsByRecordIdResponse.withPlainNotFound("Not Found")));
    }
  }

  @Override
  public void deleteRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(succeededFuture(notImplemented()));
  }

  @Override
  public void putRecordsByRecordId(
    String recordId,
    String lang,
    Record entity,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(succeededFuture(notImplemented()));
  }

  private Response notImplemented() {
    return Response.status(501)
      .header("Content-Type", "text/plain")
      .entity("Not Implemented")
      .build();
  }
}
