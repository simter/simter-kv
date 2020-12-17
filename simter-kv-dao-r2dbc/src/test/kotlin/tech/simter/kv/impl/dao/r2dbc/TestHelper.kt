package tech.simter.kv.impl.dao.r2dbc

import io.r2dbc.spi.Row
import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKeyValue

object TestHelper {
  fun randomKeyValuePo(): KeyValuePo = KeyValuePo.from(randomKeyValue())

  val rowToKeyValuePo = { row: Row ->
    KeyValuePo(k = row.get("k", String::class.java)!!, v = row.get("v", String::class.java)!!)
  }
}