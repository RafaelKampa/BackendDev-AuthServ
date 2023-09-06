package br.pucpr.authserver.users

import jakarta.persistence.*

@Entity
@Table(name="TblUser")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    var name: String,

    @Column(unique = true)
    val email: String,

    val password: String
)
