package com.shiftscheduler.dto

import com.shiftscheduler.domain.TimeEntry
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class ClockInRequest(
    @field:NotNull(message = "Employee ID is required")
    val employeeId: Long,

    val shiftId: Long? = null
)

data class ClockOutRequest(
    @field:NotNull(message = "Employee ID is required")
    val employeeId: Long
)

data class TimeEntryResponse(
    val id: Long,
    val employeeId: Long,
    val employeeName: String,
    val shiftId: Long?,
    val clockIn: LocalDateTime,
    val clockOut: LocalDateTime?,
    val totalHours: BigDecimal?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entry: TimeEntry) = TimeEntryResponse(
            id = entry.id,
            employeeId = entry.employee.id,
            employeeName = entry.employee.name,
            shiftId = entry.shift?.id,
            clockIn = entry.clockIn,
            clockOut = entry.clockOut,
            totalHours = entry.totalHours,
            createdAt = entry.createdAt
        )
    }
}
