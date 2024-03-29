package br.pucpr.authserver.tasks

import br.pucpr.authserver.costCenters.CostCenter
import br.pucpr.authserver.users.User
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TblTask")
class Task(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(name = "TIPO_SERVICO", nullable = false)
    var tipoServico: String,

    @Column(name = "VALOR_UNITARIO", nullable = false)
    var valorUnitario: Double,

    @Column(name = "DIMENSAO", nullable = false)
    var dimensao: Double,

    @Column(name = "UNIDADE_MEDIDA", nullable = false)
    var unidadeMedida: String,

    @ManyToOne
    @JoinColumn(name = "idCostCenter")
    var centroDeCusto: CostCenter? = null,

    @Column(name = "LOCAL_EXECUCAO", nullable = false)
    var localExecucao: String,

    @Column(name = "DATA_INICIO", nullable = false)
    var dataInicio: Date,

    @Column(name = "PREV_TERMINO", nullable = false)
    var previsaoTermino: Date,

    @Column(name = "DATA_FINAL")
    var dataFinal: Date?,

    @Column(name = "VALOR_TOTAL")
    var valorTotal: Double,

    @Column(name = "OBS")
    var obs: String?,

    @ManyToMany
    @JoinTable(
        name = "TaskExecutor",
        joinColumns = [JoinColumn(name = "idTask")],
        inverseJoinColumns = [JoinColumn(name = "idUser")]
    )
    var executor: MutableSet<User> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "TaskConferente",
        joinColumns = [JoinColumn(name = "idTask")],
        inverseJoinColumns = [JoinColumn(name = "idUser")]
    )
    var conferente: MutableSet<User> = mutableSetOf()


) {}