package br.pucpr.authserver.costCenters

import br.pucpr.authserver.SortDir
import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CostCenterService(
    val repository: CostCenterRepository
) {
    fun insert(costCenter: CostCenter): CostCenter {
        if (repository.findCostCenterByName(costCenter.nomeCentroDeCusto) != null) {
            throw BadRequestException("Cost Center already exists")
        }
        return repository.save(costCenter)
            .also { log.info("Cost Center inserted: {}", it.id) }
    }

    fun findByIdOrNull(id: Long) = repository.findById(id).getOrNull()

    fun findByIdOrThrow(id: Long) =
        findByIdOrNull(id) ?: throw NotFoundException(id)

    fun updateName(id: Long, name: String): CostCenter? {
        val costCenter = findByIdOrThrow(id)

        if (costCenter.nomeCentroDeCusto == name) return null
        costCenter.nomeCentroDeCusto = name

        return repository.save(costCenter)
    }

    fun updateAdress(id: Long, adress: String): CostCenter? {
        val costCenter = findByIdOrThrow(id)

        if (costCenter.enderecoCentroDeCusto == adress) return null
        costCenter.enderecoCentroDeCusto = adress

        return repository.save(costCenter)
    }

    fun increaseValueUndertaken(id: Long, value: Double): CostCenter? {
        val costCenter = findByIdOrThrow(id)

        if (value <= 0) return null
        costCenter.valorEmpreendido = costCenter.valorEmpreendido + value

        return repository.save(costCenter)
    }

    fun decreaseValueUndertaken(id: Long, value: Double): CostCenter? {
        val costCenter = findByIdOrThrow(id)

        if (value <= 0) return null
        costCenter.valorEmpreendido = costCenter.valorEmpreendido - value

        return repository.save(costCenter)
    }

    fun findAll(dir: SortDir = SortDir.ASC): List<CostCenter> = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("nomeCentroDeCusto").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("nomeCentroDeCusto").descending())
    }

    fun findByName(nomeCentroDeCusto: String): CostCenter? {
        return repository.findCostCenterByName(nomeCentroDeCusto) ?: throw NotFoundException("Cost Center not found: $nomeCentroDeCusto")
    }

    fun delete(id: Long): Boolean {
        val costCenter = findByIdOrNull(id) ?: return false

        if (costCenter.valorEmpreendido > 0) throw ForbiddenException("Cannot delete a Cost Center with value undertaken!")

        repository.delete(costCenter)
        log.info("Cost Center deleted: {}", costCenter.id)
        return true
    }

    companion object {
        private val log = LoggerFactory.getLogger(CostCenterService::class.java)
    }
}