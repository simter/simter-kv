package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.dao.jpa.TestHelper.randomString
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.jpa.ReactiveEntityManager
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class FindMethodImplTest @Autowired constructor(
  val rem: ReactiveEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    dao.find().test().verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(randomString()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val random = randomString()
    val pos = (1..3).map { KeyValue("$random-$it", "$random-$it") }
    rem.persist(*pos.toTypedArray()).test().verifyComplete()

    // invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.v, actualMap[it.k]) }
      }.verifyComplete()
  }
}