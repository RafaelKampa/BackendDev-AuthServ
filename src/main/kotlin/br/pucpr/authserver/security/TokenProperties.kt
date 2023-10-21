package br.pucpr.authserver.security

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("security.token")
data class TokenProperties @ConstructorBinding constructor(
    @NotBlank
    val issuer: String,

    @Size(min=32, max=32)
    val secret: String,

    @Min(1)
    val expireHours: Long
)
