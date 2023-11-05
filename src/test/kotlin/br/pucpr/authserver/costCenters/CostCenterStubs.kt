package br.pucpr.authserver.costCenters

object CostCenterStubs {
    fun costStub(
        id: Long? = 1,
        nomeCentroDeCusto: String = "Centro de treinamento",
        enderecoCentroDeCusto: String = "Rua Fictícia, 1000",
        valorEmpreendido: Double = 3000.00
    ) = CostCenter(
        id = id,
        nomeCentroDeCusto = nomeCentroDeCusto,
        enderecoCentroDeCusto = enderecoCentroDeCusto,
        valorEmpreendido = valorEmpreendido
    )
}