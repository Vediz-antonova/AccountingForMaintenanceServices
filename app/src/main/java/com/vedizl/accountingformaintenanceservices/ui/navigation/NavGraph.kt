package com.vedizl.accountingformaintenanceservices.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vedizl.accountingformaintenanceservices.ui.cars.AddCarScreen
import com.vedizl.accountingformaintenanceservices.ui.cars.CarsScreen
import com.vedizl.accountingformaintenanceservices.ui.cars.CarsViewModel
import com.vedizl.accountingformaintenanceservices.ui.maintenance.AddMaintenanceScreen
import com.vedizl.accountingformaintenanceservices.ui.maintenance.MaintenanceDetailScreen
import com.vedizl.accountingformaintenanceservices.ui.maintenance.MaintenanceListScreen
import com.vedizl.accountingformaintenanceservices.ui.maintenance.MaintenanceViewModel
import com.vedizl.accountingformaintenanceservices.ui.reminders.RemindersScreen
import java.time.LocalDate

object Routes {
    const val CARS = "cars"
    const val ADD_CAR = "add_car"
    const val HISTORY = "history/{carId}/{carName}"
    const val ADD_MAINTENANCE = "add_maintenance/{carId}"
    const val MAINTENANCE_DETAIL = "maintenance_detail/{recordId}/{carId}"
    const val REMINDERS = "reminders/{carId}"

    fun history(carId: String, carName: String) = "history/$carId/$carName"
    fun addMaintenance(carId: String) = "add_maintenance/$carId"
    fun maintenanceDetail(recordId: String, carId: String) = "maintenance_detail/$recordId/$carId"
    fun reminders(carId: String) = "reminders/$carId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    carsViewModel: CarsViewModel,
) {
    val cars by carsViewModel.cars.collectAsState()
    val maintenanceViewModel: MaintenanceViewModel = viewModel()

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
                    carsViewModel.selectCar(carId)
                    val car = cars.find { it.id == carId }
                    car?.let {
                        navController.navigate(Routes.history(carId, it.displayName()))
                    }
                },
                onDeleteCar = { carId ->
                    carsViewModel.deleteCar(carId)
                }
            )
        }

        composable(Routes.ADD_CAR) {
            AddCarScreen(
                onBack = { navController.popBackStack() },
                onSave = { brand, model, year, licensePlate, mileage ->
                    carsViewModel.addCar(brand, model, year, licensePlate, mileage)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.HISTORY,
            arguments = listOf(
                navArgument("carId") { type = NavType.StringType },
                navArgument("carName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            val carName = backStackEntry.arguments?.getString("carName") ?: return@composable

            LaunchedEffect(carId) {
                maintenanceViewModel.loadForCar(carId)
            }

            val records by maintenanceViewModel.records.collectAsState()
            val filters by maintenanceViewModel.filters.collectAsState()

            MaintenanceListScreen(
                carName = carName,
                records = records,
                filters = filters,
                onBack = { navController.popBackStack() },
                onAdd = { navController.navigate(Routes.addMaintenance(carId)) },
                onRecordClick = { recordId ->
                    navController.navigate(Routes.maintenanceDetail(recordId, carId))
                },
                onDeleteRecord = { recordId ->
                    maintenanceViewModel.deleteRecord(recordId)
                },
                onFiltersChange = { maintenanceViewModel.updateFilters(it) },
                onRemindersClick = {
                    navController.navigate(Routes.reminders(carId))
                }
            )
        }

        composable(
            route = Routes.ADD_MAINTENANCE,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable

            AddMaintenanceScreen(
                onBack = { navController.popBackStack() },
                onSave = { category, type, dateEpochDay, mileage, partNumber, partCost, notes ->
                    val record = com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord(
                        carId = carId,
                        category = category,
                        type = type,
                        dateEpochDay = dateEpochDay,
                        mileage = mileage,
                        partNumber = partNumber,
                        partCost = partCost,
                        notes = notes,
                    )
                    maintenanceViewModel.addRecord(record)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.MAINTENANCE_DETAIL,
            arguments = listOf(
                navArgument("recordId") { type = NavType.StringType },
                navArgument("carId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId") ?: return@composable
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable

            LaunchedEffect(carId) {
                maintenanceViewModel.loadForCar(carId)
            }

            val record = maintenanceViewModel.getRecordById(recordId)
            if (record != null) {
                MaintenanceDetailScreen(
                    record = record,
                    onBack = { navController.popBackStack() },
                    onDelete = { id ->
                        maintenanceViewModel.deleteRecord(id)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Routes.REMINDERS,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) {
            RemindersScreen()
        }
    }
}
