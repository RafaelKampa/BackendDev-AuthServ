package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role

object Stubs {
    fun userStub(
        id: Long? = 1,
        name: String = "user",
        password: String = "Str4ngP@ss!",
        email: String? = "user@email.com"
    ) = User(
        id = id,
        email = email ?: "$name@email.com",
        name = name,
        password = password
    )

    fun roleStub(
        id: Long? = 1,
        name: String = "USER",
        description: String = "Role description"
    ) = Role(id = id, name = name, description = description)
}