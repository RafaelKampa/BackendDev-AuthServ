package br.pucpr.authserver.roles.controller.requests

import br.pucpr.authserver.users.Stubs.roleStub
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CreateRoleRequestTest {
    @Test
    fun `toRole creates a new role based on the request`() {
        with(roleStub()) {
            val req = CreateRoleRequest(name, description).toRole()
            req.id shouldBe null
            req.name shouldBe name
            req.description shouldBe description
        }
    }
}