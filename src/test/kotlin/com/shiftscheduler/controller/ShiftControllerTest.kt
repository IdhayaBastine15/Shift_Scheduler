package com.shiftscheduler.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.shiftscheduler.domain.Role
import com.shiftscheduler.domain.ShiftStatus
import com.shiftscheduler.dto.CreateShiftRequest
import com.shiftscheduler.dto.ShiftResponse
import com.shiftscheduler.exception.BusinessRuleException
import com.shiftscheduler.service.ShiftService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(ShiftController::class)
class ShiftControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var shiftService: ShiftService

    private val sampleShift = ShiftResponse(
        id = 1L,
        employeeId = 1L,
        employeeName = "Alice Smith",
        startTime = LocalDateTime.of(2026, 3, 9, 9, 0),
        endTime = LocalDateTime.of(2026, 3, 9, 17, 0),
        roleAtShift = Role.SERVER,
        status = ShiftStatus.SCHEDULED,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `GET all shifts returns 200`() {
        every { shiftService.getAllShifts(null) } returns listOf(sampleShift)

        mockMvc.get("/api/shifts") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].id") { value(1) }
            jsonPath("$[0].status") { value("SCHEDULED") }
        }
    }

    @Test
    fun `POST create shift returns 201`() {
        val request = CreateShiftRequest(
            employeeId = 1L,
            startTime = LocalDateTime.of(2026, 3, 9, 9, 0),
            endTime = LocalDateTime.of(2026, 3, 9, 17, 0),
            roleAtShift = Role.SERVER
        )

        every { shiftService.createShift(any()) } returns sampleShift

        mockMvc.post("/api/shifts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.employeeId") { value(1) }
        }
    }

    @Test
    fun `POST create shift returns 400 when business rule violated`() {
        val request = CreateShiftRequest(
            employeeId = 1L,
            startTime = LocalDateTime.of(2026, 3, 9, 9, 0),
            endTime = LocalDateTime.of(2026, 3, 9, 17, 0),
            roleAtShift = Role.SERVER
        )

        every { shiftService.createShift(any()) } throws BusinessRuleException("Shift overlaps with existing shift")

        mockMvc.post("/api/shifts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
