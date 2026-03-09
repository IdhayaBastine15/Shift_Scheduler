package com.shiftscheduler.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "time_entries")
class TimeEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    var employee: Employee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    var shift: Shift? = null,

    @Column(nullable = false)
    var clockIn: LocalDateTime,

    @Column
    var clockOut: LocalDateTime? = null,

    @Column(precision = 6, scale = 2)
    var totalHours: BigDecimal? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
