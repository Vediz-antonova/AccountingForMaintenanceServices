package com.vedizl.accountingformaintenanceservices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vedizl.accountingformaintenanceservices.ui.cars.CarsViewModel
import com.vedizl.accountingformaintenanceservices.ui.navigation.NavGraph
import com.vedizl.accountingformaintenanceservices.ui.theme.AccountingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccountingTheme {
                val navController = rememberNavController()
                val viewModel: CarsViewModel = viewModel()
                NavGraph(
                    navController = navController,
                    carsViewModel = viewModel
                )
            }
        }
    }
}
