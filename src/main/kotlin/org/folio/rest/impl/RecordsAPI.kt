package org.folio.rest.impl

import io.vertx.core.AsyncResult
import io.vertx.core.Context
import io.vertx.core.Future.succeededFuture
import io.vertx.core.Handler
import org.folio.rest.jaxrs.model.Record
import org.folio.rest.jaxrs.model.Records
import org.folio.rest.jaxrs.resource.ExampleDomainResource
import org.folio.rest.tools.utils.OutStream
import java.util.*
import javax.ws.rs.core.Response

//TODO: Support multiple tenants
class RecordsApi : ExampleDomainResource {
  companion object {
    val records = mutableMapOf<String, Record>()
  }

  override fun deleteExampleDomainRecords(
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    records.clear()

    asyncResultHandler?.respond(
      ExampleDomainResource.DeleteExampleDomainRecordsResponse.withNoContent())
  }

  override fun getExampleDomainRecords(
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    asyncResultHandler?.respond(
      ExampleDomainResource.GetExampleDomainRecordsResponse.withJsonOK(
        Records()
          .withTotalRecords(records.size)
          .withRecords(ArrayList(records.values))))
  }

  override fun postExampleDomainRecords(
    lang: String?,
    entity: Record?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    entity?.id = entity?.id ?: UUID.randomUUID().toString()

    records[entity?.id!!] = entity

    val stream = OutStream()
    stream.data = entity

    val location = "example-domain/records/${entity.id}"

    asyncResultHandler?.respond(
      ExampleDomainResource.PostExampleDomainRecordsResponse
        .withJsonCreated(location, stream))
  }

  override fun getExampleDomainRecordsByRecordId(
    recordId: String?,
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    if(records.containsKey(recordId)) {
      asyncResultHandler?.respond(
        ExampleDomainResource.GetExampleDomainRecordsByRecordIdResponse
          .withJsonOK(records[recordId]))
    }
    else {
      asyncResultHandler?.respond(notFoundResponse(
        ExampleDomainResource.GetExampleDomainRecordsByRecordIdResponse::withPlainNotFound))
    }
  }

  override fun deleteExampleDomainRecordsByRecordId(
    recordId: String?,
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    if(records.containsKey(recordId)) {
      records.remove(recordId)

      asyncResultHandler?.respond(
        ExampleDomainResource.DeleteExampleDomainRecordsByRecordIdResponse
          .withNoContent())
    }
    else {
      asyncResultHandler?.respond(notFoundResponse(
        ExampleDomainResource.DeleteExampleDomainRecordsByRecordIdResponse::withPlainNotFound))
    }
  }

  override fun putExampleDomainRecordsByRecordId(
    recordId: String?,
    lang: String?,
    entity: Record?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    //TODO: Validate that ID in representation matches URL parameter
    if(records.containsKey(recordId)) {
      records.replace(recordId!!, entity!!);
      asyncResultHandler?.respond(
        ExampleDomainResource.PutExampleDomainRecordsByRecordIdResponse
          .withNoContent())
    }
    else {
      asyncResultHandler?.respond(notFoundResponse(
          ExampleDomainResource.PutExampleDomainRecordsByRecordIdResponse::withPlainNotFound));
    }
  }

  private fun notFoundResponse(notFoundResponseFactory: (String) -> Response) =
    notFoundResponseFactory("Not Found")

  private fun Handler<AsyncResult<Response>>.respond(response: Response) =
    this.handle(succeededFuture(response))
}
