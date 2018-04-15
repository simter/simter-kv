package tech.simter.kv.service

import reactor.core.publisher.Mono

/**
 * The key-value Service Interface.
 *
 * @author RJ
 */
interface KeyValueService {
  /**
   * Get value of the specific key.
   *
   * @param[key] the key
   * @return the value. if the key is not exists, return Mono.empty()
   */
  fun getValue(key: String): Mono<String>

  /**
   * Find all key-value pairs by its key.
   *
   * @param[keys] the keys
   * @return the key-value pairs store in a map or a empty map if none matches
   */
  fun find(vararg keys: String): Mono<Map<String, String>>

  /**
   * Save or update key-value pairs.
   *
   * If the key-value pair exists, update its value. Otherwise create a new key-value pair.
   *
   * @param[keyValues] the key-value pairs to save or update
   */
  fun save(keyValues: Map<String, String>): Mono<Void>
}