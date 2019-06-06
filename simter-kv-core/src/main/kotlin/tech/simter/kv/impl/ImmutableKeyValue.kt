package tech.simter.kv.impl

import tech.simter.kv.core.KeyValue

/**
 * The immutable implementation of [KeyValue].
 *
 * @author RJ
 */
data class ImmutableKeyValue(
  override val k: String,
  override val v: String
) : KeyValue