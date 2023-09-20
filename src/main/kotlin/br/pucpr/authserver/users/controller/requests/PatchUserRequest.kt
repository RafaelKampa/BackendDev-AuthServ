package br.pucpr.authserver.users.controller.requests

import jakarta.validation.constraints.NotBlank

data class PatchUserRequest(
    @field:NotBlank
    val name: String?
)