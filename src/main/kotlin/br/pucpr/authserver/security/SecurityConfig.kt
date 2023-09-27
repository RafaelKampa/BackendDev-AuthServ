package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@Configuration
@EnableMethodSecurity
@SecurityScheme(
    name="AuthServer",
    type=SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@PropertySource("classpath:/security.properties")
class SecurityConfig(
    val jwtTokenFilter: JwtTokenFilter
) {
    @Bean
    fun mvc(introspector: HandlerMappingIntrospector) = MvcRequestMatcher.Builder(introspector)

    @Bean
    fun filterChain(security: HttpSecurity, mvc: MvcRequestMatcher.Builder) =
        security.cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint { _, res, ex ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED")
                        .also { log.warn("Authorization failed", ex)  }
                }
            }
            .headers { header -> header.frameOptions { it.disable() }}
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(antMatcher(HttpMethod.GET)).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/users")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/users/login")).permitAll()
                    .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtTokenFilter, BasicAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsFilter() =
        CorsConfiguration().apply {
            addAllowedHeader("*")
            addAllowedOrigin("*")
            addAllowedMethod("*")
        }.let {
            UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", it)
            }
        }.let { CorsFilter(it) }

    @ConfigurationProperties("security.admin")
    @Bean("defaultAdmin")
    fun adminUser() = User()

    companion object {
        private val log = LoggerFactory.getLogger(SecurityConfig::class.java)
    }
}