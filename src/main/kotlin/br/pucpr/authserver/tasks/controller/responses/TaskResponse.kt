package br.pucpr.authserver.tasks.controller.responses

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.users.User
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
    val executor: Set<Long>,
    val conferente: Set<Long>
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
        task.executor!!,
        task.conferente!!
    )
}
