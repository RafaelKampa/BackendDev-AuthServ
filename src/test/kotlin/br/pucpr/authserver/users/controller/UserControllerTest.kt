package br.pucpr.authserver.users.controller

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.Stubs.adminStub
import br.pucpr.authserver.users.Stubs.authStub
import br.pucpr.authserver.users.Stubs.userStub
import br.pucpr.authserver.users.UserService
import br.pucpr.authserver.users.controller.requests.CreateUserRequest
import br.pucpr.authserver.users.controller.requests.LoginRequest
import br.pucpr.authserver.users.controller.requests.PatchUserRequest
import br.pucpr.authserver.users.controller.responses.LoginResponse
import br.pucpr.authserver.users.controller.responses.UserResponse
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

class UserControllerTest {
    private val serviceMock = mockk<UserService>()
    private val controller = UserController(serviceMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(serviceMock)
    }

    @Test
    fun `insert must return the new user with CREATED code`() {
        val user = userStub(id = 1)

        val request = CreateUserRequest(user.email, user.name, user.password)
        every { serviceMock.insert(any()) } returns user
        with(controller.insert(request)) {
            statusCode shouldBe HttpStatus.CREATED
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update should return NO_CONTENT if the service returns null`() {
        val user = userStub(id = 1)
        every { serviceMock.update(user.id!!, user.name) } returns null
        with(controller.update(user.id!!, PatchUserRequest(user.name), authStub(user))) {
            statusCode shouldBe HttpStatus.NO_CONTENT
            body shouldBe null
        }
    }

    @Test
    fun `update should work if the user is updating himself`() {
        val user = userStub(id = 1)
        val request = PatchUserRequest(user.name)

        every { serviceMock.update(user.id!!, user.name) } returns user
        with(controller.update(user.id!!, request, authStub(user))) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update should work if the ADMIN is updating any user`() {
        val user = userStub(id = 1)
        val request = PatchUserRequest(user.name)

        every { serviceMock.update(user.id!!, user.name) } returns user
        with(controller.update(user.id!!, request, authStub(adminStub()))) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update should throw ForbiddenException if the user is trying to update other user`() {
        val user = userStub(id = 1)
        val request = PatchUserRequest(user.name)

        assertThrows<ForbiddenException> {
            controller.update(user.id!!, request, authStub(userStub(id = 2)))
        }
    }


    @Test
    fun `update should forward NotFoundException if the user is not found`() {
        val user = userStub(id = 1)
        every { serviceMock.update(user.id!!, user.name) } throws NotFoundException()
        assertThrows<NotFoundException> {
            controller.update(user.id!!, PatchUserRequest(user.name), authStub(user))
        }
    }

    @Test
    fun `list should return all found users with the given sort parameter`() {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bruno")
        )

        every { serviceMock.findAll(SortDir.DESC) } returns users
        with(controller.list("DESC")) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should use ASC as default sort parameter`() {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bruno")
        )

        every { serviceMock.findAll(SortDir.ASC) } returns users
        with(controller.list(null)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should list by the role in uppercase if the role is provided`() {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bruno")
        )

        every { serviceMock.findByRole("USER") } returns users
        with(controller.list(role = "user")) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should ignore the sort parameter if the role is provided`() {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bruno")
        )

        every { serviceMock.findByRole("USER") } returns users
        with(controller.list(sortDir = "ASC", role = "user")) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should throw BadRequestException with a invalid sort parameter`() {
        assertThrows<BadRequestException> {
            controller.list("INVALID")
        }
    }

    @Test
    fun `getById must returns the user`() {
        val user = userStub(id = 1)
        every { serviceMock.findByIdOrNull(user.id!!) } returns user
        with(controller.getById(user.id!!)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `getById must return NOT FOUND if the user is not found`() {
        every { serviceMock.findByIdOrNull(1) } returns null
        controller.getById(1).statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `delete should return OK if the user gets deleted`() {
        every { serviceMock.delete(1) } returns true
        with(controller.delete(1)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe null
        }
    }

    @Test
    fun `delete should return NOT_FOUND if the user does not exists`() {
        every { serviceMock.delete(1) } returns false
        controller.delete(1).statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `login should return the user if the credentials are valid`() {
        every { serviceMock.login("email", "password") } returns null
        with(controller.login(LoginRequest("email", "password"))) {
            statusCode shouldBe HttpStatus.UNAUTHORIZED
            body shouldBe null
        }
    }

    @Test
    fun `login should return UNAUTHORIZED if the credentials are invalid`() {
        val user = userStub()
        val response = LoginResponse(token = "token", UserResponse(user))
        every { serviceMock.login(user.email, user.password) } returns response

        with(controller.login(LoginRequest(user.email, user.password))) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe response
        }
    }
}