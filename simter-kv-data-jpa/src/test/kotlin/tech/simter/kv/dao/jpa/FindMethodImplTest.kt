package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.jpa.ReactiveEntityManager
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import java.util.*

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
    dao.find(UUID.randomUUID().toString()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    rem
      // prepare data
      .persist(*pos.toTypedArray())
      // invoke
      .then(dao.find(*pos.map { it.k }.toTypedArray()))
      .test()
      // verify
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.v, actualMap[it.k]) }
      }.verifyComplete()
  }
}