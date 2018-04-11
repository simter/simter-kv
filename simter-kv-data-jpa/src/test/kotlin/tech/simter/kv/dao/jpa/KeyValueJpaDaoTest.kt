package tech.simter.kv.dao.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import tech.simter.kv.po.KeyValue
import java.util.*
import javax.persistence.EntityManager

/**
 * @author RJ
 */
@SpringJUnitConfig(JpaTestConfiguration::class)
@DataJpaTest
class KeyValueJpaDaoTest @Autowired constructor(val dao: KeyValueJpaDao, val em: EntityManager) {
  @Test
  fun findById() {
    // verify not exists
    assertFalse(dao.findById(UUID.randomUUID().toString()).isPresent)

    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    em.persist(po)
    em.flush()
    em.clear()

    // verify exists
    val actual = dao.findById(po.key)
    assertTrue(actual.isPresent)
    assertEquals(po, actual.get())
  }

  @Test
  fun findAllById() {
    // verify not exists
    assertTrue(dao.findAllById(listOf(UUID.randomUUID().toString())).none())

    // prepare data
    val range = (1..3)
    val expected = range.map { KeyValue("k-$it", "v-$it") }
    expected.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // verify exists
    val actual = dao.findAllById(range.map { "k-$it" })
    assertEquals(expected.count(), actual.count())
    actual.forEachIndexed { index, kv -> assertEquals(expected[index], kv) }
  }

  @Test
  fun saveOne() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    dao.save(po)
    assertThat(
      em.createQuery("select value from KeyValue where key = :key")
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po.value)
  }

  @Test
  fun saveAll() {
    val range = (1..3)
    val pos = range.map { KeyValue("k-$it", "v-$it") }
    dao.saveAll(pos)
    pos.forEach {
      assertThat(
        em.createQuery("select value from KeyValue where key = :key")
          .setParameter("key", it.key).singleResult
      ).isEqualTo(it.value)
    }
  }
}