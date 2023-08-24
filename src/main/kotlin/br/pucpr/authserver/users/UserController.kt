package br.pucpr.authserver.users

import br.pucpr.authserver.users.controller.requests.CreateUserRequest
import br.pucpr.authserver.users.controller.responses.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(val service: UserService) {
    @PostMapping
    fun insert(@RequestBody @Valid user: CreateUserRequest) =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponse(service.insert(user.toUser())))

    @GetMapping
    fun list(@RequestParam sortDir: String?): ResponseEntity<List<UserResponse>> {
        val dir = sortDir?.uppercase() ?: "ASC"
        val dirEnum = SortDir.values().find { it.name == dir }
            ?: return ResponseEntity.badRequest().build()

        val users = service.findAll(dirEnum).map { UserResponse(it) }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.findByIdOrNull(id)
            ?.let { ResponseEntity.ok(UserResponse(it)) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        if (service.delete(id)) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
}