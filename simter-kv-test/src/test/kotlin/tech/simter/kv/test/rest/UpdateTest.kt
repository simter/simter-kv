package tech.simter.kv.test.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import tech.simter.kv.test.TestHelper.randomKeyValue
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.kv.test.rest.TestHelper.createOne
import javax.json.Json

/**
 * Test update.
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class UpdateTest @Autowired constructor(
  private val client: WebTestClient
) {
  @Test
  fun `success update one`() {
    // prepare data
    val kv = randomKeyValue()
    createOne(client = client, kv = kv)
    val newValue = randomValue()
    val data = Json.createObjectBuilder()
      .add(kv.k, newValue)

    // update it
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue(data.build().toString())
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    // check updated
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv.k).isEqualTo(newValue)
  }

  @Test
  fun `success update multiple`() {
    // prepare data
    val kv1 = randomKeyValue()
    val kv2 = randomKeyValue()
    createOne(client = client, kv = kv1)
    createOne(client = client, kv = kv2)
    val newValue = randomValue()
    val data = Json.createObjectBuilder()
      .add(kv1.k, newValue)
      .add(kv2.k, newValue)

    // update it
    client.post().uri("/")
      .contentType(APPLICATION_JSON)
      .bodyValue(data.build().toString())
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    // check updated
    client.get().uri("/${kv1.k},${kv2.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv1.k).isEqualTo(newValue)
      .jsonPath(kv2.k).isEqualTo(newValue)
  }
}