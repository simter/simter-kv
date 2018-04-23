package tech.simter.kv.dao.reactive.mongo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*

/**
 * See [SimpleReactiveMongoRepository] implementation.
 * @author RJ
 */
@SpringJUnitConfig(ModuleConfiguration::class)
@DataMongoTest
class KeyValueDaoImplTest @Autowired constructor(
  private val operations: ReactiveMongoOperations,
  private val dao: KeyValueDao
) {
  @BeforeEach
  fun setup() {
    // drop and create a new collection
    StepVerifier.create(
      operations.collectionExists(KeyValue::class.java)
        .flatMap { if (it) operations.dropCollection(KeyValue::class.java) else Mono.just(it) }
        .then(operations.createCollection(KeyValue::class.java))
    ).expectNextCount(1).verifyComplete()
  }

  @Test
  fun valueOf() {
    // verify not exists
    StepVerifier.create(dao.valueOf(UUID.randomUUID().toString())).expectNextCount(0L).verifyComplete()

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    StepVerifier.create(operations.insert(po)).expectNextCount(1).verifyComplete()

    // verify exists
    StepVerifier.create(dao.valueOf(po.key))
      .expectNext(po.value)
      .verifyComplete()
  }

  @Test
  fun find() {
    // 1. none key
    StepVerifier.create(dao.find()).expectNextCount(0L).verifyComplete()

    // 2. not found
    StepVerifier.create(dao.find(UUID.randomUUID().toString())).expectNextCount(0L).verifyComplete()

    // 3. found
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    StepVerifier.create(operations.insertAll(pos)).expectNextCount(pos.size.toLong()).verifyComplete()

    // 3.2 invoke
    val actual = dao.find(*pos.map { it.key }.toTypedArray())

    // 3.3 verify
    StepVerifier.create(actual)
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.value, actualMap[it.key]) }
      }
      .verifyComplete()
  }

  @Test
  fun saveNone() {
    val none = mapOf<String, String>()
    val actual = dao.save(none)
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()
  }

  @Test
  fun saveOne() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    val actual = dao.save(mapOf(po.key to po.value))

    // verify result
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()

    // verify saved
    StepVerifier.create(operations.findById(po.key, KeyValue::class.java))
      .expectNext(po)
      .verifyComplete()
  }

  @Test
  fun saveMulti() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    val actual = dao.save(pos.associate { it.key to it.value })

    // verify result
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()

    // verify saved
    pos.forEach {
      StepVerifier.create(operations.findById(it.key, KeyValue::class.java))
        .expectNext(it)
        .verifyComplete()
    }
  }

  @Test
  fun delete() {
    // 1. none key
    StepVerifier.create(dao.delete()).expectNextCount(0L).verifyComplete()

    // 2. delete not exists key
    StepVerifier.create(dao.delete(UUID.randomUUID().toString())).expectNextCount(0L).verifyComplete()

    // 3. delete exists key
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    StepVerifier.create(operations.insertAll(pos)).expectNextCount(pos.size.toLong()).verifyComplete()

    // 3.2 invoke
    val actual = dao.delete(*pos.map { it.key }.toTypedArray())

    // 3.3 verify result
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()

    // 3.4 verify deleted
    StepVerifier.create(operations.count(query(where("key").`in`(pos.map { it.key })), KeyValue::class.java))
      .expectNext(0L)
      .verifyComplete()
  }
}