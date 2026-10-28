package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.ui.AuthScreen
import com.example.ui.MainLayout
import com.example.ui.MarketViewModel
import com.example.ui.MarketViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Mirrors store data to Firestore for the live dashboard; does nothing until Firebase keys are set.
        com.example.data.FirestoreSync.start(applicationContext, AppDatabase.getInstance(applicationContext))
        setContent {
            val db = AppDatabase.getInstance(applicationContext)
            val viewModelFactory = MarketViewModelFactory(
                db.userDao(),
                db.productDao(),
                db.savedProductDao(),
                db.orderDao(),
                db.chatReportDao(),
                db.cartItemDao(),
                db.bankCardDao(),
                db.searchQueryDao(),
                db.familyShoppingListDao(),
                db.storedOrganicItemDao()
            )
            val viewModel: MarketViewModel = viewModel(factory = viewModelFactory)
            val isDark by viewModel.appDarkMode.collectAsState()
            
            MyApplicationTheme(darkTheme = isDark) {
                val currentUser by viewModel.currentUser.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Since AuthScreen and MainLayout fully handle internal paddings and window insets,
                    // we do not need to apply innerPadding explicitly here to prevent double padding issues,
                    // but we can pass it if necessary.
                    if (currentUser == null) {
                        AuthScreen(viewModel = viewModel)
                    } else {
                        MainLayout(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
