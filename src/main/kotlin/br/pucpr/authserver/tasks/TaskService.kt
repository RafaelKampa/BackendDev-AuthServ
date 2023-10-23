package br.pucpr.authserver.tasks

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class TaskService (
    val repository: TaskRepository,
    val userRepository: UserRepository,
) {

    companion object {
        private val log = LoggerFactory.getLogger(TaskService::class.java)
    }

    fun insert(task: Task): Task {
        return repository.save(task)
            .also{ log.info("Task inserted: {}", it.id) }
    }

    fun update(id: Long): Task {
        val task = repository.findById(id).
            orElseThrow{ NotFoundException("Task not found!") }
        return repository.save(task)
            .also{ log.info("Task updated: {}", it.id) }
    }

    fun findAll(dir: SortDir = SortDir.ASC): List<Task> = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("dataInicio").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("dataInicio").descending())
    }

    fun delete(idUser: Long, idTask: Long): Boolean {
        val user = userRepository.findById(idUser).
                orElseThrow { NotFoundException("User not found!") }
        val task = repository.findById(idTask).
            orElseThrow { NotFoundException("Task not found!") }
        if (user.roles.any { it.name == "ADMIN" }) {
            repository.delete(task)
            log.info("Task deleted: {}", task.id)
            return true
        }
        log.info("This user cannot delete a task!")
        return false
    }
}