package tech.simter.kv.rest.webflux.handler

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Mono
import tech.simter.kv.po.KeyValue
import tech.simter.kv.rest.webflux.handler.FindKeyValueHandler.Companion.REQUEST_PREDICATE
import tech.simter.kv.service.KeyValueService
import java.util.*
import javax.json.Json

/**
 * @author RJ
 */
@SpringJUnitConfig(FindKeyValueHandler::class)
@EnableWebFlux
@MockBean(KeyValueService::class)
class FindKeyValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  handler: FindKeyValueHandler
) {
  private val client = WebTestClient.bindToRouterFunction(
    route(REQUEST_PREDICATE, handler)
  ).build()

  @Test
  fun findOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = "v"
    `when`(service.getValue(key)).thenReturn(Mono.just(value))

    // invoke
    client.get().uri("/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .jsonPath("$.$key").isEqualTo(value)

    // verify
    verify(service).getValue(key)
  }

  @Test
  fun findButNotFound() {
    // mock
    val key = UUID.randomUUID().toString()
    `when`(service.getValue(key)).thenReturn(Mono.empty())

    // invoke
    client.get().uri("/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .json("{}")

    // verify
    verify(service).getValue(key)
  }

  @Test
  fun findMulti() {
    // mock
    val kvList = (1..3).map { KeyValue("k-$it", "v-$it") }
    val expected = Mono.just(kvList.associate { it.key to it.value })
    val keyArray = kvList.map { it.key }.toTypedArray()
    `when`(service.find(*keyArray)).thenReturn(expected)

    // invoke
    val expectedJson = Json.createObjectBuilder()
    kvList.forEach { expectedJson.add(it.key, it.value) }
    client.get().uri("/" + keyArray.joinToString(","))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .json(expectedJson.build().toString())

    // verify
    verify(service).find(*keyArray)
  }
}