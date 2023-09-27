package br.pucpr.authserver.roles

import br.pucpr.authserver.users.User
import jakarta.persistence.*

@Entity
class Role(
    @Id @GeneratedValue
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String = "",

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()
)