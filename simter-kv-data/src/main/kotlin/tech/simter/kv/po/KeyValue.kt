package tech.simter.kv.po

import tech.simter.kv.TABLE_KV
import javax.persistence.Column

/**
 * The key-value pair PO.
 *
 * @author RJ
 */
// for jpa
@javax.persistence.Entity
@javax.persistence.Table(name = TABLE_KV)
// for jdbc or r2dbc
@org.springframework.data.relational.core.mapping.Table(TABLE_KV)
// for mongodb
@org.springframework.data.mongodb.core.mapping.Document(collection = TABLE_KV)
data class KeyValue(
  @javax.persistence.Id
  @org.springframework.data.annotation.Id
  @Column(length = 100)
  val k: String,
  val v: String
)