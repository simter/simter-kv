package tech.simter.kv.starter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders.ORIGIN
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import tech.simter.kv.PACKAGE
import tech.simter.reactive.web.Utils.TEXT_HTML_UTF8
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

/**
 * Application WebFlux Configuration.
 *
 * see [WebFlux config API](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-config-enable)
 *
 * @author RJ
 */
@Configuration("$PACKAGE.starter.AppConfiguration")
@EnableWebFlux
class AppConfiguration @Autowired constructor(
  @Value("\${module.version.simter-kv:UNKNOWN}") private val version: String
) {
  /**
   * Register by method [DelegatingWebFluxConfiguration.setConfigurers].
   *
   * See [WebFlux config API](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-config-enable)
   */
  @Bean
  fun rootWebFluxConfigurer(): WebFluxConfigurer {
    return object : WebFluxConfigurer {
      /**
       * CORS config.
       *
       * See [Enabling CORS](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-cors)
       */
      override fun addCorsMappings(registry: CorsRegistry?) {
        // Enabling CORS for the whole application
        // By default all origins and GET, HEAD, and POST methods are allowed
        registry!!.addMapping("/**")
          .allowedOrigins("*")
          .allowedMethods("*")
          .allowedHeaders("Authorization", "Content-Type", "Content-Disposition")
          .exposedHeaders("Location")
          .allowCredentials(false)
          .maxAge(1800) // seconds
      }

      /** See [Static Resources](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-config-static-resources) */
      override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**")
          .addResourceLocations("classpath:/META-INF/resources/static/")
          .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
      }
    }
  }

  private val startTime = OffsetDateTime.now()
  private val rootPage: String = """
    <h2>Simter Key-Value Micro Service</h2>
    <div>Start at : $startTime</div>
    <div>Version : $version</div>
  """.trimIndent()

  /**
   * Other application routes.
   */
  @Bean
  fun rootRoutes() = router {
    "/".nest {
      // root /
      GET("/") { ok().contentType(TEXT_HTML_UTF8).syncBody(rootPage) }

      // OPTIONS /*
      OPTIONS("/**") { noContent().build() }
    }
  }

  /**
   * Enabled static file for CORS request.
   *
   * Just add Access-Control-Allow-Origin header.
   */
  @Bean
  fun corsFilter4StaticFile(): WebFilter {
    return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
      val request = exchange.request
      if (CorsUtils.isCorsRequest(request)                          // cross origin
        && !CorsUtils.isPreFlightRequest(request)                   // not OPTION request
        && request.path.value().startsWith("/static/")) {   // only for static file dir
        // Add Access-Control-Allow-Origin header
        exchange.response.headers.add("Access-Control-Allow-Origin", request.headers.getFirst(ORIGIN))
      }
      chain.filter(exchange)
    }
  }
}