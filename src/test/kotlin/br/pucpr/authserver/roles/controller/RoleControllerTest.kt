package br.pucpr.authserver.roles.controller

import br.pucpr.authserver.roles.RoleService
import br.pucpr.authserver.roles.controller.requests.CreateRoleRequest
import br.pucpr.authserver.roles.controller.responses.RoleResponse
import br.pucpr.authserver.users.Stubs
import br.pucpr.authserver.users.Stubs.roleStub
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class RoleControllerTest {
    private val serviceMock = mockk<RoleService>()
    private val controller = RoleController(serviceMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(serviceMock)
    }

    @Test
    fun `insert must return the new role with CREATED code`() {
        val role = roleStub(id = 1)

        val request = CreateRoleRequest(role.name, role.description)
        every { serviceMock.insert(any()) } returns role
        with(controller.insert(request)) {
            statusCode shouldBe HttpStatus.CREATED
            body shouldBe RoleResponse(role)
        }
    }

    @Test
    fun `list should return all found roles`() {
        val roles = listOf(
            roleStub(1, "ADMIN"), Stubs.roleStub(2, "USER")
        )

        every { serviceMock.findAll() } returns roles
        with(controller.list()) {
            statusCode shouldBe HttpStatus.OK
            body shouldBe roles.map { RoleResponse(it) }
        }
    }
}