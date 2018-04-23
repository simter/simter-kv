package tech.simter.kv.rest.webflux.handler

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Mono
import tech.simter.kv.rest.webflux.handler.DeleteKeyValueHandler.Companion.REQUEST_PREDICATE
import tech.simter.kv.service.KeyValueService
import java.util.*

/**
 * @author RJ
 */
@SpringJUnitConfig(DeleteKeyValueHandler::class)
@EnableWebFlux
@MockBean(KeyValueService::class)
class DeleteKeyValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  handler: DeleteKeyValueHandler
) {
  private val client = bindToRouterFunction(route(REQUEST_PREDICATE, handler)).build()

  @Test
  fun deleteOne() {
    // mock
    val key = UUID.randomUUID().toString()
    `when`(service.delete(key)).thenReturn(Mono.empty())

    // invoke
    client.delete().uri("/$key").exchange().expectStatus().isNoContent

    // verify
    verify(service).delete(key)
  }

  @Test
  fun deleteMulti() {
    // mock
    val keyList = (1..3).map { "k-$it" }
    val keyArray = keyList.toTypedArray()
    `when`(service.delete(*keyArray)).thenReturn(Mono.empty())

    // invoke
    client.delete().uri("/${keyList.joinToString(",")}").exchange().expectStatus().isNoContent

    // verify
    verify(service).delete(*keyArray)
  }
}