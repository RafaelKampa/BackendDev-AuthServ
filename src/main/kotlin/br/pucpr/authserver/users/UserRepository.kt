package br.pucpr.authserver.users

import org.springframework.stereotype.Component

@Component
class UserRepository {
    private val users = mutableMapOf<Long, User>()

    fun save(user: User): User {
        if (user.id == null) {
            lastId += 1
            user.id = lastId
        }
        users[user.id!!] = user
        return user
    }

    fun findAll(dir: SortDir) =
        when (dir) {
            SortDir.ASC -> users.values.sortedBy { it.name }
            SortDir.DESC -> users.values.sortedByDescending { it.name }
        }


    fun findByIdOrNull(id: Long) = users[id]
    fun findByEmailOrNull(email: String) = users.values.find { it.email == email }

    fun delete(user: User) = users.remove(user.id)

    companion object {
        private var lastId: Long = 0
    }
}