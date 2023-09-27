package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.security.Jwt.Companion.createAuthentication
import br.pucpr.authserver.security.UserToken

object Stubs {
    fun userStub(
        id: Long? = 1,
        name: String = "user",
        password: String = "Str4ngP@ss!",
        email: String? = "user@email.com",
        roles: List<String> = listOf()
    ) = User(
        id = id,
        email = email ?: "$name@email.com",
        name = name,
        password = password,
        roles = roles
            .mapIndexed { i, it -> Role(i.toLong(), it, "$it role") }
            .toMutableSet()
    )

    fun adminStub() = userStub(
        id = 1000,
        email = "admin@authserver.com",
        password = "admin",
        name = "Auth Server Administrator",
        roles = listOf("ADMIN")
    )

    fun roleStub(
        id: Long? = 1,
        name: String = "USER",
        description: String = "Role description"
    ) = Role(id = id, name = name, description = description)

    fun authStub(
        user: User,
    ) = createAuthentication(UserToken(user))
}