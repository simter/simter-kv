package tech.simter.kv.impl.dao.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomKeyValue

/**
 * Test [KeyValueDaoImpl.delete].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class DeleteTest @Autowired constructor(
  private val client: WebTestClient,
  private val dao: KeyValueDao
) {
  @Test
  fun `delete nothing`() {
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(randomKey()).test().verifyComplete()
  }

  @Test
  fun `delete one`() {
    // prepare data
    val kv = randomKeyValue()
    dao.save(mapOf(kv.k to kv.v)).test().verifyComplete()

    // delete it
    dao.delete(kv.k).test().verifyComplete()

    // verify deleted
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }

  @Test
  fun `delete many`() {
    // prepare data
    val kvs = (1..3).map { randomKeyValue() }
    val map = kvs.associate { it.k to it.v }
    dao.save(map).test().verifyComplete()

    // do delete
    dao.delete(*kvs.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    client.get().uri("/${kvs.joinToString(",")}")
      .exchange()
      .expectStatus().isNoContent
      .expectBody().isEmpty
  }
}