package tech.simter.kv.impl.dao.r2dbc

import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKeyValue

object TestHelper {
  fun randomKeyValuePo(): KeyValuePo = KeyValuePo.from(randomKeyValue())
}