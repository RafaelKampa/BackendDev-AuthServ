package br.pucpr.authserver.costCenters.controller.requests

import br.pucpr.authserver.costCenters.CostCenter
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateCostCenterRequest(
    @field: NotBlank
    val nomeCentroDeCusto: String,
    @field: NotBlank
    val enderecoCentroDeCusto: String,
    @field: NotNull
    val valorEmpreendido: Double
) {
    fun toCostCenter() = CostCenter(
        nomeCentroDeCusto = nomeCentroDeCusto,
        enderecoCentroDeCusto = enderecoCentroDeCusto,
        valorEmpreendido = valorEmpreendido
    )
}
