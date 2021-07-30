package tech.simter.kv.test.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomKeyValue
import tech.simter.kv.test.rest.TestHelper.createOne

/**
 * Test find.
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class FindTest @Autowired constructor(
  private val client: WebTestClient
) {
  @Test
  fun `found nothing`() {
    client.get().uri("/${randomKey()}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `found one`() {
    // prepare data
    val kv = randomKeyValue()
    createOne(client = client, kv = kv)

    // find it
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv.k).isEqualTo(kv.v)
  }

  @Test
  fun `found multiple`() {
    // prepare data
    val kv1 = randomKeyValue()
    val kv2 = randomKeyValue()
    createOne(client = client, kv = kv1)
    createOne(client = client, kv = kv2)

    // find it
    client.get().uri("/${kv1.k},${kv2.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv1.k).isEqualTo(kv1.v)
      .jsonPath(kv2.k).isEqualTo(kv2.v)
  }
}