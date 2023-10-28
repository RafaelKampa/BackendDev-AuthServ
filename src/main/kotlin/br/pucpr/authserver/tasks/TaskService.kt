package br.pucpr.authserver.tasks

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import br.pucpr.authserver.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService (
    val repository: TaskRepository,
    val userService: UserService,
    val userRepository: UserRepository,
) {

    companion object {
        private val log = LoggerFactory.getLogger(TaskService::class.java)
    }

    fun insert(task: Task): Task {
        val executor =  task.executor.mapNotNull { it.id?.let { it1 -> userService.findByIdOrNull(it1) } }
        val conferente =  task.conferente.mapNotNull { it.id?.let { it1 -> userService.findByIdOrNull(it1) } }
        var conferenteIsAdm: User = conferente.first()

        if (executor.isEmpty()) {
            throw BadRequestException("ID Executor not found!")
        }
        if (conferente.isEmpty()) {
            throw BadRequestException("ID Conferente not found!")
        }
        if(!conferenteIsAdm.isAdmin) {
            throw BadRequestException("The 'Conferente' does not have Administrator permission!")
        }

        task.executor = executor.toMutableSet()
        task.conferente = conferente.toMutableSet()

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