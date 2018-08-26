package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.model.Records;
import org.folio.rest.jaxrs.resource.ExampleDomainResource;
import org.folio.rest.tools.utils.OutStream;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static io.vertx.core.Future.succeededFuture;

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

    asyncResultHandler.handle(succeededFuture(
      DeleteExampleDomainRecordsResponse.withNoContent()));
  }

  @Override
  public void getExampleDomainRecords(
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>>
      asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(succeededFuture(
      GetExampleDomainRecordsResponse.withJsonOK(
        new Records()
          .withTotalRecords(records.size())
          .withRecords(new ArrayList<>(records.values())))));
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

    //TODO: Generate a location
    asyncResultHandler.handle(succeededFuture(
      PostExampleDomainRecordsResponse.withJsonCreated("", stream)));
  }

  @Override
  public void getExampleDomainRecordsByRecordId(
    String recordId,
    String lang,
    Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    if(records.containsKey(recordId)) {
      asyncResultHandler.handle(succeededFuture(
        GetExampleDomainRecordsByRecordIdResponse.withJsonOK(
          records.get(recordId))));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        notFoundResponse(GetExampleDomainRecordsByRecordIdResponse::withPlainNotFound)));
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

      asyncResultHandler.handle(succeededFuture(
        DeleteExampleDomainRecordsByRecordIdResponse.withNoContent()));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        notFoundResponse(DeleteExampleDomainRecordsByRecordIdResponse::withPlainNotFound)));
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
      asyncResultHandler.handle(succeededFuture(
        PutExampleDomainRecordsByRecordIdResponse.withNoContent()));
    }
    else {
      asyncResultHandler.handle(succeededFuture(
        notFoundResponse(PutExampleDomainRecordsByRecordIdResponse::withPlainNotFound)));
    }
  }

  private Response notFoundResponse(Function<String, Response> notFoundResponseFactory) {
    return notFoundResponseFactory.apply("Not Found");
  }
}
