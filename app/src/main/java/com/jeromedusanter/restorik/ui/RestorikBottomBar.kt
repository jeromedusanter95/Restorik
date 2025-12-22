package com.jeromedusanter.restorik.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jeromedusanter.restorik.feature.meal.R as MealR
import com.jeromedusanter.restorik.feature.profile.R as ProfileR
import com.jeromedusanter.restorik.feature.profile.navigation.ProfileDestinations

@Composable
fun RestorikBottomBar(
    currentRoute: String?,
    onMealClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute?.startsWith(prefix = "meal") == true,
            onClick = onMealClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = stringResource(id = MealR.string.feature_meal_list_title)
                )
            },
            label = {
                Text(text = stringResource(id = MealR.string.feature_meal_list_title))
            }
        )
        NavigationBarItem(
            selected = currentRoute == ProfileDestinations.Profile.route,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(id = ProfileR.string.feature_profile_title)
                )
            },
            label = {
                Text(text = stringResource(id = ProfileR.string.feature_profile_title))
            }
        )
    }
}
