package tech.simter.kv.impl.dao.r2dbc

import io.r2dbc.client.R2dbc
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValue
import tech.simter.kv.impl.ImmutableKeyValue
import java.util.*

@Component
class TestHelper @Autowired constructor(
  connectionFactory: ConnectionFactory
) {
  val r2dbc = R2dbc(connectionFactory)

  fun insert(vararg po: KeyValue): Mono<Void> {
    return r2dbc.useHandle { handle ->
      val create = handle.createUpdate("insert into $TABLE_KV (k, v) values ($1, $2)")
      po.forEach { create.bind("$1", it.k).bind("$2", it.v).add() }
      create.execute()
    }
  }

  fun <T> select(sql: String, vararg params: Any, mapper: (Row, RowMetadata) -> T): Flux<T> {
    return r2dbc.withHandle { handle ->
      handle
        .select(sql, *params)
        .mapResult { it.map { row, meta -> mapper(row, meta) } }
    }
  }

  companion object {
    fun randomString(): String = UUID.randomUUID().toString()
    fun randomKeyValue(): ImmutableKeyValue = ImmutableKeyValue("k-" + randomString(), "v-" + randomString())
  }
}