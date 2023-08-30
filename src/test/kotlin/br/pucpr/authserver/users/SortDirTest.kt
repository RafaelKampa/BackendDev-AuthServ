package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SortDirTest {
    @Test
    fun `findOrThrow must throw BadRequestException if an invalid value is passed`() {
        assertThrows<BadRequestException> {
            SortDir.findOrThrow("invalid")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["ASC", "asc", "Asc"])
    fun `findOrThrow must return ASC ignoring case`(value: String) {
        SortDir.findOrThrow(value) shouldBe SortDir.ASC
    }

    @ParameterizedTest
    @ValueSource(strings = ["DESC", "desc", "Desc"])
    fun `findOrThrow must return DESC ignoring case`(value: String) {
        SortDir.findOrThrow(value) shouldBe SortDir.DESC
    }

}