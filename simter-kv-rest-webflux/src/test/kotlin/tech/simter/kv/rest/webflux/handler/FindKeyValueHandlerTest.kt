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
import tech.simter.kv.core.KeyValue
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.rest.webflux.handler.FindKeyValueHandler.Companion.REQUEST_PREDICATE
import java.util.*
import javax.json.Json

/**
 * @author RJ
 */
@SpringJUnitConfig(FindKeyValueHandler::class)
@EnableWebFlux
@MockkBean(KeyValueService::class)
class FindKeyValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  handler: FindKeyValueHandler
) {
  private val client = bindToRouterFunction(route(REQUEST_PREDICATE, handler)).build()

  @Test
  fun findOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = "v"
    every { service.valueOf(key) } returns Mono.just(value)

    // invoke
    client.get().uri("/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.$key").isEqualTo(value)

    // verify
    verify(exactly = 1) { service.valueOf(key) }
  }

  @Test
  fun findOneButNotFound() {
    // mock
    val key = UUID.randomUUID().toString()
    every { service.valueOf(key) } returns Mono.empty()

    // invoke
    client.get().uri("/$key")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(exactly = 1) { service.valueOf(key) }
  }

  @Test
  fun findMulti() {
    // mock
    val kvList = (1..3).map { KeyValue.of("k-$it", "v-$it") }
    val expected = Mono.just(kvList.associate { it.k to it.v })
    val keyArray = kvList.map { it.k }.toTypedArray()
    every { service.find(*keyArray) } returns expected

    // invoke
    val expectedJson = Json.createObjectBuilder()
    kvList.forEach { expectedJson.add(it.k, it.v) }
    client.get().uri("/" + keyArray.joinToString(","))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .json(expectedJson.build().toString())

    // verify
    verify(exactly = 1) { service.find(*keyArray) }
  }

  @Test
  fun findMultiButNotFound() {
    // mock
    val keyList = (1..3).map { "k-$it" }
    val keyArray = keyList.toTypedArray()
    every { service.find(*keyArray) } returns Mono.empty()

    // invoke
    client.get().uri("/" + keyList.joinToString(","))
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(exactly = 1) { service.find(*keyArray) }
  }
}