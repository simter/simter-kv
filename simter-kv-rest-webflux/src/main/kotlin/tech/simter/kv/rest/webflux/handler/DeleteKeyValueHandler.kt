package tech.simter.kv.rest.webflux.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates.DELETE
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import tech.simter.kv.service.KeyValueService

/**
 * The [HandlerFunction] for delete single key-value pair or multiple key-value pairs.
 * Multiple keys combine with comma. The key could not contains comma symbol `','`.
 *
 * Request:
 *
 * ```
 * DELETE {context-path}/{key}
 * ```
 *
 * Response:
 *
 * ```
 * 204 No Content
 * ```
 *
 * @author RJ
 */
@Component
class DeleteKeyValueHandler @Autowired constructor(
  private val service: KeyValueService
) : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    val keys = request.pathVariable("key").split(",")
    return service.delete(*keys.toTypedArray())
      .then(ServerResponse.noContent().build())
  }

  companion object {
    /** The default [RequestPredicate] */
    val REQUEST_PREDICATE: RequestPredicate = DELETE("/{key}")
  }
}