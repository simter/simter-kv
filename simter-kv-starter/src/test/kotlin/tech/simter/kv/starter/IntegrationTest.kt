package tech.simter.kv.starter

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import javax.json.Json

/**
 * 实际环境的集成测试。
 *
 * see [WebClientIntegrationTests](https://github.com/spring-projects/spring-framework/blob/v5.0.5.RELEASE/spring-webflux/src/test/java/org/springframework/web/reactive/function/client/WebClientIntegrationTests.java)
 *
 * @author RJ
 */
@Disabled
class IntegrationTest {
  private val webClient = WebTestClient.bindToServer().baseUrl("http://localhost:8085").build()
  private val contextPath = ""

  @Test
  fun findOne() {
    // prepare data
    val key = UUID.randomUUID().toString()
    val value = "v"
    val map = mapOf(key to value)
    save(map)

    // find
    webClient.get().uri("$contextPath/$key")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .json(map2JsonString(map))
  }

  @Test
  fun findOneButNotFound() {
    val key = UUID.randomUUID().toString()
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
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .json(map2JsonString(map))
  }

  @Test
  fun saveNone() {
    save(mapOf())
  }

  @Test
  fun saveOne() {
    save(mapOf(UUID.randomUUID().toString() to "v"))
  }

  @Test
  fun saveMulti() {
    save((1..3).associate { "k-$it" to "v-0-$it" })
  }

  private fun save(map: Map<String, String>) {
    // invoke save
    webClient.post().uri(contextPath)
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody(map2JsonString(map))
      .exchange()
      .expectStatus().isNoContent

    // verify saved
    if (!map.isEmpty()) {
      webClient.get().uri("$contextPath/" + map.keys.joinToString(","))
        .exchange()
        .expectStatus().isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
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
    val key = UUID.randomUUID().toString()
    webClient.delete().uri("$contextPath/$key").exchange().expectStatus().isNoContent
  }

  @Test
  fun deleteOne() {
    // prepare data
    val key = UUID.randomUUID().toString()
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