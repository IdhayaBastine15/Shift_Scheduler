package com.shiftscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShiftSchedulerApplication

fun main(args: Array<String>) {
    runApplication<ShiftSchedulerApplication>(*args)
}
