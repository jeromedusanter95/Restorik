package com.jeromedusanter.restorik.feature.meal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.ui.RestorikOutlineTextField
import com.jeromedusanter.restorik.core.ui.RestorikRatingBar
import com.jeromedusanter.restorik.feature.meal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishEditorDialog(
    state: DishEditorState,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onDishTypeChange: (DishType) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) {
                    stringResource(R.string.feature_meal_edit_dish_title)
                } else {
                    stringResource(R.string.feature_meal_add_dish_title)
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp)
            ) {
                RestorikOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.name,
                    onValueChange = onNameChange,
                    label = stringResource(R.string.feature_meal_dish_name_label),
                    isRequired = true,
                    isError = state.nameError != null,
                    supportingText = state.nameError
                )

                RestorikOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = stringResource(R.string.feature_meal_dish_description_label),
                    singleLine = false
                )

                ExposedDropdownMenuBox(
                    expanded = state.isExpanded,
                    onExpandedChange = onExpandedChange,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RestorikOutlineTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        value = stringResource(getDishTypeString(dishType = state.dishType)),
                        onValueChange = {},
                        label = stringResource(R.string.feature_meal_dish_type_label),
                        enabled = false,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.isExpanded)
                        },
                        isRequired = true
                    )
                    ExposedDropdownMenu(
                        containerColor = MaterialTheme.colorScheme.background,
                        expanded = state.isExpanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        DishType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = stringResource(getDishTypeString(dishType = type))) },
                                onClick = {
                                    onDishTypeChange(type)
                                    onExpandedChange(false)
                                }
                            )
                        }
                    }
                }

                RestorikOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.priceString,
                    onValueChange = onPriceChange,
                    label = stringResource(R.string.feature_meal_price_label),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Euro,
                            contentDescription = stringResource(R.string.feature_meal_euro_content_description)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isRequired = true,
                    isError = state.priceError != null,
                    supportingText = state.priceError
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.feature_meal_rating_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.ratingError != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    RestorikRatingBar(
                        modifier = Modifier.padding(top = 8.dp),
                        value = state.rating,
                        onValueChanged = onRatingChange
                    )
                    state.ratingError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(text = stringResource(R.string.feature_meal_save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.feature_meal_delete_cancel_button))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun getDishTypeString(dishType: DishType): Int {
    return when (dishType) {
        DishType.APERITIF -> R.string.feature_meal_dish_type_aperitif
        DishType.STARTER -> R.string.feature_meal_dish_type_starter
        DishType.MAIN_COURSE -> R.string.feature_meal_dish_type_main_course
        DishType.SIDE_DISH -> R.string.feature_meal_dish_type_side_dish
        DishType.CHEESE -> R.string.feature_meal_dish_type_cheese
        DishType.DESSERT -> R.string.feature_meal_dish_type_dessert
        DishType.DRINK -> R.string.feature_meal_dish_type_drink
    }
}
