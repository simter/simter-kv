package tech.simter.kv.rest.webflux.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import tech.simter.kv.service.KeyValueService

/**
 * The [HandlerFunction] for get single key value or multi key values. Multi key should be combined with comma.
 *
 * Request:
 *
 * ```
 * GET {context-path}/{key}
 * ```
 *
 * Response: (if at lease one key exists)
 *
 * ```
 * 200 OK
 * Content-Type: application/json;charset=UTF-8
 *
 * {key1: value1, ...}
 * ```
 *
 * Response: (if all key not exists)
 *
 * ```
 * 204 NO_CONTENT
 * ```
 *
 * @author RJ
 */
@Component
class FindKeyValueHandler @Autowired constructor(
  private val service: KeyValueService
) : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    val keys = request.pathVariable("key").split(",")
    return (
      if (keys.size == 1) service.getValue(keys[0]).map { mapOf(keys[0] to it) }        // {key: value}
      else service.findAll(*keys.toTypedArray()).collectMap({ it.key }, { it.value })  // {key1: value1, ...}
      )
      .flatMap({
        ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .syncBody(it)
      })
      .switchIfEmpty(ServerResponse.noContent().build())
  }

  companion object {
    /** The default [RequestPredicate] */
    val REQUEST_PREDICATE: RequestPredicate = GET("/{key}")
  }
}