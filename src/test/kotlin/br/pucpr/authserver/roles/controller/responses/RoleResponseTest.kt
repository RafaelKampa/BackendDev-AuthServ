package br.pucpr.authserver.roles.controller.responses

import br.pucpr.authserver.users.Stubs.roleStub
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RoleResponseTest {
    @Test
    fun `constructor should copy all important values`() {
        val role = roleStub()
        val response = RoleResponse(role)
        response.name shouldBe role.name
        response.description shouldBe role.description
    }
}