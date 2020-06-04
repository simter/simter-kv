package tech.simter.kv.test

import tech.simter.kv.core.KeyValue
import tech.simter.util.RandomUtils.randomString

/**
 * Some common tools for unit test.
 *
 * @author RJ
 */
object TestHelper {
  /** Create a random key */
  fun randomKey(len: Int = 10): String {
    return randomString(len = len)
  }

  /** Create a random value */
  fun randomValue(): String {
    return randomString()
  }

  /** Create a random [KeyValue] instance */
  fun randomKeyValue(
    k: String = randomKey(),
    v: String = randomValue()
  ): KeyValue {
    return KeyValue.of(k = k, v = v)
  }
}