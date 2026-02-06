package com.jeromedusanter.restorik.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme

@Composable
fun SearchResults(
    searchResultList: List<SearchResultUiModel>,
    onMealClick: (Int) -> Unit,
    onRestaurantClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        items(
            items = searchResultList,
            key = { result ->
                when (result) {
                    is SearchResultUiModel.MealItem -> "meal_${result.id}"
                    is SearchResultUiModel.RestaurantItem -> "restaurant_${result.id}"
                }
            }
        ) { result ->
            when (result) {
                is SearchResultUiModel.MealItem -> MealResultItem(
                    mealItem = result,
                    onClick = { onMealClick(result.id) }
                )

                is SearchResultUiModel.RestaurantItem -> RestaurantResultItem(
                    restaurantItem = result,
                    onClick = { onRestaurantClick(result.id) }
                )
            }
        }
    }
}

@Composable
private fun MealResultItem(
    mealItem: SearchResultUiModel.MealItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mealItem.photoUri != null) {
            AsyncImage(
                model = mealItem.photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(size = 64.dp)
                    .clip(shape = RoundedCornerShape(size = 8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(size = 64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            Text(
                text = mealItem.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = mealItem.restaurantName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = mealItem.cityName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(times = mealItem.rating.toInt()) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(size = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantResultItem(
    restaurantItem: SearchResultUiModel.RestaurantItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            modifier = Modifier.size(size = 48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            Text(
                text = restaurantItem.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = restaurantItem.cityName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchResultsPreview() {
    RestorikTheme {
        SearchResults(
            searchResultList = listOf(
                SearchResultUiModel.MealItem(
                    id = 1,
                    name = "Meal name",
                    photoUri = null,
                    restaurantName = "Restaurant name",
                    cityName = "City name",
                    rating = 4.0f
                ),
                SearchResultUiModel.RestaurantItem(
                    id = 1,
                    name = "Restaurant name",
                    cityName = "City name"
                )
            ),
            onMealClick = {},
            onRestaurantClick = {}
        )
    }
}
