package com.jeromedusanter.restorik.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.YearMonth
import java.util.Locale

@Composable
fun ProfileScreen(
    onNavigateToMonthSelector: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        onMonthChipClick = onNavigateToMonthSelector,
        onMonthChange = { yearMonth -> viewModel.selectMonth(yearMonth = yearMonth) },
        modifier = modifier
    )
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onMonthChipClick: () -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        // Month selector chip
        AssistChip(
            onClick = onMonthChipClick,
            label = {
                Text(text = formatMonthYear(yearMonth = uiState.selectedMonth))
            }
        )

        // Monthly balance card with navigation arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month arrow
            IconButton(
                onClick = { onMonthChange(uiState.selectedMonth.minusMonths(1)) },
                enabled = uiState.selectedMonth > YearMonth.of(2025, 1)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous month"
                )
            }

            // Monthly balance card
            Card(
                modifier = Modifier.weight(weight = 1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_profile_monthly_balance),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.monthlySpending > 0.0) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", uiState.monthlySpending),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.feature_profile_no_data),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Next month arrow
            IconButton(
                onClick = { onMonthChange(uiState.selectedMonth.plusMonths(1)) },
                enabled = uiState.selectedMonth < YearMonth.now()
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next month"
                )
            }
        }
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
