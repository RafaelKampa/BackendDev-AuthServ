package br.pucpr.authserver.tasks

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.costCenters.CostCenterRepository
import br.pucpr.authserver.costCenters.CostCenterStubs
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.UserStubs
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import java.util.*

internal class TaskServiceTest {
    private val repositoryMock = mockk<TaskRepository>()
    private val userRepositoryMock = mockk<UserRepository>()
    private val costCenterRepositoryMock = mockk<CostCenterRepository>()
    private val service = TaskService(repositoryMock, userRepositoryMock, costCenterRepositoryMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(repositoryMock, userRepositoryMock, costCenterRepositoryMock)
    }

    @Test
    fun `insert must return the saved task if it's inserted`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)
        every { userRepositoryMock.findById(1000L) } returns Optional.of(adminUser)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = null)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente

        val saved = TaskStubs.taskStub()
        every { repositoryMock.save(task) } returns saved
        service.insert(task) shouldBe saved
    }

    @Test
    fun `insert must return NotFoundException if executor is empty`() {
        val user = every { userRepositoryMock.findById(1L) } returns Optional.empty()

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.executor.clear()

        assertThrows<NotFoundException> {
            service.insert(task)
        } shouldHaveMessage "Executor not found!"
    }

    @Test
    fun `insert must return NotFoundException if conferente is empty`() {
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.conferente.clear()

        assertThrows<NotFoundException> {
            service.insert(task)
        } shouldHaveMessage "Conferente not found!"
    }

    @Test
    fun `insert must return NotFoundException if centroDeCusto is null`() {
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)

        val task = TaskStubs.taskStub(id = 1L)
        task.centroDeCusto = null

        assertThrows<NotFoundException> {
            service.insert(task)
        } shouldHaveMessage "Cost center not found!"
    }

    @Test
    fun `insert must return ForbiddenExeption if one or more conferentes does not have an administrator role` () {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)
        every { userRepositoryMock.findById(1000L) } returns Optional.of(adminUser)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.conferente.add(adminUser)

        val nonAdminConferentes = task.conferente.filter { !it.isAdmin }
        var usernames = ""

        if (nonAdminConferentes.isNotEmpty()) {
            usernames = nonAdminConferentes.joinToString { it.name }
        }

        assertThrows<ForbiddenException> {
            service.insert(task)
        } shouldHaveMessage "The following 'Conferente' users do not have Administrator permission: $usernames"
    }

    @Test
    fun `findByIdOrNull should delegate to repository`() {
        val task = TaskStubs.taskStub()
        every { repositoryMock.findById(1) } returns Optional.of(task)
        service.findByIdOrNull(1) shouldBe task
    }

    @Test
    fun `update update and save the task with slot and capture`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)
        every { userRepositoryMock.findById(1000L) } returns Optional.of(adminUser)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        every { repositoryMock.findById(1) } returns Optional.of(task)

        val saved = TaskStubs.taskStub(
            id = 1,
            tipoServico = "Tipo Teste",
            valorUnitario = 15.0,
            dimensao = 100.0,
            unidadeMedida = "m2",
            centroDeCusto = CostCenterStubs.costStub(1L),
            localExecucao = "Local novo",
            dataInicio = Date(),
            previsaoTermino = Date(),
            dataFinal = null,
            obs = "Obs Teste",
            executor = mutableSetOf(UserStubs.userStub(1)),
            conferente = mutableSetOf(adminUser)
            )
        val slot = slot<Task>()
        every { repositoryMock.save(capture(slot)) } returns saved

        service.update(1, saved) shouldBe saved
        slot.isCaptured shouldBe true
        slot.captured shouldBe saved
    }

    @Test
    fun `update must return NotFoundException if centroDeCusto is null`() {
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)

        val task = TaskStubs.taskStub(id = 1L)
        task.centroDeCusto = null

        assertThrows<NotFoundException> {
            service.update(1, task)
        } shouldHaveMessage "Cost center not found!"
    }

    @Test
    fun `update must return NotFoundException if executor is empty`() {
        val user = every { userRepositoryMock.findById(1L) } returns Optional.empty()

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.executor.clear()

        assertThrows<NotFoundException> {
            service.update(1, task)
        } shouldHaveMessage "Executor not found!"
    }

    @Test
    fun `update must return NotFoundException if conferente is empty`() {
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.conferente.clear()

        assertThrows<NotFoundException> {
            service.update(1, task)
        } shouldHaveMessage "Conferente not found!"
    }

    @Test
    fun `update must return ForbiddenExeption if one or more conferentes does not have an administrator role` () {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)
        every { userRepositoryMock.findById(1000L) } returns Optional.of(adminUser)

        val costCenter = CostCenterStubs.costStub(null)
        every { costCenterRepositoryMock.findById(1L) } returns Optional.of(costCenter)

        val task = TaskStubs.taskStub(id = 1L)
        task.conferente.add(adminUser)

        val nonAdminConferentes = task.conferente.filter { !it.isAdmin }
        var usernames = ""

        if (nonAdminConferentes.isNotEmpty()) {
            usernames = nonAdminConferentes.joinToString { it.name }
        }

        assertThrows<ForbiddenException> {
            service.update(1, task)
        } shouldHaveMessage "The following 'Conferente' users do not have Administrator permission: $usernames"
    }

    @Test
    fun `findAll should request an ascending list if SortDir ASC is used`() {
        val sortDir = SortDir.ASC
        val taskList = listOf(TaskStubs.taskStub(1), TaskStubs.taskStub(2), TaskStubs.taskStub(3))
        every { repositoryMock.findAll(Sort.by("dataInicio").ascending()) } returns taskList
        service.findAll(sortDir) shouldBe taskList
    }

    @Test
    fun `findAll should request an descending list if SortDir DESC is used`() {
        val sortDir = SortDir.DESC
        val taskList = listOf(TaskStubs.taskStub(1), TaskStubs.taskStub(2), TaskStubs.taskStub(3))
        every { repositoryMock.findAll(Sort.by("dataInicio").descending()) } returns taskList
        service.findAll(sortDir) shouldBe taskList
    }
}
