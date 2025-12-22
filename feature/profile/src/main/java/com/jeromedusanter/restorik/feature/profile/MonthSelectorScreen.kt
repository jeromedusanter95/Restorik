package com.jeromedusanter.restorik.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthSelectorScreen(
    currentMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedYear by remember { mutableStateOf(currentMonth.year) }
    val monthList = generateMonthListForYear(year = selectedYear)
    val currentYear = Year.now().value
    val minYear = 2025

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Year selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { selectedYear -= 1 },
                enabled = selectedYear > minYear
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous year"
                )
            }

            Text(
                text = selectedYear.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            IconButton(
                onClick = { selectedYear += 1 },
                enabled = selectedYear < currentYear
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next year"
                )
            }
        }

        HorizontalDivider()

        // Month list
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = monthList, key = { it.yearMonth.toString() }) { monthItem ->
                MonthItem(
                    monthItem = monthItem,
                    isSelected = monthItem.yearMonth == currentMonth,
                    onClick = { onMonthSelected(monthItem.yearMonth) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun MonthItem(
    monthItem: MonthItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = monthItem.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun generateMonthListForYear(year: Int): List<MonthItem> {
    val currentYearMonth = YearMonth.now()
    val isCurrentYear = year == currentYearMonth.year

    return (1..12).map { month ->
        val yearMonth = YearMonth.of(year, month)
        MonthItem(
            yearMonth = yearMonth,
            displayName = formatMonthYear(yearMonth = yearMonth)
        )
    }.filter { monthItem ->
        // If it's the current year, only show months up to current month
        !isCurrentYear || monthItem.yearMonth <= currentYearMonth
    }
}

@Composable
private fun formatMonthYear(yearMonth: YearMonth): String {
    val monthResId = when (yearMonth.monthValue) {
        1 -> R.string.feature_profile_month_january
        2 -> R.string.feature_profile_month_february
        3 -> R.string.feature_profile_month_march
        4 -> R.string.feature_profile_month_april
        5 -> R.string.feature_profile_month_may
        6 -> R.string.feature_profile_month_june
        7 -> R.string.feature_profile_month_july
        8 -> R.string.feature_profile_month_august
        9 -> R.string.feature_profile_month_september
        10 -> R.string.feature_profile_month_october
        11 -> R.string.feature_profile_month_november
        12 -> R.string.feature_profile_month_december
        else -> R.string.feature_profile_month_january
    }
    val monthName = stringResource(id = monthResId)
    return "$monthName ${yearMonth.year}"
}
