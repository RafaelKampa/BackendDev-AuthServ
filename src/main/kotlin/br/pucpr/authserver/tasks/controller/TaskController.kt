package br.pucpr.authserver.tasks.controller

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.ForbiddenException
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
class TaskController(val service: TaskService, val userService: UserService) {

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/insertTask")
    fun insert(
        @Valid
        @RequestBody task: CreateOrUpdateTaskRequest): ResponseEntity<TaskResponse> {//TODO: Criar um BadRequest
        val conferentes =  task.conferente.mapNotNull { userService.findByIdOrNull(it) }
        val executor =  task.executor.mapNotNull { userService.findByIdOrNull(it) }
        val taskEntity = task.toTask()
        taskEntity.conferente.addAll(conferentes)
        taskEntity.executor.addAll(executor)
        return TaskResponse(service.insert(taskEntity))
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }


    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateTask/{id}")
    fun update(
        @Valid
        @RequestBody request: CreateOrUpdateTaskRequest,
        @PathVariable id: Long,
        auth: Authentication
    ): ResponseEntity <TaskResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (token.id != id && !token.isAdmin) throw ForbiddenException()

        return service.update(id)
            ?.let{ ResponseEntity.ok(TaskResponse(it)) }
            ?: ResponseEntity.noContent().build()
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