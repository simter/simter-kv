package tech.simter.kv.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.po.KeyValue

/**
 * The Key-Value Service Interface.
 *
 * @author RJ
 */
interface KeyValueService {
  /**
   * Get value of the specific key.
   *
   * @param[key] the key
   * @return the value. if the key is not exists, return null
   */
  fun getValue(key: String): Mono<String>

  /**
   * Find all key-value pairs by its key.
   *
   * @param[keys] the keys
   * @return the key-value pairs
   */
  fun findAll(vararg keys: String): Flux<KeyValue>

  /**
   * Save or update a key-value pair.
   *
   * If the key-value pair exists, update its value. Otherwise create a new key-value pair.
   *
   * @param[kv] the key-value pair to save or update
   */
  fun save(kv: KeyValue): Mono<Void>

  /**
   * Save or update key-value pairs.
   *
   * If the key-value pair exists, update its value. Otherwise create a new key-value pair.
   *
   * @param[kvs] the key-value pairs to save or update
   */
  fun saveAll(kvs: Flux<KeyValue>): Mono<Void>
}