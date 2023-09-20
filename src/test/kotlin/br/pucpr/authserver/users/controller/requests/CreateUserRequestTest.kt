package br.pucpr.authserver.users.controller.requests

import br.pucpr.authserver.users.Stubs.userStub
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CreateUserRequestTest {
    @Test
    fun `toUser creates a new user based on the request`() {
        with(userStub()) {
            val req = CreateUserRequest(email, password, name).toUser()
            req.id shouldBe null
            req.name shouldBe name
            req.password shouldBe password
            req.email shouldBe email
        }
    }
}