package br.pucpr.authserver.tasks

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository <Task, Long> {

    @Query("select distinct t from Task t" +
            " join t.conferente c" +
            " join t.executor e" +
            " where UPPER(c.name) like UPPER(CONCAT('%', :userName, '%')) or UPPER(e.name) like UPPER(CONCAT('%', :userName, '%'))")
    fun findByUserName(@Param("userName") username: String): List<Task>

}