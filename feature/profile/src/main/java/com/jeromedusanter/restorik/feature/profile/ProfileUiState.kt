package com.jeromedusanter.restorik.feature.profile

import java.time.YearMonth

data class ProfileUiState(
    val selectedMonth: YearMonth,
    val monthlySpending: Double,
    val isLoading: Boolean
) {
    companion object {
        val EMPTY = ProfileUiState(
            selectedMonth = YearMonth.now(),
            monthlySpending = 0.0,
            isLoading = true
        )
    }
}
