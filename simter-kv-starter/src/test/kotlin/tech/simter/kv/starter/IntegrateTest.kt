package tech.simter.kv.starter

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import tech.simter.kv.po.KeyValue
import java.time.Duration
import java.util.*
import javax.json.Json
import javax.json.JsonObject

/**
 * 实际环境的集成测试。
 *
 * see [WebClientIntegrationTests](https://github.com/spring-projects/spring-framework/blob/master/spring-webflux/src/test/java/org/springframework/web/reactive/function/webClient/WebClientIntegrationTests.java)
 *
 * @author RJ
 */
@Disabled
class IntegrateTest {
  private val testClient = WebTestClient.bindToServer().baseUrl("http://localhost:8085").build()
  private var webClient = WebClient.create("http://localhost:8085")

  @Test
  fun getValueButKeyNotExists() {
    val key = UUID.randomUUID().toString()
    val result = webClient.get()
      .uri("/kv/$key").accept(MediaType.APPLICATION_JSON)
      .exchange()
      .map { it.statusCode() }

    StepVerifier.create(result)
      .expectNext(HttpStatus.NO_CONTENT)
      .expectComplete().verify(Duration.ofSeconds(3))
  }

  @Test
  fun getValueAndKeyExists() {
    // create
    val kv = postOne()
    println(kv)

    // get
    testClient.get().uri("/kv/${kv.first.key}")
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
      .expectBody()
      .json(kv.second.toString())
  }

  @Test
  fun postOne1KeyValue() {
    postOne()
  }

  private fun postOne(): Pair<KeyValue, JsonObject> {
    val json: JsonObject
    // post one key-value pair
    val kv = KeyValue(UUID.randomUUID().toString(), "v")
    json = Json.createObjectBuilder()
      .add("key", kv.key)
      .add("value", kv.value)
      .build()
    val result = webClient.post().uri("/kv")
      .contentType(MediaType.APPLICATION_JSON_UTF8)
      .syncBody(json.toString())
      .exchange()
      .map { it.statusCode() }

    // verify
    StepVerifier.create(result)
      .expectNext(HttpStatus.NO_CONTENT)
      .expectComplete().verify(Duration.ofSeconds(3))

    return Pair(kv, json)
  }
}