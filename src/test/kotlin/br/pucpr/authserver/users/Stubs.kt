package br.pucpr.authserver.users

object Stubs {
    fun userStub(
        id: Long? = 1,
        name: String = "user",
        password: String = "Str4ngP@ss!",
        email: String = "user@email.com"
    ) = User(
        id = id,
        name = name,
        password = password,
        email = email
    )
}