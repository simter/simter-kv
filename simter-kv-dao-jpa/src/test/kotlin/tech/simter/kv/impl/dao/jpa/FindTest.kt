package tech.simter.kv.impl.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import tech.simter.reactive.test.jpa.TestEntityManager

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class FindTest @Autowired constructor(
  val rem: TestEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    dao.find().test().verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(randomKey()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val random = randomKey()
    val pos = (1..3).map { KeyValuePo("$random-$it", "$random-$it") }
    rem.persist(*pos.toTypedArray())

    // invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.v, actualMap[it.k]) }
      }.verifyComplete()
  }
}