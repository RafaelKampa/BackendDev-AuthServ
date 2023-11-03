package br.pucpr.authserver.tasks

import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.UserStubs
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class TaskServiceTest {
    private val repositoryMock = mockk<TaskRepository>()
    private val userRepositoryMock = mockk<UserRepository>()
    private val service = TaskService(repositoryMock, userRepositoryMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(repositoryMock, userRepositoryMock)
    }

    @Test
    fun `insert must return the saved task if it's inserted`() {
        val adminUser = UserStubs.adminStub()
        val user = UserStubs.userStub(null)
        every { userRepositoryMock.findById(1L) } returns Optional.of(user)
        every { userRepositoryMock.findById(1000L) } returns Optional.of(adminUser)

        val task = TaskStubs.taskStub(id = null)
        task.conferente.clear()// remove o usuário comum da lista de conferentes
        task.conferente.add(adminUser) // adiciona um usuário admin como conferente

        val saved = TaskStubs.taskStub()
        every { repositoryMock.save(task) } returns saved
        service.insert(task) shouldBe saved
    }



}
