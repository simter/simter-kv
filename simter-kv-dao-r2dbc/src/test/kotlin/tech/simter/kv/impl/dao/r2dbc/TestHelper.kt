package tech.simter.kv.impl.dao.r2dbc

import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import tech.simter.kv.TABLE_KV
import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo
import java.util.*

object TestHelper {
  /** 清空数据库各表的数据 */
  fun cleanDatabase(databaseClient: DatabaseClient): Mono<Void> {
    return databaseClient.delete().from(TABLE_KV).then()
  }

  fun randomString(): String = UUID.randomUUID().toString()
  fun randomKeyValue(): KeyValuePo = KeyValuePo("k-" + randomString(), "v-" + randomString())
}