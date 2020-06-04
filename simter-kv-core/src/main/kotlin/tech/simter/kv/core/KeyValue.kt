package tech.simter.kv.core

/**
 * The key-value pair domain interface.
 *
 * @author RJ
 */
interface KeyValue {
  /** The Key */
  val k: String

  /** The Value */
  val v: String

  /** An inner immutable [KeyValue] implementation */
  private data class Impl(
    override val k: String,
    override val v: String
  ) : KeyValue

  companion object {
    /** Create an immutable [KeyValue] instance */
    fun of(k: String, v: String): KeyValue {
      return Impl(k = k, v = v)
    }
  }
}