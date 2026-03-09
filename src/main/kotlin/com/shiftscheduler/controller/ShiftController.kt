package com.shiftscheduler.controller

import com.shiftscheduler.dto.CreateShiftRequest
import com.shiftscheduler.dto.ShiftResponse
import com.shiftscheduler.dto.UpdateShiftRequest
import com.shiftscheduler.dto.UpdateShiftStatusRequest
import com.shiftscheduler.service.ShiftService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/shifts")
@Tag(name = "Shifts", description = "Shift scheduling endpoints")
class ShiftController(private val shiftService: ShiftService) {

    @GetMapping
    @Operation(summary = "Get all shifts, optionally filtered by employee")
    fun getAllShifts(@RequestParam(required = false) employeeId: Long?): ResponseEntity<List<ShiftResponse>> =
        ResponseEntity.ok(shiftService.getAllShifts(employeeId))

    @GetMapping("/{id}")
    @Operation(summary = "Get shift by ID")
    fun getShiftById(@PathVariable id: Long): ResponseEntity<ShiftResponse> =
        ResponseEntity.ok(shiftService.getShiftById(id))

    @PostMapping
    @Operation(summary = "Create a new shift")
    fun createShift(@Valid @RequestBody request: CreateShiftRequest): ResponseEntity<ShiftResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(request))

    @PutMapping("/{id}")
    @Operation(summary = "Update a shift's schedule")
    fun updateShift(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateShiftRequest
    ): ResponseEntity<ShiftResponse> =
        ResponseEntity.ok(shiftService.updateShift(id, request))

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a shift's status (SCHEDULED, COMPLETED, CANCELLED)")
    fun updateShiftStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateShiftStatusRequest
    ): ResponseEntity<ShiftResponse> =
        ResponseEntity.ok(shiftService.updateShiftStatus(id, request))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a shift")
    fun deleteShift(@PathVariable id: Long): ResponseEntity<Void> {
        shiftService.deleteShift(id)
        return ResponseEntity.noContent().build()
    }
}
