package tech.simter.kv.impl.dao.r2dbc

import io.r2dbc.client.R2dbc
import io.r2dbc.client.Update
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.ImmutableKeyValue

/**
 * The r2dbc-client implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Repository
class KeyValueDaoImplByR2dbcClient @Autowired constructor(
  connectionFactory: ConnectionFactory
) : KeyValueDao {
  val r2dbc = R2dbc(connectionFactory)

  override fun valueOf(key: String): Mono<String> {
    return r2dbc.withHandle { handle ->
      handle.select("select v from $TABLE_KV where k = $1", key)
        .mapResult { result -> result.map { row, _ -> getNotNullString(row, "v") } }
    }.toMono()
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return if (keys.isEmpty()) Mono.empty()
    else r2dbc.withHandle { handle ->
      val query = if (keys.size == 1) handle.select("select k, v from $TABLE_KV where k = $1", keys[0])
      else {
        val paramMarkers = keys.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
        handle.select("select k, v from $TABLE_KV where k in ($paramMarkers)", *keys)
      }
      query.mapResult { result ->
        result.map { row, _ ->
          ImmutableKeyValue(
            k = getNotNullString(row, "k"),
            v = getNotNullString(row, "v")
          )
        }
      }
    }.collectMap({ it.k }, { it.v })
      .flatMap { if (it.isEmpty()) Mono.empty() else Mono.just(it) }
  }

  private fun getNotNullString(row: Row, column: String): String {
    return row.get(column, String::class.java) as String
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return if (keyValues.isEmpty()) Mono.empty()
    else r2dbc.useTransaction { handle ->
      val paramMarkers = keyValues.entries.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
      handle.select("select k from $TABLE_KV where k in ($paramMarkers)", *keyValues.keys.toTypedArray())
        .mapResult { it.map { row, _ -> row.get("k", String::class.java) as String } }
        .collectList()
        .flatMapMany { existsKeys ->
          // separate update and create items
          val toUpdate = keyValues.filter { (k, _) -> existsKeys.contains(k) }
          val toCreate = keyValues.filter { (k, _) -> !existsKeys.contains(k) }

          // do update
          val update: Update? = if (toUpdate.isNotEmpty()) {
            val update = handle.createUpdate("update $TABLE_KV set v = $2 where k = $1")
            toUpdate.forEach { update.bind("$1", it.key).bind("$2", it.value).add() }
            update
          } else null

          // do create
          val create: Update? = if (toCreate.isNotEmpty()) {
            val create = handle.createUpdate("insert into $TABLE_KV (k, v) values ($1, $2)")
            toCreate.forEach { create.bind("$1", it.key).bind("$2", it.value).add() }
            create
          } else null

          // return
          Flux.concat(
            update?.execute() ?: Flux.empty(),
            create?.execute() ?: Flux.empty()
          )
        }
    }
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return if (keys.isEmpty()) Mono.empty()
    else r2dbc.useTransaction { handle ->
      if (keys.size == 1) handle.execute("delete from $TABLE_KV where k = $1", keys[0])
      else {
        val paramMarkers = keys.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
        handle.execute("delete from $TABLE_KV where k in ($paramMarkers)", *keys)
      }
    }
  }
}