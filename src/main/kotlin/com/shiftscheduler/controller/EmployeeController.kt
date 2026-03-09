package com.shiftscheduler.controller

import com.shiftscheduler.dto.CreateEmployeeRequest
import com.shiftscheduler.dto.EmployeeResponse
import com.shiftscheduler.dto.UpdateEmployeeRequest
import com.shiftscheduler.service.EmployeeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
class EmployeeController(private val employeeService: EmployeeService) {

    @GetMapping
    @Operation(summary = "Get all employees")
    fun getAllEmployees(): ResponseEntity<List<EmployeeResponse>> =
        ResponseEntity.ok(employeeService.getAllEmployees())

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    fun getEmployeeById(@PathVariable id: Long): ResponseEntity<EmployeeResponse> =
        ResponseEntity.ok(employeeService.getEmployeeById(id))

    @PostMapping
    @Operation(summary = "Create a new employee")
    fun createEmployee(@Valid @RequestBody request: CreateEmployeeRequest): ResponseEntity<EmployeeResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request))

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    fun updateEmployee(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateEmployeeRequest
    ): ResponseEntity<EmployeeResponse> =
        ResponseEntity.ok(employeeService.updateEmployee(id, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Void> {
        employeeService.deleteEmployee(id)
        return ResponseEntity.noContent().build()
    }
}
