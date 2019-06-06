package tech.simter.kv.rest.webflux.handler

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Mono
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.rest.webflux.handler.SaveKeyValueHandler.Companion.REQUEST_PREDICATE
import java.util.*
import javax.json.Json

/**
 * @author RJ
 */
@SpringJUnitConfig(SaveKeyValueHandler::class)
@EnableWebFlux
@MockkBean(KeyValueService::class)
class SaveKeyValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  handler: SaveKeyValueHandler
) {
  private val client = bindToRouterFunction(route(REQUEST_PREDICATE, handler)).build()

  @Test
  fun saveNone() {
    // invoke
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .syncBody("{}")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(exactly = 0) { service.save(any()) }
  }

  @Test
  fun saveOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = "v"
    val kv = mapOf(key to value)
    every { service.save(kv) } returns Mono.empty()

    // invoke
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .syncBody("{\"$key\":\"$value\"}")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(exactly = 1) { service.save(kv) }
  }

  @Test
  fun saveMulti() {
    // mock
    val kvs = (1..3).associate { "k-$it" to "v-$it" }
    val kvJson = Json.createObjectBuilder()
    kvs.forEach { kvJson.add(it.key, it.value) }
    every { service.save(any()) } returns Mono.empty()

    // invoke
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .syncBody(kvJson.build().toString())
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(exactly = 1) { service.save(any()) }
  }
}