package br.pucpr.authserver.tasks

import br.pucpr.authserver.costCenters.CostCenter
import br.pucpr.authserver.costCenters.CostCenterStubs
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserStubs
import java.util.*

object TaskStubs {
    fun taskStub(
        id: Long? = 1,
        tipoServico: String = "Teste",
        valorUnitario: Double = 10.0,
        dimensao: Double = 10.0,
        unidadeMedida: String = "m2",
        centroDeCusto: CostCenter = CostCenterStubs.costStub(id = 1L),
        localExecucao: String = "Local teste",
        dataInicio: Date = Date(),
        previsaoTermino: Date = Date(),
        dataFinal: Date? = null,
        obs: String = "Tese de Observação enormeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
        executor: MutableSet<User> = setOf(UserStubs.userStub(id = 1L)).toMutableSet(),
        conferente: MutableSet<User> = setOf(UserStubs.userStub(id = 1L)).toMutableSet()
    ) = Task(
        id = id,
        tipoServico = tipoServico,
        valorUnitario = valorUnitario,
        dimensao = dimensao,
        unidadeMedida = unidadeMedida,
        centroDeCusto = centroDeCusto,
        localExecucao = localExecucao,
        dataInicio = dataInicio,
        previsaoTermino = previsaoTermino,
        dataFinal = dataFinal,
        valorTotal = valorUnitario * dimensao,
        obs = obs,
        executor = executor,
        conferente = conferente
    )
}