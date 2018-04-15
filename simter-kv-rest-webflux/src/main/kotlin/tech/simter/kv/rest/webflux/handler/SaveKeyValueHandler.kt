package tech.simter.kv.rest.webflux.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.contentType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.po.KeyValue
import tech.simter.kv.service.KeyValueService

/**
 * The [HandlerFunction] for save or update a key-value pair.
 *
 *
 *
 * @author RJ
 */
@Component
class SaveKeyValueHandler @Autowired constructor(
  private val service: KeyValueService
) : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    return request.bodyToMono<Map<String, String>>() // {k1: v1, ...}
      .map { it.toList().map { KeyValue(it.first, it.second) } }
      .flatMap {
        when {
          it.isEmpty() -> Mono.empty<Void>()
          it.size == 1 -> service.save(it[0])
          else -> {
            val kvs = Flux.fromIterable(it)
            service.saveAll(kvs)
          }
        }.then()
      }
      .then(ServerResponse.noContent().build())
  }

  companion object {
    /** The default [RequestPredicate] */
    val REQUEST_PREDICATE: RequestPredicate = POST("/").and(contentType(APPLICATION_JSON))
  }
}