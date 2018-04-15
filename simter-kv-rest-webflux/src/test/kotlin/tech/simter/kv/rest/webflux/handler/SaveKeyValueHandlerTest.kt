package tech.simter.kv.rest.webflux.handler

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.RouterFunctions
import reactor.core.publisher.Mono
import tech.simter.kv.rest.webflux.handler.SaveKeyValueHandler.Companion.REQUEST_PREDICATE
import tech.simter.kv.service.KeyValueService
import java.util.*
import javax.json.Json

/**
 * @author RJ
 */
@SpringJUnitConfig(SaveKeyValueHandler::class)
@EnableWebFlux
@MockBean(KeyValueService::class)
class SaveKeyValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  //moduleRouter: RouterFunction<ServerResponse>,
  handler: SaveKeyValueHandler
) {
  private val client = WebTestClient.bindToRouterFunction(
    RouterFunctions.route(REQUEST_PREDICATE, handler)
  ).build()

  @Test
  fun saveNone() {
    // invoke
    client.post().uri("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody("{}")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(service, times(0)).save(any())
  }

  @Test
  fun saveOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = "v"
    val kv = mapOf(key to value)
    `when`(service.save(kv)).thenReturn(Mono.empty())

    // invoke
    client.post().uri("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody("{\"$key\":\"$value\"}")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(service).save(kv)
  }

  @Test
  fun saveMulti() {
    // mock
    val kvs = (1..3).associate { "k-$it" to "v-$it" }
    val kvJson = Json.createObjectBuilder()
    kvs.forEach { kvJson.add(it.key, it.value) }
    `when`(service.save(any())).thenReturn(Mono.empty())

    // invoke
    client.post().uri("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody(kvJson.build().toString())
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(service).save(any())
  }
}