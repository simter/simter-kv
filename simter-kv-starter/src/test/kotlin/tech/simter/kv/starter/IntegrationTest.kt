package tech.simter.kv.starter

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import javax.json.Json

/**
 * Run test in the real server.
 * @author RJ
 */
@Disabled
class IntegrationTest {
  private fun randomString() = UUID.randomUUID().toString()
  private val webClient = WebTestClient.bindToServer().baseUrl("http://localhost:8085").build()
  private val contextPath = ""

  @Test
  fun findOne() {
    // prepare data
    val key = randomString()
    val value = "v"
    val map = mapOf(key to value)
    save(map)

    // find
    webClient.get().uri("$contextPath/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .json(map2JsonString(map))
  }

  @Test
  fun findOneButNotFound() {
    val key = randomString()
    webClient.get().uri("$contextPath/$key")
      .exchange()
      .expectStatus().isNoContent
  }

  @Test
  fun findMulti() {
    // prepare data
    val map = (1..3).associate { "k-$it" to "v-$it" }
    save(map)

    // find
    webClient.get().uri("$contextPath/" + map.keys.joinToString(","))
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody()
      .json(map2JsonString(map))
  }

  @Test
  fun saveNone() {
    save(mapOf())
  }

  @Test
  fun saveOne() {
    save(mapOf(randomString() to "v"))
  }

  @Test
  fun saveMulti() {
    save((1..3).associate { "k-$it" to "v-0-$it" })
  }

  private fun save(map: Map<String, String>) {
    // invoke save
    webClient.post().uri(contextPath)
      .contentType(APPLICATION_JSON)
      .bodyValue(map2JsonString(map))
      .exchange()
      .expectStatus().isNoContent

    // verify saved
    if (map.isNotEmpty()) {
      webClient.get().uri("$contextPath/" + map.keys.joinToString(","))
        .exchange()
        .expectStatus().isOk
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody()
        .json(map2JsonString(map))
    }
  }

  private fun map2JsonString(map: Map<String, String>): String {
    return if (map.isEmpty()) "{}" else {
      val json = Json.createObjectBuilder()
      map.forEach { json.add(it.key, it.value) }
      json.build().toString()
    }
  }

  @Test
  fun deleteNotExistsKey() {
    val key = randomString()
    webClient.delete().uri("$contextPath/$key").exchange().expectStatus().isNoContent
  }

  @Test
  fun deleteOne() {
    // prepare data
    val key = randomString()
    val value = "v"
    val map = mapOf(key to value)
    save(map)

    // invoke delete
    webClient.delete().uri("$contextPath/$key").exchange().expectStatus().isNoContent

    // verify deleted
    webClient.get().uri("$contextPath/$key").exchange().expectStatus().isNoContent
  }

  @Test
  fun deleteMulti() {
    // prepare data
    val map = (1..3).associate { "k-$it" to "v-$it" }
    save(map)

    // invoke delete
    webClient.delete().uri("$contextPath/" + map.keys.joinToString(",")).exchange().expectStatus().isNoContent

    // verify deleted
    webClient.get().uri("$contextPath/" + map.keys.joinToString(",")).exchange().expectStatus().isNoContent
  }
}