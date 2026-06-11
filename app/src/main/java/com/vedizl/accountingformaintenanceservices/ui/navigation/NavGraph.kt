package com.vedizl.accountingformaintenanceservices.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vedizl.accountingformaintenanceservices.ui.cars.AddCarScreen
import com.vedizl.accountingformaintenanceservices.ui.cars.CarsScreen
import com.vedizl.accountingformaintenanceservices.ui.cars.CarsViewModel
import com.vedizl.accountingformaintenanceservices.ui.history.HistoryScreen

object Routes {
    const val CARS = "cars"
    const val ADD_CAR = "add_car"
    const val HISTORY = "history/{carId}"

    fun history(carId: String) = "history/$carId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CarsViewModel
) {
    val cars by viewModel.cars.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.CARS
    ) {
        composable(Routes.CARS) {
            CarsScreen(
                cars = cars,
                onAddCar = {
                    navController.navigate(Routes.ADD_CAR)
                },
                onSelectCar = { carId ->
                    viewModel.selectCar(carId)
                    navController.navigate(Routes.history(carId))
                },
                onDeleteCar = { carId ->
                    viewModel.deleteCar(carId)
                }
            )
        }

        composable(Routes.ADD_CAR) {
            AddCarScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSave = { brand, model, year, licensePlate, mileage ->
                    viewModel.addCar(brand, model, year, licensePlate, mileage)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.HISTORY,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            val car = cars.find { it.id == carId }
            if (car != null) {
                HistoryScreen(carName = car.displayName())
            }
        }
    }
}
