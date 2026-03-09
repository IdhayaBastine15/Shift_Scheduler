package com.shiftscheduler.domain

enum class Role {
    SERVER,
    COOK,
    MANAGER,
    BARTENDER,
    HOST
}

enum class ShiftStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}
