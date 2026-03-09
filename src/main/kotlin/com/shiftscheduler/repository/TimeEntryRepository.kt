package com.shiftscheduler.repository

import com.shiftscheduler.domain.TimeEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TimeEntryRepository : JpaRepository<TimeEntry, Long> {

    fun findAllByEmployeeId(employeeId: Long): List<TimeEntry>

    fun findByEmployeeIdAndClockOutIsNull(employeeId: Long): TimeEntry?

    @Query("""
        SELECT t FROM TimeEntry t
        WHERE t.employee.id = :employeeId
          AND t.clockIn >= :startDate
          AND t.clockIn < :endDate
        ORDER BY t.clockIn ASC
    """)
    fun findByEmployeeAndDateRange(
        @Param("employeeId") employeeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<TimeEntry>

    @Query("""
        SELECT t FROM TimeEntry t
        WHERE t.clockIn >= :startDate AND t.clockIn < :endDate
        ORDER BY t.clockIn ASC
    """)
    fun findAllByDateRange(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<TimeEntry>
}
