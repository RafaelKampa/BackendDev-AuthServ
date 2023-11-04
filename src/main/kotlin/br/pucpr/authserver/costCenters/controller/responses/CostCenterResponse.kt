package br.pucpr.authserver.costCenters.controller.responses

import br.pucpr.authserver.costCenters.CostCenter

data class CostCenterResponse(
    val id: Long?,
    val nomeCentroDeCusto: String,
    val enderecoCentroDeCusto: String,
    val valorEmpreendido: Double
) {
    constructor(costCenter: CostCenter) : this (
        costCenter.id!!,
        costCenter.nomeCentroDeCusto,
        costCenter.enderecoCentroDeCusto,
        costCenter.valorEmpreendido
    )
}
