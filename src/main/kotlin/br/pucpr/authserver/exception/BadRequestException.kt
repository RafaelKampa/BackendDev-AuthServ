package br.pucpr.authserver.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(BAD_REQUEST)
class BadRequestException(
    message: String = "Bad request",
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)