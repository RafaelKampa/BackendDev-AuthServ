package br.pucpr.authserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(
    message: String = "Bad request",
    cause: Throwable? = null
): IllegalArgumentException(message, cause)