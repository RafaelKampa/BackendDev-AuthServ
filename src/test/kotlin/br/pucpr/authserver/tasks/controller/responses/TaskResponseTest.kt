package br.pucpr.authserver.tasks.controller.responses

import br.pucpr.authserver.costCenters.CostCenter
import br.pucpr.authserver.costCenters.controller.responses.CostCenterResponse
import br.pucpr.authserver.tasks.TaskStubs
import br.pucpr.authserver.users.controller.responses.UserResponse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TaskResponseTest {
    @Test
    fun `constructor should copy all important values`() {
        val task = TaskStubs.taskStub()
        val response = TaskResponse(task)
        response.id shouldBe task.id
        response.tipoServico shouldBe task.tipoServico
        response.valorUnitario shouldBe task.valorUnitario
        response.dimensao shouldBe task.dimensao
        response.unidadeMedida shouldBe task.unidadeMedida
        response.centroDeCusto shouldBe task.centroDeCusto?.let { CostCenterResponse(it) }
        response.localExecucao shouldBe task.localExecucao
        response.dataInicio shouldBe task.dataInicio
        response.previsaoTermino shouldBe task.previsaoTermino
        response.dataFinal shouldBe task.dataFinal
        response.valorTotal shouldBe task.valorTotal
        response.obs shouldBe task.obs
        response.executor shouldBe task.executor.map { UserResponse(it) }.toSet()
        response.conferente shouldBe task.conferente.map { UserResponse(it) }.toSet()
    }
}