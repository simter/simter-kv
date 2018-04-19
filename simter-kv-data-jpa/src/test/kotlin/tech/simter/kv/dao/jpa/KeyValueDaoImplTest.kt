package tech.simter.kv.dao.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.StepVerifier
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
    StepVerifier.create(dao.valueOf(UUID.randomUUID().toString()))
      .expectNextCount(0L)
      .verifyComplete()

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    em.persist(po)
    em.flush()
    em.clear()

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
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

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
    assertThat(
      em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po)
  }

  @Test
  fun saveMulti() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    val actual = dao.save(pos.associate { it.key to it.value })

    // verify result
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()

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
    StepVerifier.create(dao.delete()).expectNextCount(0L).verifyComplete()

    // 2. delete not exists key
    StepVerifier.create(dao.delete(UUID.randomUUID().toString())).expectNextCount(0L).verifyComplete()

    // 3. delete exists key
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // 3.2 invoke
    val actual = dao.delete(*pos.map { it.key }.toTypedArray())

    // 3.3 verify empty result
    StepVerifier.create(actual).expectNextCount(0L).verifyComplete()

    // 3.4 verify deleted
    assertEquals(0L,
      em.createQuery("select count(key) from KeyValue where key in (:keys)")
        .setParameter("keys", pos.map { it.key })
        .singleResult
    )
  }
}