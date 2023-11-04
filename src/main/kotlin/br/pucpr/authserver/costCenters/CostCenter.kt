package br.pucpr.authserver.costCenters

import jakarta.persistence.*


@Entity
@Table(name = "TblCostCenter")
class CostCenter(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(name = "NOME_CENTRO_DE_CUSTO", nullable = false)
    var nomeCentroDeCusto: String,

    @Column(name = "ENDERECO", nullable = false)
    var enderecoCentroDeCusto: String,

    @Column(name = "VALOR_EMPREENDIDO")
    var valorEmpreendido: Double
) {}