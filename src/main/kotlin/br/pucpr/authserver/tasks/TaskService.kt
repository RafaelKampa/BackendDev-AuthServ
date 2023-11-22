package br.pucpr.authserver.tasks

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.costCenters.CostCenterRepository
import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class TaskService (
    val repository: TaskRepository,
    val userRepository: UserRepository,
    val costCenterRepository: CostCenterRepository
) {

    fun insert(task: Task): Task {
        val centroDeCustoId = task.centroDeCusto?.id

        val centroDeCusto = centroDeCustoId?.let {
            costCenterRepository.findById(it)
                .orElseThrow { NotFoundException("Cost center not found with ID: $centroDeCustoId") }
        }
        
        val executor =  task.executor.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }
        val conferente =  task.conferente.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }

        if (centroDeCusto == null) {
            throw NotFoundException("Cost center not found!")
        }

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

        task.centroDeCusto = centroDeCusto
        task.executor = executor.toMutableSet()
        task.conferente = conferente.toMutableSet()

        return repository.save(task)
            .also{ log.info("Task inserted: {}", it.id) }
    }

    fun findByIdOrNull(id: Long) = repository.findById(id).getOrNull()

    fun update(id: Long, request: Task): Task? {
        val centroDeCustoId = request.centroDeCusto?.id

        val centroDeCusto = centroDeCustoId?.let {
            costCenterRepository.findById(it)
                .orElseThrow { NotFoundException("Centro de Custo not found with ID: $centroDeCustoId") }
        }

        val executor =  request.executor.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }
        val conferente =  request.conferente.mapNotNull { it.id?.let { it1 -> userRepository.findByIdOrNull(it1) } }

        if (centroDeCusto == null) {
            throw NotFoundException("Cost center not found!")
        }

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

        var taskAntiga = findByIdOrNull(id)
        if (taskAntiga == request) return null

        request.centroDeCusto = centroDeCusto
        request.executor = executor.toMutableSet()
        request.conferente = conferente.toMutableSet()

        return repository.save(request)
            .also{ log.info("Task updated: {}", it.id) }
    }

    fun findAll(dir: SortDir = SortDir.ASC): List<Task> = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("dataInicio").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("dataInicio").descending())
    }

    fun delete(idTask: Long): Boolean {
        val task = repository.findByIdOrNull(idTask) ?: return false
        //Não consegui fazer isso funcionar!!!
        //Remove o valor total do serviço no centro de custo antes de deletar a task
//        costCenterRepository.decreaseValueUndertaken(task.centroDeCusto!!.id!!, task.valorTotal)

        repository.delete(task)
        log.info("Task deleted: {}", task.id)
        return true
    }

    fun findByUserName(userName: String, sortDir: String?): List<Task> {
        val sort = if (sortDir == "DESC") Sort.by(Sort.Order.desc("id")) else Sort.by(Sort.Order.asc("id"))
        return repository.findByUserName(userName, sort)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TaskService::class.java)
    }
}