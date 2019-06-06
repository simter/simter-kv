package tech.simter.kv.impl.dao.jpa

import tech.simter.kv.core.KeyValue
import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import java.util.*

object TestHelper {
  fun randomString(): String = UUID.randomUUID().toString()
  fun randomKeyValue(): KeyValue = KeyValuePo("k-" + randomString(), "v-" + randomString())
}