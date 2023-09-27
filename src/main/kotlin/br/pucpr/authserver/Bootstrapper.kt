package br.pucpr.authserver

import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole =
            roleRepository.findByName("ADMIN") ?:
                roleRepository.save(Role(name = "ADMIN", description = "System administrator"))
                    .also {
                        roleRepository.save(Role(name = "USER", description = "Premium User"))
                        log.info("ADMIN and USER roles created")
                    }

        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email="admin@authserver.com",
                password="admin",
                name="Auth Server Administrator"
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
            log.info("Administrator created")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Bootstrapper::class.java)
    }
}