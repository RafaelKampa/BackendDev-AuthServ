package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.Stubs.roleStub
import br.pucpr.authserver.users.Stubs.userStub
import br.pucpr.authserver.users.controller.responses.LoginResponse
import br.pucpr.authserver.users.controller.responses.UserResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.util.Optional

internal class UserServiceTest {
    private val repositoryMock = mockk<UserRepository>()
    private val roleRepositoryMock = mockk<RoleRepository>()
    private val jwt = mockk<Jwt>()

    private val service = UserService(repositoryMock, roleRepositoryMock, jwt)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(repositoryMock, roleRepositoryMock, jwt)
    }

    @Test
    fun `insert must throw BadRequestException if an user with the same email is found`() {
        val user = userStub(id = null)
        every { repositoryMock.findByEmail(user.email) } returns userStub()
        assertThrows<BadRequestException> {
            service.insert(user)
        } shouldHaveMessage "User already exists"
    }

    @Test
    fun `insert must return the saved user if it's inserted`() {
        val user = userStub(id = null)
        every { repositoryMock.findByEmail(user.email) } returns null

        val saved = userStub()
        every { repositoryMock.save(user) } returns saved
        service.insert(user) shouldBe saved
    }

    @Test
    fun `update must throw NotFoundException if the user does not exists`() {
        every { repositoryMock.findById(1) } returns Optional.empty()
        assertThrows<NotFoundException> {
            service.update(1, "name")
        }
    }

    @Test
    fun `update must return null if there's no changes`() {
        val user = userStub()
        every { repositoryMock.findById(1) } returns Optional.of(user)
        service.update(1, "user") shouldBe null
    }

    @Test
    fun `update update and save the user with slot and capture`() {
        val user = userStub()
        every { repositoryMock.findById(1) } returns Optional.of(user)

        val saved = userStub(1, "name")
        val slot = slot<User>()
        every { repositoryMock.save(capture(slot)) } returns saved

        service.update(1, "name") shouldBe saved
        slot.isCaptured shouldBe true
        slot.captured.name shouldBe "name"
    }

    @Test
    fun `update update and save the user with answers`() {
        every { repositoryMock.findById(1) } returns Optional.of(userStub())
        every { repositoryMock.save(any()) } answers { firstArg() }

        val saved = service.update(1, "name")!!
        saved.name shouldBe "name"
    }

    @Test
    fun `findAll should request an ascending list if SortDir ASC is used`() {
        val sortDir = SortDir.ASC
        val userList = listOf(userStub(1), userStub(2), userStub(3))
        every { repositoryMock.findAll(Sort.by("name").ascending()) } returns userList
        service.findAll(sortDir) shouldBe userList
    }

    @Test
    fun `findAll should request an descending list if SortDir DESC is used`() {
        val sortDir = SortDir.DESC
        val userList = listOf(userStub(1), userStub(2), userStub(3))
        every { repositoryMock.findAll(Sort.by("name").descending()) } returns userList
        service.findAll(sortDir) shouldBe userList
    }

    @Test
    fun `findByIdOrNull should delegate to repository`() {
        val user = userStub()
        every { repositoryMock.findById(1) } returns Optional.of(user)
        service.findByIdOrNull(1) shouldBe user
    }

    @Test
    fun `delete must return false if the user does not exists`() {
        every { repositoryMock.findById(1) } returns Optional.empty()
        service.delete(1) shouldBe false
    }

    @Test
    fun `delete must call delete and return true if the user exists`() {
        val user = userStub()
        every { repositoryMock.findById(1) } returns Optional.of(user)
        justRun { repositoryMock.delete(user) }
        service.delete(1) shouldBe true
    }

    @Test
    fun `delete should throw a BadRequestException if the user is the last admin`() {
        every { repositoryMock.findByIdOrNull(1) } returns userStub(roles = listOf("ADMIN"))
        every {
            repositoryMock.findByRole("ADMIN")
        } returns listOf(userStub(roles = listOf("ADMIN")))

        shouldThrow<BadRequestException> {
            service.delete(1)
        } shouldHaveMessage "Cannot delete the last system admin!"
    }

    @Test
    fun `findByRole must delegate to the repository`() {
        val users = listOf(userStub())
        every { repositoryMock.findByRole("role") } returns users
        service.findByRole("role") shouldBe users
    }

    @Test
    fun `addRole should throw NotFoundException if the user does not exists`() {
        every { repositoryMock.findById(1) } returns Optional.empty()
        assertThrows<NotFoundException> {
            service.addRole(1, "admin")
        }
    }

    @Test
    fun `addRole should throw BadRequestException if the role does not exist`() {
        every { repositoryMock.findById(1) } returns Optional.of(userStub())
        every { roleRepositoryMock.findByName("ADMIN") } returns null

        assertThrows<BadRequestException> {
            service.addRole(1, "ADMIN")
        } shouldHaveMessage "Invalid role: ADMIN"
    }

    @Test
    fun `addRole should return FALSE if the user already have the role`() {
        every { repositoryMock.findById(1) } returns Optional.of(userStub(roles = listOf("ADMIN")))
        service.addRole(1, "ADMIN") shouldBe false
    }

    @Test
    fun `addRole should return TRUE and save the user`() {
        val user = userStub()
        val role = roleStub(name = "ADMIN")

        every { repositoryMock.findById(1) } returns Optional.of(user)
        every { roleRepositoryMock.findByName("ADMIN") } returns role
        every { repositoryMock.save(user) } returns user

        service.addRole(1, "ADMIN") shouldBe true
        user.roles shouldContain role
    }

    @Test
    fun `login should return null if the user is not found`() {
        every { repositoryMock.findByEmail("email") } returns null
        service.login("email", "password") shouldBe null
    }

    @Test
    fun `login should return null if the password is wrong`() {
        val user = userStub()
        every { repositoryMock.findByEmail(user.email) } returns user
        service.login(user.email, "wrong") shouldBe null
    }

    @Test
    fun `login should return the login response if credentials are correct`() {
        val user = userStub()
        every { repositoryMock.findByEmail(user.email) } returns user
        every { jwt.createToken(user) } returns "token"

        service.login(user.email, user.password) shouldBe LoginResponse(
            token = "token",
            UserResponse(user)
        )
    }
}