package br.pucpr.authserver.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "TblUser")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true)
    var email: String,

    var password: String,

    var name: String,
)