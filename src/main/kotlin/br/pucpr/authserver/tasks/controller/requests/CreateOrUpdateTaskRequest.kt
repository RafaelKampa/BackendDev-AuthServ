package br.pucpr.authserver.tasks.controller.requests

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.users.User
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

    @field:NotBlank
    val centroDeCusto: String,

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
    val conferente: Set<Long>
){
    fun toTask() = Task(
        tipoServico = tipoServico!!,
        valorUnitario = valorUnitario!!,
        dimensao = dimensao!!,
        unidadeMedida = unidadeMedida!!,
        centroDeCusto = centroDeCusto!!,
        localExecucao = localExecucao!!,
        dataInicio = dataInicio!!,
        previsaoTermino = previsaoTermino!!,
        dataFinal = dataFinal,
        valorTotal = valorTotal!!,
        obs = obs
    )
}