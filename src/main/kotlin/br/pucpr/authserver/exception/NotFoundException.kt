package br.pucpr.authserver.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(NOT_FOUND)
class NotFoundException(
    message: String = "Not found",
    cause: Throwable? = null
) : IllegalArgumentException(message, cause) {
    constructor(id: Long) : this("Not found. id=$id")
}