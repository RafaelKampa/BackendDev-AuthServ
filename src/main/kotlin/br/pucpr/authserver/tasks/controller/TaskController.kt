package br.pucpr.authserver.tasks.controller

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.costCenters.CostCenterRepository
import br.pucpr.authserver.costCenters.CostCenterService
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.tasks.TaskService
import br.pucpr.authserver.tasks.controller.requests.CreateOrUpdateTaskRequest
import br.pucpr.authserver.tasks.controller.responses.TaskResponse
import br.pucpr.authserver.users.UserService
import br.pucpr.authserver.users.controller.UserController
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(val service: TaskService, val userService: UserService, val costCenterService: CostCenterService) {

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/insertTask")
    fun insert(
        @Valid
        @RequestBody task: CreateOrUpdateTaskRequest): ResponseEntity<TaskResponse> {

        val centroDeCusto = task.centroDeCustoId?.let {
            costCenterService.findByIdOrNull(it)
        } ?: throw NotFoundException("CostCenter not found!")

        val executor =  task.executor.mapNotNull { userService.findByIdOrNull(it) }
        val conferente =  task.conferente.mapNotNull { userService.findByIdOrNull(it) }
        val taskEntity = task.toTask()

        taskEntity.centroDeCusto = centroDeCusto
        taskEntity.executor.addAll(executor)
        taskEntity.conferente.addAll(conferente)

        return TaskResponse(service.insert(taskEntity))
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }


    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    fun update(
        @Valid
        @RequestBody request: CreateOrUpdateTaskRequest,
        @PathVariable id: Long,
    ): ResponseEntity <TaskResponse> {
        val executor =  request.executor.mapNotNull { userService.findByIdOrNull(it) }
        val conferente =  request.conferente.mapNotNull { userService.findByIdOrNull(it) }
        val taskEntity = request.toTask()

        taskEntity.executor.addAll(executor)
        taskEntity.conferente.addAll(conferente)

        return service.update(id, taskEntity)
            .let{ ResponseEntity.ok(TaskResponse(it)) }
    }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun list(@RequestParam sortDir: String? = null) =
        service.findAll(SortDir.findOrThrow(sortDir ?: "ASC"))
        .map { TaskResponse(it) }.let { ResponseEntity.ok(it) }

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteTask/{idUser}/{idTask}")
    fun delete(@PathVariable idUser: Long, @PathVariable idTask: Long): ResponseEntity<Void> =
        if (service.delete(idUser, idTask)) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
}