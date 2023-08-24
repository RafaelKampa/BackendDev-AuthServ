package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(val repository: UserRepository) {
    fun insert(user: User): User {
        if (repository.findByEmailOrNull(user.email) != null) {
            throw BadRequestException("User already exists!")
        }

        val saved = repository.save(user)
        log.info("User {} inserted", saved.id)
        return saved
    }

    fun findAll(sortDir: SortDir) = repository.findAll(sortDir)
    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)
    fun delete(id: Long): Boolean {
        val user = repository.findByIdOrNull(id) ?: return false
        repository.delete(user)
        log.info("User {} deleted", id)
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}