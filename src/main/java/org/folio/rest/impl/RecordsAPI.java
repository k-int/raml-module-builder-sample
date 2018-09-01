package org.folio.rest.impl;

import static org.folio.support.http.Responder.respondTo;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.core.Response;

import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.model.Records;
import org.folio.rest.jaxrs.resource.ExampleDomainResource;
import org.folio.rest.tools.utils.OutStream;
import org.folio.storage.InMemoryStorage;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

//TODO: Support multiple tenants
public class RecordsAPI implements ExampleDomainResource {
  private static final InMemoryStorage<Record> records
    = new InMemoryStorage<>(Record::getId);

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

    defaultId(entity);

    records.create(entity);

    respondTo(asyncResultHandler).respondWith(
      createdResponse(entity, RecordsAPI::recordLocation,
        PostExampleDomainRecordsResponse::withJsonCreated));
  }

  @Override
  public void getExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    final Response response = records.exists(recordId)
      ? GetExampleDomainRecordsByRecordIdResponse.withJsonOK(records.getById(recordId))
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

    if(records.exists(recordId)) {
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
    if(records.exists(recordId)) {
      records.replace(recordId, entity);

      respondTo(asyncResultHandler).respondWith(
        PutExampleDomainRecordsByRecordIdResponse.withNoContent());
    }
    else {
      respondTo(asyncResultHandler).respondWith(
        notFoundResponse(PutExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  private static Response notFoundResponse(Function<String, Response> responseFactory) {
    return responseFactory.apply("Not Found");
  }

  private static Response createdResponse(
    Record record,
    Function<Record, String> locationFactory,
    BiFunction<String, OutStream, Response> responseFactory) {

    OutStream innerStream = new OutStream();
    innerStream.setData(record);

    final String location = locationFactory.apply(record);

    return responseFactory.apply(location, innerStream);
  }

  private static String recordLocation(Record record) {
    return String.format("example-domain/records/%s", record.getId());
  }

  private void defaultId(Record record) {
    if(record.getId() == null) {
      record.setId(UUID.randomUUID().toString());
    }
  }
}
