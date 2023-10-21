package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@Component
class Jwt(
    val properties: TokenProperties
) {
    fun createToken(user: User): String =
        UserToken(user).let {
            Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(properties.secret.toByteArray()))
                .serializeToJsonWith(JacksonSerializer())
                .setIssuedAt(utcNow().toDate())
                .setExpiration(utcNow().plusHours(properties.expireHours).toDate())
                .setIssuer(properties.issuer)
                .setSubject(it.id.toString())
                .addClaims(mapOf(USER_FIELD to it))
                .compact()
        }

    fun extract(req: HttpServletRequest): Authentication? {
        try {
            val header = req.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith(PREFIX)) return null
            val token = header.replace(PREFIX, "").trim()

            val claims = Jwts.parserBuilder()
                .setSigningKey(properties.secret.toByteArray())
                .deserializeJsonWith(JacksonDeserializer(
                    mapOf(USER_FIELD to UserToken::class.java)
                )).build()
                .parseClaimsJws(token)
                .body

            if (claims.issuer != properties.issuer) return null
            val user = claims.get(USER_FIELD, UserToken::class.java)
            return createAuthentication(user)
        } catch (e: Throwable) {
            log.debug("Token rejected", e)
            return null
        }
    }


    companion object {
        private const val PREFIX = "Bearer"
        private const val USER_FIELD = "user"

        private val log = LoggerFactory.getLogger(Jwt::class.java)

        private fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())
        private fun utcNow(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
        fun createAuthentication(user: UserToken): Authentication {
            val authorities = user.roles.map { SimpleGrantedAuthority("ROLE_$it") }
            return UsernamePasswordAuthenticationToken.authenticated(
                user, user.id, authorities
            )
        }
    }
}