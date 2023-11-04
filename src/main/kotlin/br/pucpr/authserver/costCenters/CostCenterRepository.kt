package br.pucpr.authserver.costCenters

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CostCenterRepository : JpaRepository<CostCenter, Long> {

    @Query("select distinct c from CostCenter c" +
            " where c.nomeCentroDeCusto = :nomeCentroDeCusto")
    fun findCostCenterByName(nomeCentroDeCusto: String): CostCenter?

}