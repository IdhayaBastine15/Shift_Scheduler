package com.shiftscheduler.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.shiftscheduler.domain.Role
import com.shiftscheduler.dto.CreateEmployeeRequest
import com.shiftscheduler.dto.EmployeeResponse
import com.shiftscheduler.exception.ResourceNotFoundException
import com.shiftscheduler.service.EmployeeService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.time.LocalDateTime

@WebMvcTest(EmployeeController::class)
class EmployeeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var employeeService: EmployeeService

    private val sampleEmployee = EmployeeResponse(
        id = 1L,
        name = "Alice Smith",
        email = "alice@example.com",
        role = Role.SERVER,
        hourlyRate = BigDecimal("15.00"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `GET all employees returns 200 with list`() {
        every { employeeService.getAllEmployees() } returns listOf(sampleEmployee)

        mockMvc.get("/api/employees") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].id") { value(1) }
            jsonPath("$[0].name") { value("Alice Smith") }
            jsonPath("$[0].role") { value("SERVER") }
        }
    }

    @Test
    fun `GET employee by ID returns 200`() {
        every { employeeService.getEmployeeById(1L) } returns sampleEmployee

        mockMvc.get("/api/employees/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.email") { value("alice@example.com") }
        }
    }

    @Test
    fun `GET employee by ID returns 404 when not found`() {
        every { employeeService.getEmployeeById(99L) } throws ResourceNotFoundException("Employee not found with id: 99")

        mockMvc.get("/api/employees/99") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `POST create employee returns 201 with created employee`() {
        val request = CreateEmployeeRequest(
            name = "Alice Smith",
            email = "alice@example.com",
            role = Role.SERVER,
            hourlyRate = BigDecimal("15.00")
        )

        every { employeeService.createEmployee(any()) } returns sampleEmployee

        mockMvc.post("/api/employees") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.name") { value("Alice Smith") }
            jsonPath("$.role") { value("SERVER") }
        }
    }

    @Test
    fun `POST create employee returns 400 on validation failure`() {
        val invalidRequest = mapOf("name" to "", "email" to "not-an-email", "role" to "SERVER", "hourlyRate" to -1)

        mockMvc.post("/api/employees") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
