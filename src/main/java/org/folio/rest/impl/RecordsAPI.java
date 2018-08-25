package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.model.Records;
import org.folio.rest.jaxrs.resource.RecordsResource;
import org.folio.rest.tools.utils.OutStream;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.vertx.core.Future.succeededFuture;

//TODO: Support multiple tenants
public class RecordsAPI implements RecordsResource {
  private static final Map<String, Record> records = new HashMap<>();

  @Override
  public void deleteRecords(
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    records.clear();

    asyncResultHandler.handle(succeededFuture(
      DeleteRecordsResponse.withNoContent()));
  }

  @Override
  public void getRecords(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>>
      asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(succeededFuture(
      GetRecordsResponse.withJsonOK(
        new Records()
          .withTotalRecords(records.size())
          .withRecords(new ArrayList<>(records.values())))));
  }

  @Override
  public void postRecords(
    String lang,
    Record entity,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    if(entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

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

    if(records.containsKey(recordId)) {
      records.remove(recordId);

      asyncResultHandler.handle(succeededFuture(
        DeleteRecordsByRecordIdResponse.withNoContent()));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        DeleteRecordsByRecordIdResponse.withPlainNotFound("Not Found")));
    }
  }

  @Override
  public void putRecordsByRecordId(
    String recordId,
    String lang,
    Record entity,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    //TODO: Validate that ID in representation matches URL parameter

    if(records.containsKey(recordId)) {
      records.replace(recordId, entity);
      asyncResultHandler.handle(succeededFuture(
        PutRecordsByRecordIdResponse.withNoContent()));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        PutRecordsByRecordIdResponse.withPlainNotFound("Not Found")));
    }
  }
}
