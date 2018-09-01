package org.folio.rest.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.ws.rs.core.Response;

import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.model.Records;
import org.folio.rest.jaxrs.resource.ExampleDomainResource;
import org.folio.rest.tools.utils.OutStream;
import org.folio.support.http.Responder;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

//TODO: Support multiple tenants
public class RecordsAPI implements ExampleDomainResource {
  private static final Map<String, Record> records = new HashMap<>();

  @Override
  public void deleteExampleDomainRecords(
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    records.clear();

    respondWith(asyncResultHandler, DeleteExampleDomainRecordsResponse.withNoContent());
  }

  @Override
  public void getExampleDomainRecords(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>>
      asyncResultHandler,
    Context vertxContext) {

    respondWith(asyncResultHandler, GetExampleDomainRecordsResponse.withJsonOK(
      new Records()
        .withTotalRecords(records.size())
        .withRecords(new ArrayList<>(records.values()))));
  }

  @Override
  public void postExampleDomainRecords(
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

    respondWith(asyncResultHandler, PostExampleDomainRecordsResponse.withJsonCreated(
      String.format("example-domain/records/%s", entity.getId()),
      stream));
  }

  @Override
  public void getExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    if(records.containsKey(recordId)) {
      respondWith(asyncResultHandler, GetExampleDomainRecordsByRecordIdResponse.withJsonOK(
        records.get(recordId)));
    }
    else {
      respondWith(asyncResultHandler, notFoundResponse(GetExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  @Override
  public void deleteExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    if(records.containsKey(recordId)) {
      records.remove(recordId);

      respondWith(asyncResultHandler, DeleteExampleDomainRecordsByRecordIdResponse.withNoContent());
    }
    else {
      respondWith(asyncResultHandler, notFoundResponse(DeleteExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  @Override
  public void putExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Record entity,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    //TODO: Validate that ID in representation matches URL parameter
    if(records.containsKey(recordId)) {
      records.replace(recordId, entity);
      respondWith(asyncResultHandler, PutExampleDomainRecordsByRecordIdResponse.withNoContent());
    }
    else {
      respondWith(asyncResultHandler, notFoundResponse(PutExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  private void respondWith(
    Handler<AsyncResult<Response>> asyncResultHandler,
    Response response) {

    new Responder(asyncResultHandler).respondWith(response);
  }

  private Response notFoundResponse(Function<String, Response> notFoundResponseFactory) {
    return notFoundResponseFactory.apply("Not Found");
  }
}
