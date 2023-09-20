package br.pucpr.authserver.roles.controller.responses

import br.pucpr.authserver.roles.Role

data class RoleResponse(
    val name: String,
    val description: String
) {
    constructor(role: Role) : this(name = role.name, description = role.description)
}
