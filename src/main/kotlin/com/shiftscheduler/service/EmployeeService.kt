package com.shiftscheduler.service

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.dto.CreateEmployeeRequest
import com.shiftscheduler.dto.EmployeeResponse
import com.shiftscheduler.dto.UpdateEmployeeRequest
import com.shiftscheduler.exception.ConflictException
import com.shiftscheduler.exception.ResourceNotFoundException
import com.shiftscheduler.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    @Transactional(readOnly = true)
    fun getAllEmployees(): List<EmployeeResponse> =
        employeeRepository.findAll().map { EmployeeResponse.from(it) }

    @Transactional(readOnly = true)
    fun getEmployeeById(id: Long): EmployeeResponse {
        val employee = findEmployeeOrThrow(id)
        return EmployeeResponse.from(employee)
    }

    fun createEmployee(request: CreateEmployeeRequest): EmployeeResponse {
        if (employeeRepository.existsByEmail(request.email)) {
            throw ConflictException("Employee with email '${request.email}' already exists")
        }
        val employee = Employee(
            name = request.name,
            email = request.email,
            role = request.role,
            hourlyRate = request.hourlyRate
        )
        return EmployeeResponse.from(employeeRepository.save(employee))
    }

    fun updateEmployee(id: Long, request: UpdateEmployeeRequest): EmployeeResponse {
        val employee = findEmployeeOrThrow(id)
        if (request.email != employee.email && employeeRepository.existsByEmail(request.email)) {
            throw ConflictException("Email '${request.email}' is already in use")
        }
        employee.name = request.name
        employee.email = request.email
        employee.role = request.role
        employee.hourlyRate = request.hourlyRate
        return EmployeeResponse.from(employeeRepository.save(employee))
    }

    fun deleteEmployee(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw ResourceNotFoundException("Employee not found with id: $id")
        }
        employeeRepository.deleteById(id)
    }

    fun findEmployeeOrThrow(id: Long): Employee =
        employeeRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Employee not found with id: $id")
        }
}
