package tech.simter.kv.rest.webflux.handler

import com.nhaarman.mockito_kotlin.any
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
import tech.simter.kv.po.KeyValue
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
  fun saveOne() {
    // mock
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    `when`(service.save(po)).thenReturn(Mono.empty())

    // invoke
    client.post().uri("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody("{\"${po.key}\":\"${po.value}\"}")
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(service).save(po)
  }

  @Test
  fun saveMulti() {
    // mock
    val kvList = (1..3).map { KeyValue("k-$it", "v-$it") }
    val kvJson = Json.createObjectBuilder()
    kvList.forEach { kvJson.add(it.key, it.value) }
    `when`(service.saveAll(any())).thenReturn(Mono.empty())

    // invoke
    client.post().uri("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody(kvJson.build().toString())
      .exchange()
      .expectStatus().isNoContent

    // verify
    verify(service).saveAll(any())
  }
}