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
    StepVerifier.create(dao.find()).verifyComplete()

    // 2. not found
    StepVerifier.create(dao.find(UUID.randomUUID().toString())).verifyComplete()

    // 3. found
    // 3.1 prepare data
    val kvs = (1..3).map { KeyValue("k-$it", "v-$it") }
    kvs.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // 3.2 invoke
    val actual = dao.find(*kvs.map { it.key }.toTypedArray())

    // 3.3 verify
    StepVerifier.create(actual)
      .consumeNextWith { actualMap ->
        assertEquals(kvs.size, actualMap.size)
        kvs.forEach { assertEquals(it.value, actualMap[it.key]) }
      }
      .verifyComplete()
  }

  @Test
  fun saveNone() {
    val none = mapOf<String, String>()
    val actual = dao.save(none)
    StepVerifier.create(actual).verifyComplete()
  }

  @Test
  fun saveOne() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    val actual = dao.save(mapOf(po.key to po.value))

    // verify empty result
    StepVerifier.create(actual).verifyComplete()

    // verify persistence
    assertThat(
      em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po)
  }

  @Test
  fun saveAll() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    val actual = dao.save(pos.associate { it.key to it.value })

    // verify empty result
    StepVerifier.create(actual).verifyComplete()

    // verify persistence
    pos.forEach {
      assertThat(
        em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
          .setParameter("key", it.key).singleResult
      ).isEqualTo(it)
    }
  }
}