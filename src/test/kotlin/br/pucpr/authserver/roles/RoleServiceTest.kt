package br.pucpr.authserver.roles

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.users.Stubs.roleStub
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort

class RoleServiceTest {
    private val repositoryMock = mockk<RoleRepository>()
    private val service = RoleService(repositoryMock)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun cleanUp() {
        checkUnnecessaryStub(repositoryMock)
    }

    @Test
    fun `insert must throw BadRequestException if a role with the same name is found`() {
        val role = roleStub(id = null)
        every { repositoryMock.findByName(role.name) } returns roleStub()
        assertThrows<BadRequestException> {
            service.insert(role)
        } shouldHaveMessage "Role already exists"
    }

    @Test
    fun `insert must return the saved role if it's inserted`() {
        val role = roleStub(id = null)
        every { repositoryMock.findByName(role.name) } returns null

        val saved = roleStub()
        every { repositoryMock.save(role) } returns saved
        service.insert(role) shouldBe saved
    }

    @Test
    fun `findAll should delegate to repository sorting by name`() {
        val users = listOf(roleStub())
        every { repositoryMock.findAll(Sort.by("name").ascending()) } returns users
        service.findAll() shouldBe users
    }
}