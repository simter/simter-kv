package tech.simter.kv.po

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * The Key-Value PO.
 *
 * @author RJ
 */
@Entity
@Table(name = "ST_KEY_VALUE")
data class KeyValue(
  @Id @Column(length = 100)
  val key: String,
  val value: String?
)