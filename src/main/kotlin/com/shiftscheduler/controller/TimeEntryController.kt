package com.shiftscheduler.controller

import com.shiftscheduler.dto.ClockInRequest
import com.shiftscheduler.dto.ClockOutRequest
import com.shiftscheduler.dto.TimeEntryResponse
import com.shiftscheduler.service.TimeEntryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/timekeeping")
@Tag(name = "Timekeeping", description = "Clock-in/out and time tracking endpoints")
class TimeEntryController(private val timeEntryService: TimeEntryService) {

    @PostMapping("/clock-in")
    @Operation(summary = "Clock in an employee")
    fun clockIn(@Valid @RequestBody request: ClockInRequest): ResponseEntity<TimeEntryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(timeEntryService.clockIn(request))

    @PostMapping("/clock-out")
    @Operation(summary = "Clock out an employee")
    fun clockOut(@Valid @RequestBody request: ClockOutRequest): ResponseEntity<TimeEntryResponse> =
        ResponseEntity.ok(timeEntryService.clockOut(request))

    @GetMapping
    @Operation(summary = "Get all time entries")
    fun getAllEntries(): ResponseEntity<List<TimeEntryResponse>> =
        ResponseEntity.ok(timeEntryService.getAllEntries())

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get time entries for a specific employee")
    fun getEntriesForEmployee(@PathVariable employeeId: Long): ResponseEntity<List<TimeEntryResponse>> =
        ResponseEntity.ok(timeEntryService.getEntriesForEmployee(employeeId))
}
