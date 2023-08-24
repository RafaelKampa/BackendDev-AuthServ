package br.pucpr.authserver.users

class User(
    var id: Long? = null,
    val name: String,
    val email: String,
    val password: String
)