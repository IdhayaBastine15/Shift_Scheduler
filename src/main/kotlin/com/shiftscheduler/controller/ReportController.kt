package com.shiftscheduler.controller

import com.shiftscheduler.dto.LaborCostReport
import com.shiftscheduler.service.ReportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Labor cost and reporting endpoints")
class ReportController(private val reportService: ReportService) {

    @GetMapping("/labor-costs")
    @Operation(summary = "Get labor cost report for a date range")
    fun getLaborCostReport(
        @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,

        @Parameter(description = "End date inclusive (yyyy-MM-dd)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<LaborCostReport> =
        ResponseEntity.ok(reportService.getLaborCostReport(startDate, endDate))
}
