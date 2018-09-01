package org.folio.rest.impl;

import static org.folio.support.http.Responder.respondTo;

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

    respondTo(asyncResultHandler).respondWith(
      DeleteExampleDomainRecordsResponse.withNoContent());
  }

  @Override
  public void getExampleDomainRecords(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>>
      asyncResultHandler,
    Context vertxContext) {

    respondTo(asyncResultHandler).respondWith(
      GetExampleDomainRecordsResponse.withJsonOK(
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

    respondTo(asyncResultHandler).respondWith(
      PostExampleDomainRecordsResponse.withJsonCreated(
        String.format("example-domain/records/%s", entity.getId()), stream));
  }

  @Override
  public void getExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    final Response response = records.containsKey(recordId)
      ? GetExampleDomainRecordsByRecordIdResponse.withJsonOK(records.get(recordId))
      : notFoundResponse(GetExampleDomainRecordsByRecordIdResponse::withPlainNotFound);

    respondTo(asyncResultHandler).respondWith(response);
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

      respondTo(asyncResultHandler).respondWith(
        DeleteExampleDomainRecordsByRecordIdResponse.withNoContent());
    }
    else {

      respondTo(asyncResultHandler).respondWith(
        notFoundResponse(DeleteExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
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

      respondTo(asyncResultHandler).respondWith(
        PutExampleDomainRecordsByRecordIdResponse.withNoContent());
    }
    else {
      respondTo(asyncResultHandler).respondWith(
        notFoundResponse(PutExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  private Response notFoundResponse(Function<String, Response> notFoundResponseFactory) {
    return notFoundResponseFactory.apply("Not Found");
  }
}
