package br.pucpr.authserver.tasks

import br.pucpr.authserver.users.User
import jakarta.persistence.*

@Entity
@Table(name = "TaskUserRelationship")
class TaskUserRelationship(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "idTask")
    var task: Task,

    @ManyToOne
    @JoinColumn(name = "idUser")
    var user: User
)
