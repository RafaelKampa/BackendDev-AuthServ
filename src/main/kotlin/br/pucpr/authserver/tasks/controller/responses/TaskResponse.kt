package br.pucpr.authserver.tasks.controller.responses

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.controller.responses.UserResponse
import java.util.*

data class TaskResponse(
    val id: Long?,
    val tipoServico: String,
    val valorUnitario: Double,
    val dimensao: Double,
    val unidadeMedida: String,
    val centroDeCusto: String,
    val localExecucao: String,
    val dataInicio: Date,
    val previsaoTermino: Date,
    val dataFinal: Date?,
    val valorTotal: Double,
    val obs: String?,
    val executor: Set<UserResponse>,
    val conferente: Set<UserResponse>
) {
    constructor(task: Task) : this(
        task.id!!,
        task.tipoServico!!,
        task.valorUnitario!!,
        task.dimensao!!,
        task.unidadeMedida!!,
        task.centroDeCusto!!,
        task.localExecucao!!,
        task.dataInicio!!,
        task.previsaoTermino!!,
        task.dataFinal,
        task.valorTotal!!,
        task.obs,
        task.executor.map { UserResponse(it) }.toSet(),
        task.conferente.map { UserResponse(it) }.toSet()
    )
}
