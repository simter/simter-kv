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
import reactor.test.test
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
    operations.collectionExists(KeyValue::class.java)
      .flatMap { if (it) operations.dropCollection(KeyValue::class.java) else Mono.just(it) }
      .then(operations.createCollection(KeyValue::class.java))
      .test()
      .expectNextCount(1)
      .verifyComplete()
  }

  @Test
  fun valueOf() {
    // verify not exists
    dao.valueOf(UUID.randomUUID().toString()).test().expectNextCount(0L).verifyComplete()

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    operations.insert(po).test().expectNextCount(1).verifyComplete()

    // verify exists
    dao.valueOf(po.k).test().expectNext(po.v).verifyComplete()
  }

  @Test
  fun find() {
    // 1. none key
    dao.find().test().expectNextCount(0L).verifyComplete()

    // 2. not found
    dao.find(UUID.randomUUID().toString()).test().expectNextCount(0L).verifyComplete()

    // 3. found
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    operations.insertAll(pos).test().expectNextCount(pos.size.toLong()).verifyComplete()

    // 3.2 invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.v, actualMap[it.k]) }
      }.verifyComplete()
  }

  @Test
  fun saveNone() {
    val none = mapOf<String, String>()
    dao.save(none).test().expectNextCount(0L).verifyComplete()
  }

  @Test
  fun saveOne() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")

    // verify result
    dao.save(mapOf(po.k to po.v)).test().expectNextCount(0L).verifyComplete()

    // verify saved
    operations.findById(po.k, KeyValue::class.java).test().expectNext(po).verifyComplete()
  }

  @Test
  fun saveMulti() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }

    // verify result
    dao.save(pos.associate { it.k to it.v }).test().expectNextCount(0L).verifyComplete()

    // verify saved
    pos.forEach {
      operations.findById(it.k, KeyValue::class.java)
        .test()
        .expectNext(it)
        .verifyComplete()
    }
  }

  @Test
  fun delete() {
    // 1. none key
    dao.delete().test().expectNextCount(0L).verifyComplete()

    // 2. delete not exists key
    dao.delete(UUID.randomUUID().toString()).test().expectNextCount(0L).verifyComplete()

    // 3. delete exists key
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    operations.insertAll(pos).test().expectNextCount(pos.size.toLong()).verifyComplete()

    // 3.2 invoke and verify result
    dao.delete(*pos.map { it.k }.toTypedArray()).test().expectNextCount(0L).verifyComplete()

    // 3.3 verify deleted
    operations.count(query(where("k").`in`(pos.map { it.k })), KeyValue::class.java)
      .test()
      .expectNext(0L)
      .verifyComplete()
  }
}