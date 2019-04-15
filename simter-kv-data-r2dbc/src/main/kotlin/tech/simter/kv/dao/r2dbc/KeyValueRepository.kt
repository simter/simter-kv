package tech.simter.kv.dao.r2dbc

import org.springframework.data.r2dbc.function.DatabaseClient
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import tech.simter.kv.po.KeyValue

/**
 * See interfaces [ReactiveSortingRepository], [ReactiveQueryByExampleExecutor], [ReactiveCrudRepository], [DatabaseClient].
 *
 * See implements [SimpleR2dbcRepository].
 *
 * @author RJ
 */
interface KeyValueRepository : ReactiveCrudRepository<KeyValue, String>