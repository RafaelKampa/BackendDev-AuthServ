package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.users.Stubs.userStub
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceTest {
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
    fun `insert must throw BadRequestException if an user with same email is found`() {
        val email = "teste@teste.com"
        every {
            repositoryMock.findByEmailOrNull(email)
        } returns Stubs.userStub(email=email)

        val error = assertThrows<BadRequestException> {
            service.insert(userStub(id=null, email=email))
        }
        error shouldHaveMessage "User already exists!"
    }

    @Test
    fun `insert must return the saved user if its inserted`() {
        val user = userStub(id=null)
        every { repositoryMock.findByEmailOrNull(user.email) } returns null

        val saved = userStub()
        every { repositoryMock.save(user) } returns saved
        service.insert(user) shouldBe saved
    }

    @Test
    fun `findAll should delegate to the repository`() {
        val userList = listOf(userStub(1), userStub(2))
        every { repositoryMock.findAll(SortDir.ASC) } returns userList
        service.findAll(SortDir.ASC) shouldBe userList
    }

    @Test
    fun `update must save the user with answers`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user
        every { repositoryMock.save(any()) } answers { firstArg() }

        val saved = service.update(1, "name")!!
        saved.name shouldBe "name"
    }

    @Test
    fun `update must save the user with slot`() {
        val user = userStub()
        every { repositoryMock.findByIdOrNull(1) } returns user

        val saved = service.update(1, "name")!!
        val userSlot = slot<User>()
        every { repositoryMock.save(capture(userSlot)) } returns saved

        val returned = service.update(1, "name")
        userSlot.isCaptured shouldBe true
        userSlot.captured.name shouldBe "name"

        returned shouldBe saved
    }

}