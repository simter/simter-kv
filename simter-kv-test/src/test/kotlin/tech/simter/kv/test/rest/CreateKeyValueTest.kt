package tech.simter.kv.test.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import tech.simter.kv.test.TestHelper.randomKeyValue
import javax.json.Json

/**
 * Test create.
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class CreateKeyValueTest @Autowired constructor(
  private val client: WebTestClient
) {
  @Test
  fun `success create one`() {
    // mock
    val kv = randomKeyValue()
    val data = Json.createObjectBuilder()
      .add(kv.k, kv.v)

    // invoke and verify
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue(data.build().toString())
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `success create multiple`() {
    // mock
    val kv1 = randomKeyValue()
    val kv2 = randomKeyValue()
    val data = Json.createObjectBuilder()
      .add(kv1.k, kv1.v)
      .add(kv2.k, kv2.v)

    // invoke and verify
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue(data.build().toString())
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `ignore by empty json`() {
    // invoke and verify
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue("{}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `ignore by empty body`() {
    // invoke and verify
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue("")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `ignore by without body`() {
    // invoke and verify
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }
}