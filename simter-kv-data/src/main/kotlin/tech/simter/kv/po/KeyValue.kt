package tech.simter.kv.po

import org.springframework.data.mongodb.core.mapping.Document
import tech.simter.kv.TABLE_KV
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The key-value pair PO.
 *
 * @author RJ
 */
@Entity
@Table(name = TABLE_KV)
@Document(collection = TABLE_KV)
data class KeyValue(
  @javax.persistence.Id
  @org.springframework.data.annotation.Id
  @Column(length = 100)
  val k: String,
  val v: String
)