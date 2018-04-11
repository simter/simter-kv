package tech.simter.kv.rest.webflux.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.contentType
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Mono
import tech.simter.kv.po.KeyValue
import tech.simter.kv.service.KeyValueService

/**
 * The [HandlerFunction] for save or update a key-value pair..
 *
 * @author RJ
 */
@Component
class SaveKeyValueHandler @Autowired constructor(
  private val service: KeyValueService
) : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    val dto = request.bodyToMono<KeyValue>().block()!!
    return request.bodyToMono<KeyValue>()
      .flatMap { service.save(dto) }
      .then(ServerResponse.noContent().build())
  }

  /** Default router */
  fun router(): RouterFunction<ServerResponse> {
    return route(POST("/kv").and(contentType(MediaType.APPLICATION_JSON_UTF8)), this)
  }
}