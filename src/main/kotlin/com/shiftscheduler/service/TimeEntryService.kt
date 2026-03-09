package com.shiftscheduler.service

import com.shiftscheduler.domain.TimeEntry
import com.shiftscheduler.dto.ClockInRequest
import com.shiftscheduler.dto.ClockOutRequest
import com.shiftscheduler.dto.TimeEntryResponse
import com.shiftscheduler.exception.BusinessRuleException
import com.shiftscheduler.exception.ResourceNotFoundException
import com.shiftscheduler.repository.TimeEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class TimeEntryService(
    private val timeEntryRepository: TimeEntryRepository,
    private val employeeService: EmployeeService,
    private val shiftService: ShiftService,
    private val laborCalculationService: LaborCalculationService
) {

    fun clockIn(request: ClockInRequest): TimeEntryResponse {
        val employee = employeeService.findEmployeeOrThrow(request.employeeId)

        val existing = timeEntryRepository.findByEmployeeIdAndClockOutIsNull(request.employeeId)
        if (existing != null) {
            throw BusinessRuleException("Employee ${employee.name} is already clocked in (entry id: ${existing.id})")
        }

        val shift = request.shiftId?.let { shiftService.findShiftOrThrow(it) }

        val entry = TimeEntry(
            employee = employee,
            shift = shift,
            clockIn = LocalDateTime.now()
        )
        return TimeEntryResponse.from(timeEntryRepository.save(entry))
    }

    fun clockOut(request: ClockOutRequest): TimeEntryResponse {
        val entry = timeEntryRepository.findByEmployeeIdAndClockOutIsNull(request.employeeId)
            ?: throw BusinessRuleException("No active clock-in found for employee id: ${request.employeeId}")

        val clockOutTime = LocalDateTime.now()
        entry.clockOut = clockOutTime
        entry.totalHours = laborCalculationService.calculateHoursWorked(
            entry.copy(clockOut = clockOutTime)
        )
        return TimeEntryResponse.from(timeEntryRepository.save(entry))
    }

    @Transactional(readOnly = true)
    fun getEntriesForEmployee(employeeId: Long): List<TimeEntryResponse> {
        if (!employeeService.run { findEmployeeOrThrow(employeeId); true }) return emptyList()
        return timeEntryRepository.findAllByEmployeeId(employeeId).map { TimeEntryResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getAllEntries(): List<TimeEntryResponse> =
        timeEntryRepository.findAll().map { TimeEntryResponse.from(it) }

    private fun TimeEntry.copy(clockOut: LocalDateTime?) = TimeEntry(
        id = this.id,
        employee = this.employee,
        shift = this.shift,
        clockIn = this.clockIn,
        clockOut = clockOut,
        totalHours = this.totalHours
    )
}
