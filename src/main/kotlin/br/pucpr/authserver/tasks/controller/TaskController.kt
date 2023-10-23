package br.pucpr.authserver.tasks.controller

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.tasks.TaskService
import br.pucpr.authserver.tasks.controller.requests.CreateOrUpdateTaskRequest
import br.pucpr.authserver.tasks.controller.responses.TaskResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
class TaskController(val service: TaskService) {

    @SecurityRequirement(name="AuthServer")
    @PreAuthorize("hasRole('ADMIN')")
    //TODO: Verificar com o professor o porque o post de insert user não precisa de um caminho no @PostMapping
    @PostMapping("/insertTask")
    fun insert(@Valid @RequestBody task: CreateOrUpdateTaskRequest) =
        TaskResponse(service.insert(task.toTask()))
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

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
    //TODO: Ver com o professor sobre o caminho do Get para futura utilização no front
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