package tech.simter.kv.impl.dao.jpa

import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKeyValue

object TestHelper {
  fun randomKeyValuePo(): KeyValuePo = KeyValuePo.from(randomKeyValue())
}