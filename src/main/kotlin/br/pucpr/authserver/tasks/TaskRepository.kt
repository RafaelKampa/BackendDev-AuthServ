package br.pucpr.authserver.tasks

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository <Task, Long> {

    @Query("select distinct t from Task t" +
            " join t.executor e" +
            " where e = :idExecutor")
    fun findByExecutor(idExecutor: Long): List<Task>

    @Query("select distinct t from Task t" +
            " join t.conferente c" +
            " where c = :idConferente")
    fun findByConferente(idConferente: Long): List<Task>

}