package br.pucpr.authserver.users.controller

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.Stubs
import br.pucpr.authserver.users.Stubs.userStub
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserService
import br.pucpr.authserver.users.controller.requests.CreateUserRequest
import br.pucpr.authserver.users.controller.requests.PatchUserRequest
import br.pucpr.authserver.users.controller.responses.UserResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

class UserControllerTest {
    private val userServiceMock = mockk<UserService>()
    private val controller = UserController(userServiceMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(userServiceMock)
    }

    @Test
    fun `insert must return the new user with CREATED code`() {
        val user = userStub()
        val request = CreateUserRequest(user.email, user.password, user.name)
        every { userServiceMock.insert(any()) } returns user

        with(controller.insert(request)) {
            statusCode shouldBe HttpStatus.CREATED
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update must return OK if the user was updated`() {
        val user = userStub()
        val request = PatchUserRequest(user.name)
        every { userServiceMock.update(user.id!!, user.name) } returns user

        with(controller.update(user.id!!, request)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe UserResponse(user)
        }
    }

    @Test
    fun `update must return NO_CONTENT if the controller returns null`() {
        val user = userStub()
        val request = PatchUserRequest(user.name)
        every { userServiceMock.update(user.id!!, user.name) } returns null

        with(controller.update(user.id!!, request)) {
            statusCode shouldBe HttpStatus.NO_CONTENT
            body shouldBe null
        }
    }

    @Test
    fun `update must forward NotFoundException if the service throws it`() {
        val user = userStub()
        val request = PatchUserRequest(user.name)
        every { userServiceMock.update(user.id!!, user.name) } throws NotFoundException()

        assertThrows<NotFoundException> {
            controller.update(user.id!!, request)
        }
    }

    @Test
    fun `list should use ASC as the default parameter` () {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bianca")
        )
        every { userServiceMock.findAll(SortDir.ASC) } returns users
        with(controller.list(null)) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should return all found users with the fiven sort parameter` () {
        val users = listOf(
            userStub(1, "Ana"), userStub(2, "Bianca")
        )
        every { userServiceMock.findAll(SortDir.DESC) } returns users
        with(controller.list("DESC")) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe users.map { UserResponse(it) }
        }
    }

    @Test
    fun `list should throw BadRequestException for an invalid sort parameter` () {
        assertThrows<BadRequestException> {
            controller.list("INVALID")
        }
    }

//    @Test
//    fun `getById should throw OK code` () {
//        val user = userStub()
//        val request = PatchUserRequest(user.name)
//        every { controller.getById(user.id!!) } returns user
//
//        with(controller.getById(user.id!!)) {
//            statusCode shouldBe HttpStatus.OK
//            body shouldBe user
//        }
//    }

}