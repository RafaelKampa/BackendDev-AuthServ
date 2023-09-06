package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.Stubs.userStub
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UserServiceTest {
    private val repositoryMock = mockk<UserRepository>()
    private val service = UserService(repositoryMock)

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
        val user = userStub(id = null)
        every { repositoryMock.findByEmailOrNull(user.email) } returns userStub()
        assertThrows<BadRequestException> {
            service.insert(user)
        } shouldHaveMessage "User already exists"
    }

    @Test
    fun `insert must return the saved user if it's inserted`() {
        val user = userStub(id = null)
        every { repositoryMock.findByEmailOrNull(user.email) } returns null

        val saved = userStub()
        every { repositoryMock.save(user) } returns saved
        service.insert(user) shouldBe saved
    }

    @Test
    fun `update must throw NotFoundException if the user does not exists`() {
        every { repositoryMock.findByIdOrNull(1) } returns null
        assertThrows<NotFoundException> {
            service.update(1, "name")
        }
    }

    @Test
    fun `update must return null if there's no changes`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        service.update(1, "user") shouldBe null
    }

    @Test
    fun `update update and save the user with slot and capture`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user

        val saved = userStub(1, "name")
        val slot = slot<User>()
        every { repositoryMock.save(capture(slot)) } returns saved

        service.update(1, "name") shouldBe saved
        slot.isCaptured shouldBe true
        slot.captured.name shouldBe "name"
    }

    @Test
    fun `update update and save the user with answers`() {
        every { repositoryMock.findByIdOrNull(1) } returns userStub()
        every { repositoryMock.save(any()) } answers { firstArg() }

        val saved = service.update(1, "name")!!
        saved.name shouldBe "name"
    }

    @Test
    fun `findAll should delegate to repository`() {
        val sortDir = SortDir.values().random()
        val userList = listOf(userStub(1), userStub(2), userStub(3))
        every { repositoryMock.findAll(sortDir) } returns userList
        service.findAll(sortDir) shouldBe userList
    }

    @Test
    fun `findByIdOrNull should delegate to repository`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        service.findByIdOrNull(1) shouldBe user
    }

    @Test
    fun `delete must return false if the user does not exists`() {
        every { repositoryMock.findByIdOrNull(1) } returns null
        service.delete(1) shouldBe false
    }

    @Test
    fun `delete must call delete and return true if the user exists`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        every { repositoryMock.delete(user) } returns null
        service.delete(1) shouldBe true
    }
}