package br.pucpr.authserver.users.controller.responses

import br.pucpr.authserver.users.Stubs.userStub
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserResponseTest {
    @Test
    fun `constructor should copy all important values`() {
        val user = userStub()
        val response = UserResponse(user)
        response.id shouldBe user.id
        response.name shouldBe user.name
        response.email shouldBe user.email
    }
}