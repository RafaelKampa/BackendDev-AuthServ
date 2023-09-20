package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(val repository: UserRepository) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw BadRequestException("User already exists")
        }
        return repository.save(user)
            .also { log.info("User inserted: {}", it.id) }
    }

    fun update(id: Long, name: String): User? {
        val user = findByIdOrNull(id) ?: throw NotFoundException(id)
        if (user.name == name) return null
        user.name = name
        return repository.save(user)
    }

    fun findAll(dir: SortDir = SortDir.ASC): List<User> = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findByIdOrNull(id: Long) = repository.findById(id).getOrNull()
    fun delete(id: Long): Boolean {
        val user = findByIdOrNull(id) ?: return false
        repository.delete(user)
        log.info("User deleted: {}", user.id)
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}