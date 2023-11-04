package br.pucpr.authserver.costCenters.controller.requests

import jakarta.validation.constraints.NotBlank

data class PatchCostCenterNameResquest(
    @field:NotBlank
    val nomeCentroDeCusto: String
)
