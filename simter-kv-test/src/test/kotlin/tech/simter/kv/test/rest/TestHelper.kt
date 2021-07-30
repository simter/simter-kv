package tech.simter.kv.test.rest

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import tech.simter.kv.core.KeyValue
import tech.simter.kv.test.TestHelper.randomKeyValue

object TestHelper {
  /** create one kv */
  fun createOne(
    client: WebTestClient,
    kv: KeyValue = randomKeyValue()
  ): KeyValue {
    client.post()
      .uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue(mapOf(kv.k to kv.v))
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    return kv
  }
}