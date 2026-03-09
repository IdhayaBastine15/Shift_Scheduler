package com.shiftscheduler.service

import com.shiftscheduler.domain.Shift
import com.shiftscheduler.dto.CreateShiftRequest
import com.shiftscheduler.dto.ShiftResponse
import com.shiftscheduler.dto.UpdateShiftRequest
import com.shiftscheduler.dto.UpdateShiftStatusRequest
import com.shiftscheduler.exception.BusinessRuleException
import com.shiftscheduler.exception.ResourceNotFoundException
import com.shiftscheduler.repository.ShiftRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ShiftService(
    private val shiftRepository: ShiftRepository,
    private val employeeService: EmployeeService
) {

    @Transactional(readOnly = true)
    fun getAllShifts(employeeId: Long? = null): List<ShiftResponse> {
        val shifts = if (employeeId != null) {
            shiftRepository.findAllByEmployeeId(employeeId)
        } else {
            shiftRepository.findAll()
        }
        return shifts.map { ShiftResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getShiftById(id: Long): ShiftResponse {
        val shift = findShiftOrThrow(id)
        return ShiftResponse.from(shift)
    }

    fun createShift(request: CreateShiftRequest): ShiftResponse {
        validateShiftTimes(request.startTime, request.endTime)
        val employee = employeeService.findEmployeeOrThrow(request.employeeId)
        checkForOverlappingShifts(request.employeeId, request.startTime, request.endTime, excludeShiftId = null)

        val shift = Shift(
            employee = employee,
            startTime = request.startTime,
            endTime = request.endTime,
            roleAtShift = request.roleAtShift
        )
        return ShiftResponse.from(shiftRepository.save(shift))
    }

    fun updateShift(id: Long, request: UpdateShiftRequest): ShiftResponse {
        val shift = findShiftOrThrow(id)
        validateShiftTimes(request.startTime, request.endTime)
        checkForOverlappingShifts(shift.employee.id, request.startTime, request.endTime, excludeShiftId = id)

        shift.startTime = request.startTime
        shift.endTime = request.endTime
        shift.roleAtShift = request.roleAtShift
        return ShiftResponse.from(shiftRepository.save(shift))
    }

    fun updateShiftStatus(id: Long, request: UpdateShiftStatusRequest): ShiftResponse {
        val shift = findShiftOrThrow(id)
        shift.status = request.status
        return ShiftResponse.from(shiftRepository.save(shift))
    }

    fun deleteShift(id: Long) {
        if (!shiftRepository.existsById(id)) {
            throw ResourceNotFoundException("Shift not found with id: $id")
        }
        shiftRepository.deleteById(id)
    }

    fun findShiftOrThrow(id: Long): Shift =
        shiftRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Shift not found with id: $id")
        }

    private fun validateShiftTimes(startTime: LocalDateTime, endTime: LocalDateTime) {
        if (!endTime.isAfter(startTime)) {
            throw BusinessRuleException("Shift end time must be after start time")
        }
    }

    private fun checkForOverlappingShifts(
        employeeId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        excludeShiftId: Long?
    ) {
        val overlapping = shiftRepository.findOverlappingShifts(employeeId, startTime, endTime)
            .filter { it.id != excludeShiftId }
        if (overlapping.isNotEmpty()) {
            throw BusinessRuleException(
                "Employee already has a scheduled shift that overlaps with the requested time window"
            )
        }
    }
}
