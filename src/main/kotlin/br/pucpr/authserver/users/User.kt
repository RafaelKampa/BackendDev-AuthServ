package br.pucpr.authserver.users

class User(
    var id: Long? = null,
    var name: String,
    var email: String,
    var password: String
)