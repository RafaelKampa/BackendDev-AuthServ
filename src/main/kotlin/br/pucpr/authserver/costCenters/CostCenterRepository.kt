package br.pucpr.authserver.costCenters

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CostCenterRepository : JpaRepository<CostCenter, Long> {

    @Query("select distinct c from CostCenter c where c.nomeCentroDeCusto = :nomeCentroDeCusto")
    fun findCostCenterByName(nomeCentroDeCusto: String): CostCenter?

    @Transactional
    @Modifying
    @Query("update CostCenter c set c.valorEmpreendido = c.valorEmpreendido - :amount where c.id = :costCenterId")
    fun decreaseValueUndertaken(costCenterId: Long, amount: Double): Int

}