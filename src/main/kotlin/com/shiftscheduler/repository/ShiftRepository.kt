package com.shiftscheduler.repository

import com.shiftscheduler.domain.Shift
import com.shiftscheduler.domain.ShiftStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ShiftRepository : JpaRepository<Shift, Long> {

    fun findAllByEmployeeId(employeeId: Long): List<Shift>

    fun findAllByStatus(status: ShiftStatus): List<Shift>

    @Query("""
        SELECT s FROM Shift s
        WHERE s.employee.id = :employeeId
          AND s.status != 'CANCELLED'
          AND (s.startTime < :endTime AND s.endTime > :startTime)
    """)
    fun findOverlappingShifts(
        @Param("employeeId") employeeId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<Shift>

    @Query("""
        SELECT s FROM Shift s
        WHERE s.startTime >= :startDate AND s.startTime < :endDate
        ORDER BY s.startTime ASC
    """)
    fun findAllByDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Shift>

    @Query("""
        SELECT s FROM Shift s
        WHERE s.employee.id = :employeeId
          AND s.startTime >= :startDate
          AND s.startTime < :endDate
        ORDER BY s.startTime ASC
    """)
    fun findByEmployeeAndDateRange(
        @Param("employeeId") employeeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Shift>
}
