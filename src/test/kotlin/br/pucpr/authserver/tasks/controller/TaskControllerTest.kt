package br.pucpr.authserver.tasks.controller

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.costCenters.CostCenterService
import br.pucpr.authserver.costCenters.CostCenterStubs
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.tasks.TaskService
import br.pucpr.authserver.tasks.TaskStubs
import br.pucpr.authserver.tasks.controller.requests.CreateOrUpdateTaskRequest
import br.pucpr.authserver.tasks.controller.responses.TaskResponse
import br.pucpr.authserver.users.UserService
import br.pucpr.authserver.users.UserStubs
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

class TaskControllerTest {
    private val serviceMock = mockk<TaskService>()
    private val userServiceMock = mockk<UserService>()
    private val costCenterServiceMock = mockk<CostCenterService>()
    private val controller = TaskController(serviceMock, userServiceMock, costCenterServiceMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(serviceMock, userServiceMock, costCenterServiceMock)
    }

    @Test
    fun `insert must return the new task with CREATED code`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(1L)
        every { userServiceMock.findByIdOrNull(1L) } returns user
        every { userServiceMock.findByIdOrNull(1000L) } returns adminUser

        val costCenter = CostCenterStubs.costStub(1L)
        every { costCenterServiceMock.findByIdOrNull(1) } returns costCenter

        val task = TaskStubs.taskStub(id = 1L)
        task.executor.clear()
        task.executor.add(user)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente
        task.centroDeCusto = costCenter

        every { costCenterServiceMock.increaseValueUndertaken(costCenter.id!!, task.valorTotal) } returns costCenter

        val request = CreateOrUpdateTaskRequest(
            tipoServico = task.tipoServico,
            valorUnitario = task.valorUnitario,
            dimensao = task.dimensao,
            unidadeMedida = task.unidadeMedida,
            centroDeCustoId = task.centroDeCusto!!.id!!,
            localExecucao = task.localExecucao,
            dataInicio = task.dataInicio,
            previsaoTermino = task.previsaoTermino,
            dataFinal = task.dataFinal,
            obs = task.obs,
            executor = setOf(task.executor.first().id!!),
            conferente = setOf(task.conferente.first().id!!))
        every { serviceMock.insert(any()) } returns task
        with(controller.insert(request)) {
            statusCode shouldBe HttpStatus.CREATED
            body shouldBe TaskResponse(task)
        }
    }

    @Test
    fun `insert must return NotFoundException if centroDeCusto is null`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(1L)

        val task = TaskStubs.taskStub(id = 1L)
        task.executor.clear()
        task.executor.add(user)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente
        task.centroDeCusto = null

        every { costCenterServiceMock.findByIdOrNull(any()) } returns null

        assertThrows<NotFoundException> {
            controller.insert(CreateOrUpdateTaskRequest(
                tipoServico = task.tipoServico,
                valorUnitario = task.valorUnitario,
                dimensao = task.dimensao,
                unidadeMedida = task.unidadeMedida,
                centroDeCustoId = 1000L,
                localExecucao = task.localExecucao,
                dataInicio = task.dataInicio,
                previsaoTermino = task.previsaoTermino,
                dataFinal = task.dataFinal,
                obs = task.obs,
                executor = setOf(task.executor.first().id!!),
                conferente = setOf(task.conferente.first().id!!))
            )
        } shouldHaveMessage "Cost Center not found!"
    }

    @Test
    fun `update must return the task updated`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(1L)
        every { userServiceMock.findByIdOrNull(1L) } returns user
        every { userServiceMock.findByIdOrNull(1000L) } returns adminUser

        val costCenter = CostCenterStubs.costStub(1L)
        every { costCenterServiceMock.findByIdOrNull(1) } returns costCenter

        val task = TaskStubs.taskStub(id = 1L)
        every { serviceMock.findByIdOrNull(1) } returns task

        task.executor.clear()
        task.executor.add(user)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente
        task.centroDeCusto = costCenter

        every { costCenterServiceMock.increaseValueUndertaken(costCenter.id!!, task.valorTotal) } returns costCenter
        every { costCenterServiceMock.decreaseValueUndertaken(costCenter.id!!, task.valorTotal) } returns costCenter

        val request = CreateOrUpdateTaskRequest(
            tipoServico = task.tipoServico,
            valorUnitario = task.valorUnitario,
            dimensao = task.dimensao,
            unidadeMedida = task.unidadeMedida,
            centroDeCustoId = task.centroDeCusto!!.id!!,
            localExecucao = task.localExecucao,
            dataInicio = task.dataInicio,
            previsaoTermino = task.previsaoTermino,
            dataFinal = task.dataFinal,
            obs = task.obs,
            executor = setOf(task.executor.first().id!!),
            conferente = setOf(task.conferente.first().id!!))
        every { serviceMock.update(1, any()) } returns task
        with(controller.update(request, 1)) {
            body shouldBe TaskResponse(task)
        }
    }

    @Test
    fun `update must return NotFoundException if centroDeCusto is null`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(1L)

        val task = TaskStubs.taskStub(id = 1L)
        task.executor.clear()
        task.executor.add(user)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente
        task.centroDeCusto = null

        every { costCenterServiceMock.findByIdOrNull(any()) } returns null

        assertThrows<NotFoundException> {
            controller.update(CreateOrUpdateTaskRequest(
                tipoServico = task.tipoServico,
                valorUnitario = task.valorUnitario,
                dimensao = task.dimensao,
                unidadeMedida = task.unidadeMedida,
                centroDeCustoId = 1000L,
                localExecucao = task.localExecucao,
                dataInicio = task.dataInicio,
                previsaoTermino = task.previsaoTermino,
                dataFinal = task.dataFinal,
                obs = task.obs,
                executor = setOf(task.executor.first().id!!),
                conferente = setOf(task.conferente.first().id!!)),
                task.id!!
            )
        } shouldHaveMessage "Cost Center not found!"
    }

    @Test
    fun `update should return NO_CONTENT if the service returns null`() {
        val user = UserStubs.userStub(1L)
        every { userServiceMock.findByIdOrNull(1L) } returns user

        val costCenter = CostCenterStubs.costStub(1L)
        every { costCenterServiceMock.findByIdOrNull(1000L) } returns costCenter

        val task = TaskStubs.taskStub(id = 1L)
        every { serviceMock.findByIdOrNull(1L) } returns task
        every { serviceMock.update(eq(1), any()) } returns null

        every { costCenterServiceMock.decreaseValueUndertaken(costCenter.id!!, task.valorTotal) } returns costCenter
        every { costCenterServiceMock.increaseValueUndertaken(costCenter.id!!, task.valorTotal) } returns costCenter

        with(controller.update(CreateOrUpdateTaskRequest(
            tipoServico = task.tipoServico,
            valorUnitario = task.valorUnitario,
            dimensao = task.dimensao,
            unidadeMedida = task.unidadeMedida,
            centroDeCustoId = 1000L,
            localExecucao = task.localExecucao,
            dataInicio = task.dataInicio,
            previsaoTermino = task.previsaoTermino,
            dataFinal = task.dataFinal,
            obs = task.obs,
            executor = setOf(task.executor.first().id!!),
            conferente = setOf(task.conferente.first().id!!)
            ), task.id!!))
        {
            statusCode shouldBe HttpStatus.NO_CONTENT
            body shouldBe null
        }
    }

    @Test
    fun `list should return all found tasks with the given sort parameter`() {
        val taskList = listOf(TaskStubs.taskStub(1), TaskStubs.taskStub(2), TaskStubs.taskStub(3))

        every { serviceMock.findAll(SortDir.DESC) } returns taskList
        with(controller.list("DESC")) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe taskList.map { TaskResponse(it) }
        }
    }

    @Test
    fun `list should return all found tasks with the ascending Sort if no parameter is passed`() {
        val taskList = listOf(TaskStubs.taskStub(1), TaskStubs.taskStub(2), TaskStubs.taskStub(3))

        every { serviceMock.findAll(SortDir.ASC) } returns taskList
        with(controller.list(null)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe taskList.map { TaskResponse(it) }
        }
    }

    @Test
    fun `delete should return OK if the task gets deleted`() {
        every { serviceMock.delete(1) } returns true
        with(controller.delete(1)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe null
        }
    }

    @Test
    fun `delete should return NOT_FOUND if the task does not exists`() {
        every { serviceMock.delete(1) } returns false
        controller.delete(1).statusCode shouldBe HttpStatus.NOT_FOUND
    }

}