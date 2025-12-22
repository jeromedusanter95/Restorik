package com.jeromedusanter.restorik

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.jeromedusanter.restorik.core.designsystem.theme.RestorikTheme
import com.jeromedusanter.restorik.debug.MockDataPopulator
import com.jeromedusanter.restorik.ui.RestorikApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mockDataPopulator: MockDataPopulator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Insert mock data on first launch
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasInsertedMockData = prefs.getBoolean("mock_data_inserted", false)

        if (!hasInsertedMockData) {
            lifecycleScope.launch {
                mockDataPopulator.populateWithMockData()
                prefs.edit().putBoolean("mock_data_inserted", true).apply()
            }
        }

        setContent {
            RestorikTheme {
                RestorikApp()
            }
        }
    }
}