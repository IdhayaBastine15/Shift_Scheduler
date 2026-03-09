package com.shiftscheduler.service

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.domain.Role
import com.shiftscheduler.domain.Shift
import com.shiftscheduler.domain.ShiftStatus
import com.shiftscheduler.dto.CreateShiftRequest
import com.shiftscheduler.exception.BusinessRuleException
import com.shiftscheduler.repository.ShiftRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.math.BigDecimal

class ShiftServiceTest {

    private lateinit var shiftService: ShiftService
    private val shiftRepository: ShiftRepository = mockk()
    private val employeeService: EmployeeService = mockk()

    private val employee = Employee(
        id = 1L,
        name = "Alice Smith",
        email = "alice@example.com",
        role = Role.SERVER,
        hourlyRate = BigDecimal("15.00")
    )

    @BeforeEach
    fun setUp() {
        shiftService = ShiftService(shiftRepository, employeeService)
    }

    @Test
    fun `createShift throws BusinessRuleException when endTime is before startTime`() {
        val request = CreateShiftRequest(
            employeeId = 1L,
            startTime = LocalDateTime.of(2026, 3, 9, 17, 0),
            endTime = LocalDateTime.of(2026, 3, 9, 9, 0),
            roleAtShift = Role.SERVER
        )

        assertThatThrownBy { shiftService.createShift(request) }
            .isInstanceOf(BusinessRuleException::class.java)
            .hasMessageContaining("end time must be after start time")
    }

    @Test
    fun `createShift throws BusinessRuleException when shift overlaps with existing`() {
        val startTime = LocalDateTime.of(2026, 3, 9, 9, 0)
        val endTime = LocalDateTime.of(2026, 3, 9, 17, 0)

        val existingShift = Shift(
            id = 10L,
            employee = employee,
            startTime = startTime,
            endTime = endTime,
            roleAtShift = Role.SERVER
        )

        every { employeeService.findEmployeeOrThrow(1L) } returns employee
        every {
            shiftRepository.findOverlappingShifts(1L, startTime, endTime)
        } returns listOf(existingShift)

        val request = CreateShiftRequest(
            employeeId = 1L,
            startTime = startTime,
            endTime = endTime,
            roleAtShift = Role.SERVER
        )

        assertThatThrownBy { shiftService.createShift(request) }
            .isInstanceOf(BusinessRuleException::class.java)
            .hasMessageContaining("overlaps")
    }

    @Test
    fun `createShift saves and returns shift when valid`() {
        val startTime = LocalDateTime.of(2026, 3, 9, 9, 0)
        val endTime = LocalDateTime.of(2026, 3, 9, 17, 0)

        val savedShift = Shift(
            id = 1L,
            employee = employee,
            startTime = startTime,
            endTime = endTime,
            roleAtShift = Role.SERVER,
            status = ShiftStatus.SCHEDULED
        )

        every { employeeService.findEmployeeOrThrow(1L) } returns employee
        every { shiftRepository.findOverlappingShifts(1L, startTime, endTime) } returns emptyList()
        every { shiftRepository.save(any()) } returns savedShift

        val request = CreateShiftRequest(
            employeeId = 1L,
            startTime = startTime,
            endTime = endTime,
            roleAtShift = Role.SERVER
        )

        val result = shiftService.createShift(request)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.status).isEqualTo(ShiftStatus.SCHEDULED)
        verify(exactly = 1) { shiftRepository.save(any()) }
    }

    private fun assertThat(actual: Any?) = org.assertj.core.api.Assertions.assertThat(actual)
}
