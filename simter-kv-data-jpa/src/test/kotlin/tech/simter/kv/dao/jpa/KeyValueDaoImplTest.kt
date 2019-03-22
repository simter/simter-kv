package tech.simter.kv.dao.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author RJ
 */
@SpringJUnitConfig(ModuleConfiguration::class)
@DataJpaTest
class KeyValueDaoImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun valueOf() {
    // verify not exists
    dao.valueOf(UUID.randomUUID().toString())
      .test()
      .expectNextCount(0L)
      .verifyComplete()

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    em.persist(po)
    em.flush()
    em.clear()

    // verify exists
    dao.valueOf(po.key)
      .test()
      .expectNext(po.value)
      .verifyComplete()
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
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // 3.2 invoke and verify
    dao.find(*pos.map { it.key }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.value, actualMap[it.key]) }
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
    dao.save(mapOf(po.key to po.value)).test().expectNextCount(0L).verifyComplete()

    // verify saved
    assertThat(
      em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po)
  }

  @Test
  fun saveMulti() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }

    // verify result
    dao.save(pos.associate { it.key to it.value }).test().expectNextCount(0L).verifyComplete()

    // verify saved
    pos.forEach {
      assertThat(
        em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
          .setParameter("key", it.key).singleResult
      ).isEqualTo(it)
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
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // 3.2 verify empty result
    dao.delete(*pos.map { it.key }.toTypedArray()).test().expectNextCount(0L).verifyComplete()

    // 3.4 verify deleted
    assertEquals(0L,
      em.createQuery("select count(key) from KeyValue where key in (:keys)")
        .setParameter("keys", pos.map { it.key })
        .singleResult
    )
  }
}