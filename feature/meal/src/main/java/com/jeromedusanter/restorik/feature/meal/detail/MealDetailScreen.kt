package com.jeromedusanter.restorik.feature.meal.detail

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.core.designsystem.theme.gold
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.ui.PhotoViewDialog
import com.jeromedusanter.restorik.feature.meal.R
import java.util.Locale

@Composable
fun MealDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MealDetailUiState,
    onDeleteClick: () -> Unit = {}
) {
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            // Photo Carousel
            if (uiState.photoUriList.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { uiState.photoUriList.size })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = uiState.photoUriList[page],
                            contentDescription = "Meal photo ${page + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { selectedPhotoUri = uiState.photoUriList[page] },
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Page Indicators
                    if (uiState.photoUriList.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(uiState.photoUriList.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (pagerState.currentPage == index)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }
            } else {
                // Placeholder when no photos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp)
            ) {
                // Meal Name
                Text(
                    text = uiState.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Restaurant Info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = uiState.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // City Info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.cityName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                // Rating and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = gold.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = gold
                            )
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "%.1f/5",
                                    uiState.ratingOnFive
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (uiState.isSomeoneElsePaying) {
                        Text(
                            stringResource(R.string.feature_meal_details_someone_else_is_paying_label),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.priceAsString,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Icon(
                                    imageVector = Icons.Default.Euro,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Dishes Section
                if (uiState.dishList.isNotEmpty()) {
                    HorizontalDivider()

                    Column(
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_meal_dishes_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        uiState.dishList.forEach { dish ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = dish.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(weight = 1f)
                                        )
                                        if (!uiState.isSomeoneElsePaying) {
                                            Text(
                                                text = String.format(
                                                    Locale.getDefault(),
                                                    "%.2f€",
                                                    dish.price
                                                ),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    if (dish.description.isNotBlank()) {
                                        Text(
                                            text = dish.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(5) { index ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (index < dish.rating.toInt()) gold else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.3f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Delete Button
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = stringResource(R.string.feature_meal_delete_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Photo View Dialog
        selectedPhotoUri?.let { photoUri ->
            PhotoViewDialog(
                photoUri = photoUri,
                onDismiss = { selectedPhotoUri = null }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            DeleteConfirmDialog(
                onConfirm = {
                    showDeleteDialog = false
                    onDeleteClick()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MealDetailScreenPreview() {
    RestorikTheme {
        MealDetailScreen(
            uiState = MealDetailUiState(
                restaurantName = "Sapore",
                cityName = "Paris",
                name = "Italian Dinner",
                dishList = listOf(
                    Dish(
                        id = 1,
                        name = "Pizza Margherita",
                        rating = 5f,
                        description = "Perfect crispy crust with fresh basil",
                        price = 12.50,
                        dishType = DishType.MAIN_COURSE
                    ),
                    Dish(
                        id = 2,
                        name = "Tiramisu",
                        rating = 4.5f,
                        description = "Classic Italian dessert",
                        price = 6.50,
                        dishType = DishType.DESSERT
                    )
                ),
                priceAsString = "19.00",
                ratingOnFive = 4.7f,
                photoUriList = emptyList()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealDetailScreenSomeoneElsePayingPreview() {
    RestorikTheme {
        MealDetailScreen(
            uiState = MealDetailUiState(
                restaurantName = "Sapore",
                cityName = "Paris",
                name = "Italian Dinner",
                dishList = listOf(
                    Dish(
                        id = 1,
                        name = "Pizza Margherita",
                        rating = 5f,
                        description = "Perfect crispy crust with fresh basil",
                        price = 12.50,
                        dishType = DishType.MAIN_COURSE
                    ),
                    Dish(
                        id = 2,
                        name = "Tiramisu",
                        rating = 4.5f,
                        description = "Classic Italian dessert",
                        price = 6.50,
                        dishType = DishType.DESSERT
                    )
                ),
                priceAsString = "19.00",
                ratingOnFive = 4.7f,
                photoUriList = emptyList(),
                isSomeoneElsePaying = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealDetailScreenEmptyPreview() {
    RestorikTheme {
        MealDetailScreen(
            uiState = MealDetailUiState.EMPTY
        )
    }
}