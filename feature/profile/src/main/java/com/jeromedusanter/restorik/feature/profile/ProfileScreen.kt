package com.jeromedusanter.restorik.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
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
            .verticalScroll(state = rememberScrollState())
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
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.monthlySpending > 0.0) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f €", uiState.monthlySpending),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        // Comparison with previous month
                        if (uiState.previousMonthSpending > 0.0) {
                            val difference = uiState.monthlySpending - uiState.previousMonthSpending
                            val percentageChange = (difference / uiState.previousMonthSpending) * 100
                            val isIncrease = difference > 0

                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "%s%.1f%% %s",
                                    if (isIncrease) "+" else "",
                                    percentageChange,
                                    stringResource(id = R.string.feature_profile_vs_previous_month)
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isIncrease) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.tertiary
                                },
                                textAlign = TextAlign.Center
                            )
                        }
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

        // Statistics grid (2x3)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            // First row - Meal stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                StatCard(
                    title = stringResource(id = R.string.feature_profile_number_of_meals),
                    value = uiState.numberOfMeals.toString(),
                    modifier = Modifier.weight(weight = 1f)
                )
                StatCard(
                    title = stringResource(id = R.string.feature_profile_average_rating),
                    value = if (uiState.averageRating > 0.0) {
                        String.format(Locale.getDefault(), "%.1f", uiState.averageRating)
                    } else {
                        "-"
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }

            // Second row - Meal spending & Restaurant count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                StatCard(
                    title = stringResource(id = R.string.feature_profile_average_meal_spending),
                    value = if (uiState.averageMealSpending > 0.0) {
                        String.format(Locale.getDefault(), "%.2f €", uiState.averageMealSpending)
                    } else {
                        "-"
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
                StatCard(
                    title = stringResource(id = R.string.feature_profile_number_of_restaurants),
                    value = uiState.numberOfRestaurants.toString(),
                    modifier = Modifier.weight(weight = 1f)
                )
            }

            // Third row - Restaurant stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                StatCard(
                    title = stringResource(id = R.string.feature_profile_new_restaurants_tried),
                    value = uiState.newRestaurantsTried.toString(),
                    modifier = Modifier.weight(weight = 1f)
                )
                TopRestaurantsCard(
                    topRestaurantList = uiState.topRestaurantsBySpending,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(ratio = 1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopRestaurantsCard(
    topRestaurantList: List<RestaurantSpending>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(ratio = 1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.feature_profile_top_restaurants),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            if (topRestaurantList.isEmpty()) {
                Text(
                    text = "-",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {
                    topRestaurantList.forEachIndexed { index, restaurant ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${index + 1}. ${restaurant.restaurantName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f €", restaurant.totalSpending),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
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
