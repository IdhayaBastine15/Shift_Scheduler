package com.shiftscheduler.repository

import com.shiftscheduler.domain.Employee
import com.shiftscheduler.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun findByEmail(email: String): Employee?
    fun existsByEmail(email: String): Boolean
    fun findAllByRole(role: Role): List<Employee>
}
