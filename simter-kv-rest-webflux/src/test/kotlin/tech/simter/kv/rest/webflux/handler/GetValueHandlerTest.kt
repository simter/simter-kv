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
import org.springframework.web.reactive.function.server.HandlerFunction
import reactor.core.publisher.Mono
import tech.simter.kv.service.KeyValueService
import java.util.*

/**
 * 测试保存会话信息的 [HandlerFunction]。
 *
 * @author RJ
 */
@SpringJUnitConfig(GetValueHandler::class)
@EnableWebFlux
@MockBean(KeyValueService::class)
class GetValueHandlerTest @Autowired constructor(
  private val service: KeyValueService,
  handler: GetValueHandler
) {
  private val client = WebTestClient.bindToRouterFunction(handler.router()).build()

  @Test
  fun test() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = "v"
    `when`(service.getValue(key)).thenReturn(Mono.just(value))

    // invoke
    client.get().uri("/kv/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .jsonPath("$.key").isEqualTo(value)

    // verify
    verify(service).getValue(key)
  }
}