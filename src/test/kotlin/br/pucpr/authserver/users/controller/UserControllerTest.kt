package br.pucpr.authserver.users.controller

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.Stubs.userStub
import br.pucpr.authserver.users.UserService
import br.pucpr.authserver.users.controller.requests.CreateUserRequest
import br.pucpr.authserver.users.controller.requests.PatchUserRequest
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
        with(controller.update(user.id!!, PatchUserRequest(user.name))) {
            statusCode shouldBe HttpStatus.NO_CONTENT
            body shouldBe null
        }
    }

    @Test
    fun `update should return OK with the updated user if the service updates it`() {
        val user = userStub(id = 1)
        val request = PatchUserRequest(user.name)

        every { serviceMock.update(user.id!!, user.name) } returns user
        with(controller.update(user.id!!, request)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update should forward NotFoundException if the user is not found`() {
        val user = userStub(id = 1)
        every { serviceMock.update(user.id!!, user.name) } throws NotFoundException()
        assertThrows<NotFoundException> {
            controller.update(user.id!!, PatchUserRequest(user.name))
        }
    }

    @Test
    fun `list should return all found users with the given parameter`() {
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
    fun `list should use ASC as default parameter`() {
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
}