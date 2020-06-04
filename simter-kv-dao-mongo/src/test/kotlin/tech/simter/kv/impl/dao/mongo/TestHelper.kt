package tech.simter.kv.impl.dao.mongo

import tech.simter.kv.impl.dao.mongo.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKeyValue

object TestHelper {
  fun randomKeyValuePo(): KeyValuePo = KeyValuePo.from(randomKeyValue())
}