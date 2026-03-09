package com.shiftscheduler.service

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.domain.Role
import com.shiftscheduler.domain.TimeEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class LaborCalculationServiceTest {

    private lateinit var service: LaborCalculationService

    private val employee = Employee(
        id = 1L,
        name = "Alice Smith",
        email = "alice@example.com",
        role = Role.SERVER,
        hourlyRate = BigDecimal("15.00")
    )

    @BeforeEach
    fun setUp() {
        service = LaborCalculationService()
    }

    @Test
    fun `calculateHoursWorked returns zero when clockOut is null`() {
        val entry = TimeEntry(
            id = 1L,
            employee = employee,
            clockIn = LocalDateTime.now()
        )
        assertThat(service.calculateHoursWorked(entry)).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun `calculateHoursWorked returns correct hours for 8-hour shift`() {
        val clockIn = LocalDateTime.of(2026, 3, 9, 9, 0)
        val clockOut = LocalDateTime.of(2026, 3, 9, 17, 0)
        val entry = TimeEntry(
            id = 1L,
            employee = employee,
            clockIn = clockIn,
            clockOut = clockOut
        )
        assertThat(service.calculateHoursWorked(entry)).isEqualByComparingTo(BigDecimal("8.00"))
    }

    @Test
    fun `calculateHoursWorked handles partial hours correctly`() {
        val clockIn = LocalDateTime.of(2026, 3, 9, 9, 0)
        val clockOut = LocalDateTime.of(2026, 3, 9, 10, 30)
        val entry = TimeEntry(
            id = 1L,
            employee = employee,
            clockIn = clockIn,
            clockOut = clockOut
        )
        assertThat(service.calculateHoursWorked(entry)).isEqualByComparingTo(BigDecimal("1.50"))
    }

    @Test
    fun `calculateLaborCost returns correct amount`() {
        val hours = BigDecimal("8.00")
        val rate = BigDecimal("15.00")
        assertThat(service.calculateLaborCost(hours, rate)).isEqualByComparingTo(BigDecimal("120.00"))
    }

    @Test
    fun `buildLaborReport returns correct totals for completed entries`() {
        val clockIn = LocalDateTime.of(2026, 3, 9, 9, 0)
        val clockOut = LocalDateTime.of(2026, 3, 9, 17, 0)

        val entry = TimeEntry(
            id = 1L,
            employee = employee,
            clockIn = clockIn,
            clockOut = clockOut,
            totalHours = BigDecimal("8.00")
        )

        val report = service.buildLaborReport(
            listOf(entry),
            LocalDate.of(2026, 3, 9),
            LocalDate.of(2026, 3, 9)
        )

        assertThat(report.totalHoursWorked).isEqualByComparingTo(BigDecimal("8.00"))
        assertThat(report.totalLaborCost).isEqualByComparingTo(BigDecimal("120.00"))
        assertThat(report.byEmployee).hasSize(1)
        assertThat(report.byRole).hasSize(1)
        assertThat(report.byRole[0].role).isEqualTo(Role.SERVER)
    }

    @Test
    fun `buildLaborReport excludes entries without clockOut`() {
        val entry = TimeEntry(
            id = 1L,
            employee = employee,
            clockIn = LocalDateTime.now()
        )

        val report = service.buildLaborReport(
            listOf(entry),
            LocalDate.now(),
            LocalDate.now()
        )

        assertThat(report.totalHoursWorked).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(report.byEmployee).isEmpty()
    }

    @Test
    fun `buildLaborReport aggregates multiple employees correctly`() {
        val manager = Employee(
            id = 2L,
            name = "Bob Jones",
            email = "bob@example.com",
            role = Role.MANAGER,
            hourlyRate = BigDecimal("25.00")
        )

        val clockIn = LocalDateTime.of(2026, 3, 9, 9, 0)
        val clockOut = LocalDateTime.of(2026, 3, 9, 17, 0)

        val entries = listOf(
            TimeEntry(1L, employee, null, clockIn, clockOut, BigDecimal("8.00")),
            TimeEntry(2L, manager, null, clockIn, clockOut, BigDecimal("8.00"))
        )

        val report = service.buildLaborReport(entries, LocalDate.of(2026, 3, 9), LocalDate.of(2026, 3, 9))

        assertThat(report.totalHoursWorked).isEqualByComparingTo(BigDecimal("16.00"))
        assertThat(report.totalLaborCost).isEqualByComparingTo(BigDecimal("320.00"))
        assertThat(report.byEmployee).hasSize(2)
        assertThat(report.byRole).hasSize(2)
    }
}
