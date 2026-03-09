package com.shiftscheduler.service

import com.shiftscheduler.dto.LaborCostReport
import com.shiftscheduler.repository.TimeEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class ReportService(
    private val timeEntryRepository: TimeEntryRepository,
    private val laborCalculationService: LaborCalculationService
) {

    fun getLaborCostReport(startDate: LocalDate, endDate: LocalDate): LaborCostReport {
        require(!endDate.isBefore(startDate)) { "End date must not be before start date" }

        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)

        val entries = timeEntryRepository.findAllByDateRange(startDateTime, endDateTime)
        return laborCalculationService.buildLaborReport(entries, startDate, endDate)
    }
}
