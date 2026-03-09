package com.shiftscheduler.dto

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.domain.Role
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateEmployeeRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = 150)
    val email: String,

    @field:NotNull(message = "Role is required")
    val role: Role,

    @field:NotNull(message = "Hourly rate is required")
    @field:DecimalMin(value = "0.01", message = "Hourly rate must be greater than 0")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid hourly rate format")
    val hourlyRate: BigDecimal
)

data class UpdateEmployeeRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotNull(message = "Role is required")
    val role: Role,

    @field:NotNull(message = "Hourly rate is required")
    @field:DecimalMin(value = "0.01")
    @field:Digits(integer = 8, fraction = 2)
    val hourlyRate: BigDecimal
)

data class EmployeeResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role,
    val hourlyRate: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(employee: Employee) = EmployeeResponse(
            id = employee.id,
            name = employee.name,
            email = employee.email,
            role = employee.role,
            hourlyRate = employee.hourlyRate,
            createdAt = employee.createdAt,
            updatedAt = employee.updatedAt
        )
    }
}
