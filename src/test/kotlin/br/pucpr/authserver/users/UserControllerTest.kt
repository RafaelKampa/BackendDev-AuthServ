package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.controller.UserController
import br.pucpr.authserver.users.controller.requests.CreateUserRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserControllerTest {
    private val repositoryMock = mockk<UserService>()
    private val controller = UserController(repositoryMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(repositoryMock)
    }

    @Test
    fun `insert must throw BadRequestException if an user with the same email is found`() {
        val user = Stubs.userStub(id = null)
        every { repositoryMock.insert(user) } returns Stubs.userStub()
        assertThrows<BadRequestException> {
            controller.insert(CreateUserRequest(user.email, user.password, user.name))
        } shouldHaveMessage "User already exists"
    }

    @Test
    fun `update must return null if there's no changes`() {
        val user = Stubs.userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        controller.service.update(1, "user") shouldBe null
    }

    @Test
    fun `findByIdOrNull should delegate to repository`() {
        val user = Stubs.userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        controller.service.findByIdOrNull(1) shouldBe user
    }

    @Test
    fun `update must throw NotFoundException if the user does not exists`() {
        every { repositoryMock.findByIdOrNull(1) } returns null
        assertThrows<NotFoundException> {
            controller.service.update(1, "name")
        }
    }

    @Test
    fun `update update and save the user with slot and capture`() {
        val user = Stubs.userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user

        val saved = Stubs.userStub(1, "name")
        val slot = slot<User>()
        every { user.id?.let { repositoryMock.update(it, user.name) } } returns saved

        controller.service.update(1, "name") shouldBe saved
        slot.isCaptured shouldBe true
        slot.captured.name shouldBe "name"
    }

    @Test
    fun `update update and save the user with answers`() {
        every { repositoryMock.findByIdOrNull(1) } returns Stubs.userStub()
        every { repositoryMock.update(any(), any()) } answers { firstArg() }

        val saved = controller.service.update(1, "name")!!
        saved.name shouldBe "name"
    }

    @Test
    fun `findAll should delegate to repository`() {
        val sortDir = SortDir.values().random()
        val userList = listOf(Stubs.userStub(1), Stubs.userStub(2), Stubs.userStub(3))
        every { repositoryMock.findAll(sortDir) } returns userList
        controller.service.findAll(sortDir) shouldBe userList
    }

    @Test
    fun `delete must return false if the user does not exists`() {
        every { repositoryMock.findByIdOrNull(1) } returns null
        controller.service.delete(1) shouldBe false
    }

    @Test
    fun `delete must call delete and return true if the user exists`() {
        val user = Stubs.userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        every { user.id?.let { repositoryMock.delete(it) } } returns null
        controller.service.delete(1) shouldBe true
    }
}