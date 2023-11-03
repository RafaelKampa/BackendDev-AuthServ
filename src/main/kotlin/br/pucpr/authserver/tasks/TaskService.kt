package br.pucpr.authserver.tasks

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService (
    val repository: TaskRepository,
    val userRepository: UserRepository,
) {

    companion object {
        private val log = LoggerFactory.getLogger(TaskService::class.java)
    }

    fun insert(task: Task): Task {
        val executor =  task.executor.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }
        val conferente =  task.conferente.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }

        if (executor.isEmpty()) {
            throw NotFoundException("Executor not found!")
        }
        if (conferente.isEmpty()) {
            throw NotFoundException("Conferente not found!")
        }

        val nonAdminConferentes = conferente.filter { !it.isAdmin }

        if (nonAdminConferentes.isNotEmpty()) {
            val usernames = nonAdminConferentes.joinToString { it.name }
            throw ForbiddenException("The following 'Conferente' users do not have Administrator permission: $usernames")
        }

        task.executor = executor.toMutableSet()
        task.conferente = conferente.toMutableSet()

        return repository.save(task)
            .also{ log.info("Task inserted: {}", it.id) }
    }

    fun update(id: Long, request: Task): Task {
        var task = repository.findById(id).
            orElseThrow{ NotFoundException("Task not found!") }
        task = request
        task.id = id

        val executor =  task.executor.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }
        val conferente =  task.conferente.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }
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
        throw BadRequestException("This user cannot delete a task!")
        return false
    }
}