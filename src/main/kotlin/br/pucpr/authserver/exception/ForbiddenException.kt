package br.pucpr.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(
    message: String = "Forbidden",
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)