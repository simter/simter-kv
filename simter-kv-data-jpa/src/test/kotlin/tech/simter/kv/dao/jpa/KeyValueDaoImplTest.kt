package tech.simter.kv.dao.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*
import javax.persistence.EntityManager

/**
 * @author RJ
 */
@SpringJUnitConfig(ModuleConfiguration::class)
@DataJpaTest
class KeyValueDaoImplTest @Autowired constructor(
  val dao: KeyValueDao,
  val em: EntityManager
) {
  @Test
  fun findById() {
    // verify not exists
    StepVerifier.create(dao.findById(UUID.randomUUID().toString()))
      .verifyComplete()

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    em.persist(po)
    em.flush()
    em.clear()

    // verify exists
    StepVerifier.create(dao.findById(po.key))
      .expectNext(po)
      .verifyComplete()
  }

  @Test
  fun findAllById() {
    // verify not exists
    StepVerifier.create(dao.findAllById(listOf(UUID.randomUUID().toString())))
      .recordWith({ ArrayList() })
      .expectNextCount(0L)
      .verifyComplete()

    // prepare data
    val range = (1..3)
    val kvs = range.map { KeyValue("k-$it", "v-$it") }
    kvs.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // invoke
    val actual = dao.findAllById(range.map { "k-$it" })

    // verify exists
    StepVerifier.create(actual)
      .recordWith({ ArrayList() })
      .expectNextCount(kvs.size.toLong())
      .consumeRecordedWith { for ((index, value) in it.withIndex()) assertEquals(kvs[index], value) }
      .verifyComplete()
  }

  @Test
  fun saveOne() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    val actual = dao.save(po)

    // verify return value
    StepVerifier.create(actual)
      .expectNext(po)
      .verifyComplete()

    // verify persistence
    assertThat(
      em.createQuery("select a from KeyValue a where key = :key")
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po)
  }

  @Test
  fun saveAll() {
    val range = (1..3)
    val pos = range.map { KeyValue("k-$it", "v-$it") }
    val actual = dao.saveAll(Flux.fromIterable(pos))

    // verify return value
    StepVerifier.create(actual)
      .recordWith({ ArrayList() })
      .expectNextCount(pos.size.toLong())
      .consumeRecordedWith { for ((index, value) in it.withIndex()) assertEquals(pos[index], value) }
      .verifyComplete()

    // verify persistence
    pos.forEach {
      assertThat(
        em.createQuery("select a from KeyValue a where key = :key")
          .setParameter("key", it.key).singleResult
      ).isEqualTo(it)
    }
  }
}