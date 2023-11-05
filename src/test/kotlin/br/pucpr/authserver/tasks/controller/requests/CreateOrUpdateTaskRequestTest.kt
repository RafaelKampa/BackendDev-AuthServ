package br.pucpr.authserver.tasks.controller.requests

import br.pucpr.authserver.tasks.TaskStubs
import br.pucpr.authserver.users.UserStubs
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beBlank
import org.junit.jupiter.api.Test

class CreateOrUpdateTaskRequestTest {
    @Test
    fun `toTask creates a new task based on the request`() {
        val executor = UserStubs.userStub(id = 1L)
        val conferente = UserStubs.userStub(id = 1L)
        with(TaskStubs.taskStub()) {
            val req = CreateOrUpdateTaskRequest(
                tipoServico = tipoServico,
                valorUnitario = valorUnitario,
                dimensao = dimensao,
                unidadeMedida = unidadeMedida,
                centroDeCustoId = 1L,
                localExecucao = localExecucao,
                dataInicio = dataInicio,
                previsaoTermino = previsaoTermino,
                dataFinal = dataFinal,
                obs = obs,
                executor = setOf(executor.id!!),
                conferente = setOf(conferente.id!!)).toTask()
            req.id shouldBe null
            req.tipoServico shouldNotBe beBlank()
            req.tipoServico shouldBe tipoServico
            req.valorUnitario shouldNotBe null
            req.valorUnitario shouldBe valorUnitario
            req.dimensao shouldBe dimensao
            req.unidadeMedida shouldBe unidadeMedida
            req.localExecucao shouldBe localExecucao
            req.dataInicio shouldBe dataInicio
            req.previsaoTermino shouldBe previsaoTermino
            req.dataFinal shouldBe dataFinal
            req.obs shouldBe obs
        }
    }
}