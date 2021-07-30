package tech.simter.kv.impl.dao.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValue
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.test.TestHelper.randomKeyValue
import tech.simter.kv.test.TestHelper.randomValue

/**
 * Test [KeyValueDaoImpl.save].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class SaveTest @Autowired constructor(
  private val client: WebTestClient,
  private val dao: KeyValueDao
) {
  @Test
  fun `save nothing`() {
    dao.save(emptyMap()).test().verifyComplete()
  }

  @Test
  fun `create one`() {
    // create it
    val kv = randomKeyValue()
    dao.save(mapOf(kv.k to kv.v)).test().verifyComplete()

    // verify created
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv.k).isEqualTo(kv.v)
  }

  @Test
  fun `create many`() {
    // create it
    val kvs = (1..3).map { randomKeyValue() }
    val map = kvs.associate { it.k to it.v }
    dao.save(map).test().verifyComplete()

    // verify created
    val body = client.get().uri("/${kvs.joinToString(",") { it.k }}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
    kvs.forEach { body.jsonPath(it.k).isEqualTo(it.v) }
  }

  @Test
  fun `update one`() {
    // create it
    val kv = randomKeyValue()
    dao.save(mapOf(kv.k to kv.v)).test().verifyComplete()

    // update it
    val newValue = randomValue()
    dao.save(mapOf(kv.k to newValue)).test().verifyComplete()

    // verify updated
    client.get().uri("/${kv.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(kv.k).isEqualTo(newValue)
  }

  @Test
  fun `update many`() {
    // create it
    val kvs = (1..3).map { randomKeyValue() }
    val map = kvs.associate { it.k to it.v }
    dao.save(map).test().verifyComplete()

    // update it
    val newValue = randomValue()
    dao.save(map.mapValues { newValue }).test().verifyComplete()

    // verify updated
    val body = client.get().uri("/${kvs.joinToString(",") { it.k }}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
    kvs.forEach { body.jsonPath(it.k).isEqualTo(newValue) }
  }

  @Test
  fun `create and update`() {
    // prepare data
    var toUpdate = randomKeyValue()
    dao.save(mapOf(toUpdate.k to toUpdate.v)).test().verifyComplete()
    val toCreate = randomKeyValue()
    toUpdate = KeyValue.of(k = toUpdate.k, v = randomValue())

    // do create and update
    val map = mapOf(toUpdate.k to toUpdate.v, toCreate.k to toCreate.v)
    dao.save(map).test().verifyComplete()

    // verify created and updated
    client.get().uri("/${toUpdate.k},${toCreate.k}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .jsonPath(toUpdate.k).isEqualTo(toUpdate.v)
      .jsonPath(toCreate.k).isEqualTo(toCreate.v)
  }
}