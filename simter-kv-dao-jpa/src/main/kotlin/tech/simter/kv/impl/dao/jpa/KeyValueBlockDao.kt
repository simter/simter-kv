package tech.simter.kv.impl.dao.jpa

import java.util.*

/**
 * The Key-Value block Dao Interface.
 *
 * @author RJ
 */
interface KeyValueBlockDao {
  /**
   * Retrieves a value by key.
   *
   * @param[key] the key
   * @return [Optional] with the value if key exists or [Optional.empty] otherwise.
   */
  fun valueOf(key: String): Optional<String>

  /**
   * Find all key-value pairs by keys.
   *
   * @param[keys] the keys
   * @return key-value pairs store in a map if at lease one key exists or empty [Map] otherwise.
   */
  fun find(vararg keys: String): Map<String, String>

  /**
   * Create or update key-value pairs.
   *
   * Update the value if key exists or create a new key-value pair otherwise .
   *
   * @param[keyValues] the key-value pairs to save or update
   */
  fun save(keyValues: Map<String, String>)

  /**
   * Delete key-value pairs by key.
   *
   * @param[keys] the keys to delete
   */
  fun delete(vararg keys: String)
}