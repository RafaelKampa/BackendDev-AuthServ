package br.pucpr.authserver.costCenters.controller.requests

import jakarta.validation.constraints.NotBlank

data class PatchCostCenterAdressRequest(
    @field:NotBlank
    val enderecoCentroDeCusto: String
)
