package br.pucpr.authserver.tasks.controller.requests

import br.pucpr.authserver.costCenters.CostCenter
import br.pucpr.authserver.costCenters.CostCenterRepository
import br.pucpr.authserver.costCenters.controller.responses.CostCenterResponse
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.tasks.Task
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateOrUpdateTaskRequest(
    @field:NotBlank
    val tipoServico: String,

    @field:NotNull
    val valorUnitario: Double,

    @field:NotNull
    val dimensao: Double,

    @field:NotBlank
    val unidadeMedida: String,

    @field:NotNull
    val centroDeCustoId: Long,

    @field:NotBlank
    val localExecucao: String,

    @field:NotNull
    val dataInicio: Date,

    @field:NotNull
    val previsaoTermino: Date,

    val dataFinal: Date?,

    @field:NotNull
    val valorTotal: Double,

    val obs: String?,

    @field:NotEmpty
    val executor: Set<Long>,

    @field:NotEmpty
    val conferente: Set<Long>,

){
    fun toTask() = Task (
        tipoServico = tipoServico,
        valorUnitario = valorUnitario,
        dimensao = dimensao,
        unidadeMedida = unidadeMedida,
        localExecucao = localExecucao,
        dataInicio = dataInicio,
        previsaoTermino = previsaoTermino,
        dataFinal = dataFinal,
        valorTotal = valorTotal,
        obs = obs
    )
}