package tech.simter.kv.impl.dao.r2dbc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao

/**
 * The spring-data-r2dbc implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Repository
class KeyValueDaoImpl @Autowired constructor(
  private val databaseClient: DatabaseClient
) : KeyValueDao {
  override fun valueOf(key: String): Mono<String> {
    return databaseClient.sql("select v from $TABLE_KV where k = :key")
      .bind("key", key)
      .map { row -> row.get("v", String::class.java)!! }
      .one()
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return if (keys.isEmpty()) Mono.empty()
    else {
      if (keys.size == 1) { // single key
        if (keys[0].contains("%")) { // fuzzy match
          databaseClient.sql("select k, v from $TABLE_KV where k like :key order by k asc")
            .bind("key", keys[0])
        } else { // equation match
          databaseClient.sql("select k, v from $TABLE_KV where k = :key")
            .bind("key", keys[0])
        }
      } else { // multiple keys
        val equationMatchKeys = keys.filterNot { it.contains("%") }
        val fuzzyMatchKeys = keys.filter { it.contains("%") }
        val fuzzyMatchConditions = mutableListOf<String>()
        fuzzyMatchKeys.forEachIndexed { index, _ -> fuzzyMatchConditions.add("k like :key$index") }

        // build sql
        var sql = "select k, v from $TABLE_KV where "
        sql += if (equationMatchKeys.isEmpty()) {
          fuzzyMatchConditions.joinToString(" or ")
        } else {
          "k in (:keys)" + if (fuzzyMatchConditions.isEmpty()) "" else (" or " + fuzzyMatchConditions.joinToString(" or "))
        }
        sql += " order by k asc"
        println(sql)
        var spec = databaseClient.sql(sql)

        // bind params
        if (equationMatchKeys.isNotEmpty()) spec = spec.bind("keys", equationMatchKeys)
        fuzzyMatchKeys.forEachIndexed { index, key -> spec = spec.bind("key$index", key) }

        // return
        spec
      }.map { row -> arrayOf(row.get("k", String::class.java)!!, row.get("v", String::class.java)!!) }
        .all()
        .collectMap({ it[0] }, { it[1] })
        .flatMap { if (it.isEmpty()) Mono.empty() else Mono.just(it) }
    }
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return if (keyValues.isEmpty()) Mono.empty()
    else databaseClient.sql("select k from $TABLE_KV where k in (:keys)")
      .bind("keys", keyValues.keys.toList())
      .map { row -> row.get("k", String::class.java) }
      .all()
      .collectList()
      .flatMapMany { existsKeys ->
        // separate update and create items
        val toUpdateList = keyValues.filter { (k, _) -> existsKeys.contains(k) }
        val toCreateList = keyValues.filter { (k, _) -> !existsKeys.contains(k) }

        val updateSql = "update $TABLE_KV set v = :value where k = :key"
        val createSql = "insert into $TABLE_KV (k, v) values (:key, :value)"

        val monoList = toUpdateList.map {
          // do update
          databaseClient.sql(updateSql)
            .bind("key", it.key)
            .bind("value", it.value)
            .fetch()
            .rowsUpdated()
        } + toCreateList.map {
          // do create
          databaseClient.sql(createSql)
            .bind("key", it.key)
            .bind("value", it.value)
            .fetch()
            .rowsUpdated()
        }

        // return
        Flux.concat(*monoList.toTypedArray())
      }.then()
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return if (keys.isEmpty()) Mono.empty()
    else (
      if (keys.size == 1)
        databaseClient.sql("delete from $TABLE_KV where k = :key")
          .bind("key", keys[0])
      else
        databaseClient.sql("delete from $TABLE_KV where k in (:keys)")
          .bind("keys", keys.toList())
      )
      .fetch()
      .rowsUpdated()
      .then()
  }
}