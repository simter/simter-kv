package tech.simter.kv.test.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomKeyValue
import tech.simter.kv.test.rest.TestHelper.createOneKeyValue

/**
 * Test delete.
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class DeleteKeyValueTest @Autowired constructor(
  private val client: WebTestClient
) {
  @Test
  fun `delete not exists key`() {
    client.delete().uri("/${randomKey()}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `delete one`() {
    // prepare data
    val kv = randomKeyValue()
    createOneKeyValue(client = client, kv = kv)

    // delete it
    client.delete().uri("/${kv.k}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    // check deleted
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `delete multiple`() {
    // prepare data
    val kv1 = randomKeyValue()
    val kv2 = randomKeyValue()
    createOneKeyValue(client = client, kv = kv1)
    createOneKeyValue(client = client, kv = kv2)

    // delete it
    client.delete().uri("/${kv1.k},${kv2.k}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty

    // check deleted
    client.get().uri("/${kv1.k},${kv2.k}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }
}