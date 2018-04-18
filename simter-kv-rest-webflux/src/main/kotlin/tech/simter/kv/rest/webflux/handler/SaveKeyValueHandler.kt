package tech.simter.kv.rest.webflux.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.contentType
import reactor.core.publisher.Mono
import tech.simter.kv.service.KeyValueService

/**
 * The [HandlerFunction] for save or update key-value pairs.
 *
 * If the key is exists update its value, otherwise create a ney key-value pair.
 *
 * Request:
 *
 * ```
 * POST /
 * Content-Type : application/json;charset=utf-8
 *
 * {key1: value1, ...}
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
class SaveKeyValueHandler @Autowired constructor(
  private val service: KeyValueService
) : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono<Map<String, String>>() // {k1: v1, ...}
      .flatMap {
        when {
          it.isEmpty() -> Mono.empty<Void>()
          else -> service.save(it)
        }.then()
      }
      .then(ServerResponse.noContent().build())
  }

  companion object {
    /** The default [RequestPredicate] */
    val REQUEST_PREDICATE: RequestPredicate = POST("/").and(contentType(APPLICATION_JSON))
  }
}