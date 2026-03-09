package com.shiftscheduler.dto

import com.shiftscheduler.domain.Role
import java.math.BigDecimal
import java.time.LocalDate

data class LaborReportRequest(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class EmployeeLaborSummary(
    val employeeId: Long,
    val employeeName: String,
    val role: Role,
    val hourlyRate: BigDecimal,
    val totalHoursWorked: BigDecimal,
    val totalLaborCost: BigDecimal
)

data class RoleLaborSummary(
    val role: Role,
    val employeeCount: Int,
    val totalHoursWorked: BigDecimal,
    val totalLaborCost: BigDecimal
)

data class LaborCostReport(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalHoursWorked: BigDecimal,
    val totalLaborCost: BigDecimal,
    val byEmployee: List<EmployeeLaborSummary>,
    val byRole: List<RoleLaborSummary>
)
