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
import org.folio.rest.jaxrs.resource.ExampleDomainResource.DeleteExampleDomainRecordsByRecordIdResponse as DeleteByIdResponse
import org.folio.rest.jaxrs.resource.ExampleDomainResource.DeleteExampleDomainRecordsResponse as DeleteResponse
import org.folio.rest.jaxrs.resource.ExampleDomainResource.GetExampleDomainRecordsByRecordIdResponse as GetByIdResponse
import org.folio.rest.jaxrs.resource.ExampleDomainResource.GetExampleDomainRecordsResponse as GetResponse
import org.folio.rest.jaxrs.resource.ExampleDomainResource.PostExampleDomainRecordsResponse as PostResponse
import org.folio.rest.jaxrs.resource.ExampleDomainResource.PutExampleDomainRecordsByRecordIdResponse as PutResponse

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

    asyncResultHandler?.respond(DeleteResponse.withNoContent())
  }

  override fun getExampleDomainRecords(
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    asyncResultHandler?.respond(GetResponse.withJsonOK(
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

    asyncResultHandler?.respond(PostResponse.withJsonCreated(location, stream))
  }

  override fun getExampleDomainRecordsByRecordId(
    recordId: String?,
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    asyncResultHandler?.respond(when {
        records.containsKey(recordId) -> GetByIdResponse.withJsonOK(records[recordId])
        else -> notFoundResponse(GetByIdResponse::withPlainNotFound)
    })
  }

  override fun deleteExampleDomainRecordsByRecordId(
    recordId: String?,
    lang: String?,
    okapiHeaders: MutableMap<String, String>?,
    asyncResultHandler: Handler<AsyncResult<Response>>?,
    vertxContext: Context?) {

    if(records.containsKey(recordId)) {
      records.remove(recordId)

      asyncResultHandler?.respond(DeleteByIdResponse.withNoContent())
    }
    else {
      asyncResultHandler?.respond(notFoundResponse(DeleteByIdResponse::withPlainNotFound))
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
      asyncResultHandler?.respond(PutResponse.withNoContent())
    }
    else {
      asyncResultHandler?.respond(notFoundResponse(PutResponse::withPlainNotFound));
    }
  }

  private fun notFoundResponse(notFoundResponseFactory: (String) -> Response) =
    notFoundResponseFactory("Not Found")

  private fun Handler<AsyncResult<Response>>.respond(response: Response) =
    this.handle(succeededFuture(response))
}
