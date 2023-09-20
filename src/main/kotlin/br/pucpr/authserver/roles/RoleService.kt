package br.pucpr.authserver.roles

import br.pucpr.authserver.exception.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class RoleService(val repository: RoleRepository) {
    fun insert(role: Role): Role {
        if (repository.findByName(role.name) != null) {
            throw BadRequestException("Role already exists")
        }
        return repository.save(role)
            .also { log.info("Role inserted: {}", role.name) }
    }

    fun findAll(): List<Role> = repository.findAll(Sort.by("name").ascending())

    companion object {
        private val log = LoggerFactory.getLogger(RoleService::class.java)
    }
}