package com.shiftscheduler.dto

import com.shiftscheduler.domain.Role
import com.shiftscheduler.domain.Shift
import com.shiftscheduler.domain.ShiftStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreateShiftRequest(
    @field:NotNull(message = "Employee ID is required")
    val employeeId: Long,

    @field:NotNull(message = "Start time is required")
    val startTime: LocalDateTime,

    @field:NotNull(message = "End time is required")
    val endTime: LocalDateTime,

    @field:NotNull(message = "Role is required")
    val roleAtShift: Role
)

data class UpdateShiftRequest(
    @field:NotNull(message = "Start time is required")
    val startTime: LocalDateTime,

    @field:NotNull(message = "End time is required")
    val endTime: LocalDateTime,

    @field:NotNull(message = "Role is required")
    val roleAtShift: Role
)

data class UpdateShiftStatusRequest(
    @field:NotNull(message = "Status is required")
    val status: ShiftStatus
)

data class ShiftResponse(
    val id: Long,
    val employeeId: Long,
    val employeeName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val roleAtShift: Role,
    val status: ShiftStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(shift: Shift) = ShiftResponse(
            id = shift.id,
            employeeId = shift.employee.id,
            employeeName = shift.employee.name,
            startTime = shift.startTime,
            endTime = shift.endTime,
            roleAtShift = shift.roleAtShift,
            status = shift.status,
            createdAt = shift.createdAt,
            updatedAt = shift.updatedAt
        )
    }
}
