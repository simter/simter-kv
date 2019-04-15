package tech.simter.kv.dao.jpa

import tech.simter.kv.po.KeyValue
import java.util.*

object TestHelper {
  fun randomString(): String = UUID.randomUUID().toString()
  fun randomKeyValue(): KeyValue = KeyValue("k-" + randomString(), "v-" + randomString())
}