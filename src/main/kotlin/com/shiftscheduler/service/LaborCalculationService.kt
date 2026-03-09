package com.shiftscheduler.service

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.domain.TimeEntry
import com.shiftscheduler.dto.EmployeeLaborSummary
import com.shiftscheduler.dto.LaborCostReport
import com.shiftscheduler.dto.RoleLaborSummary
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate

@Service
class LaborCalculationService {

    fun calculateHoursWorked(entry: TimeEntry): BigDecimal {
        val clockOut = entry.clockOut ?: return BigDecimal.ZERO
        val minutes = Duration.between(entry.clockIn, clockOut).toMinutes()
        return BigDecimal(minutes).divide(BigDecimal(60), 2, RoundingMode.HALF_UP)
    }

    fun calculateLaborCost(hoursWorked: BigDecimal, hourlyRate: BigDecimal): BigDecimal =
        hoursWorked.multiply(hourlyRate).setScale(2, RoundingMode.HALF_UP)

    fun buildLaborReport(
        entries: List<TimeEntry>,
        startDate: LocalDate,
        endDate: LocalDate
    ): LaborCostReport {
        val completedEntries = entries.filter { it.clockOut != null }

        val byEmployee = completedEntries
            .groupBy { it.employee }
            .map { (employee, empEntries) -> buildEmployeeSummary(employee, empEntries) }
            .sortedBy { it.employeeName }

        val byRole = byEmployee
            .groupBy { it.role }
            .map { (role, summaries) ->
                RoleLaborSummary(
                    role = role,
                    employeeCount = summaries.size,
                    totalHoursWorked = summaries.fold(BigDecimal.ZERO) { acc, s -> acc + s.totalHoursWorked },
                    totalLaborCost = summaries.fold(BigDecimal.ZERO) { acc, s -> acc + s.totalLaborCost }
                )
            }
            .sortedBy { it.role.name }

        val totalHours = byEmployee.fold(BigDecimal.ZERO) { acc, s -> acc + s.totalHoursWorked }
        val totalCost = byEmployee.fold(BigDecimal.ZERO) { acc, s -> acc + s.totalLaborCost }

        return LaborCostReport(
            startDate = startDate,
            endDate = endDate,
            totalHoursWorked = totalHours,
            totalLaborCost = totalCost,
            byEmployee = byEmployee,
            byRole = byRole
        )
    }

    private fun buildEmployeeSummary(employee: Employee, entries: List<TimeEntry>): EmployeeLaborSummary {
        val totalHours = entries.fold(BigDecimal.ZERO) { acc, entry ->
            acc + (entry.totalHours ?: calculateHoursWorked(entry))
        }
        val totalCost = calculateLaborCost(totalHours, employee.hourlyRate)
        return EmployeeLaborSummary(
            employeeId = employee.id,
            employeeName = employee.name,
            role = employee.role,
            hourlyRate = employee.hourlyRate,
            totalHoursWorked = totalHours,
            totalLaborCost = totalCost
        )
    }
}
