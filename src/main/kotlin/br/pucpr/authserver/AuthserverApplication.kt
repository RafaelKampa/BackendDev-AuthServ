package br.pucpr.authserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthserverApplication

fun main(args: Array<String>) {
	runApplication<AuthserverApplication>(*args)
}