package com.vedizl.accountingformaintenanceservices.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.vedizl.accountingformaintenanceservices.ui.parts.PartsScreen
import com.vedizl.accountingformaintenanceservices.ui.parts.PartsViewModel
import com.vedizl.accountingformaintenanceservices.ui.reminders.RemindersScreen

object Routes {
    const val CARS = "cars"
    const val ADD_CAR = "add_car"
    const val HISTORY = "history/{carId}/{carName}"
    const val ADD_MAINTENANCE = "add_maintenance/{carId}"
    const val MAINTENANCE_DETAIL = "maintenance_detail/{recordId}/{carId}"
    const val REMINDERS = "reminders/{carId}"
    const val PARTS = "parts/{carId}"

    fun history(carId: String, carName: String) = "history/$carId/$carName"
    fun addMaintenance(carId: String) = "add_maintenance/$carId"
    fun maintenanceDetail(recordId: String, carId: String) = "maintenance_detail/$recordId/$carId"
    fun reminders(carId: String) = "reminders/$carId"
    fun parts(carId: String) = "parts/$carId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    carsViewModel: CarsViewModel,
) {
    val cars by carsViewModel.cars.collectAsState()
    val carMakes by carsViewModel.carMakes.collectAsState()
    val carModels by carsViewModel.carModels.collectAsState()
    val carError by carsViewModel.error.collectAsState()
    val maintenanceViewModel: MaintenanceViewModel = viewModel()
    val maintenanceCategories by maintenanceViewModel.categories.collectAsState()
    val maintenanceWorkTypes by maintenanceViewModel.workTypes.collectAsState()
    val maintenanceError by maintenanceViewModel.error.collectAsState()
    val partsViewModel: PartsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.CARS
    ) {
        composable(Routes.CARS) {
            CarsScreen(
                cars = cars,
                error = carError,
                onAddCar = {
                    navController.navigate(Routes.ADD_CAR)
                },
                onSelectCar = { carId ->
                    val car = cars.find { it.id == carId }
                    car?.let {
                        navController.navigate(Routes.history(carId, it.displayName()))
                    }
                },
                onDeleteCar = { carId ->
                    carsViewModel.deleteCar(carId)
                },
                onUpdateLicensePlate = { carId, licensePlate ->
                    carsViewModel.updateCarLicensePlate(carId, licensePlate)
                },
                onErrorConsumed = { carsViewModel.clearError() }
            )
        }

        composable(Routes.ADD_CAR) {
            AddCarScreen(
                makes = carMakes,
                models = carModels,
                error = carError,
                onErrorConsumed = { carsViewModel.clearError() },
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
                carId = carId,
                carName = carName,
                records = records,
                filters = filters,
                categories = maintenanceCategories,
                workTypes = maintenanceWorkTypes,
                error = maintenanceError,
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
                },
                onUpdateMileage = { id, mileage ->
                    carsViewModel.updateCarMileage(id, mileage)
                },
                onPartsClick = {
                    navController.navigate(Routes.parts(carId))
                },
                onErrorConsumed = { maintenanceViewModel.clearError() }
            )
        }

        composable(
            route = Routes.ADD_MAINTENANCE,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable

            AddMaintenanceScreen(
                categories = maintenanceCategories,
                workTypes = maintenanceWorkTypes,
                error = maintenanceError,
                onErrorConsumed = { maintenanceViewModel.clearError() },
                onBack = { navController.popBackStack() },
                onSave = { category, type, dateEpochDay, mileage, partNumber, partManufacturer, partCost, notes ->
                    val record = com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord(
                        carId = carId,
                        category = category,
                        type = type,
                        dateEpochDay = dateEpochDay,
                        mileage = mileage,
                        partNumber = partNumber,
                        partManufacturer = partManufacturer,
                        partCost = partCost,
                        notes = notes,
                    )
                    maintenanceViewModel.addRecord(record)
                    if (mileage != null) {
                        carsViewModel.updateCarMileage(carId, mileage)
                    }
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

            var record by remember { mutableStateOf<com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord?>(null) }
            LaunchedEffect(recordId) {
                record = maintenanceViewModel.getRecordById(recordId)
            }

            if (record != null) {
                MaintenanceDetailScreen(
                    record = record!!,
                    onBack = { navController.popBackStack() },
                    onDelete = { id ->
                        maintenanceViewModel.deleteRecord(id)
                        navController.popBackStack()
                    }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Запись не найдена",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Назад")
                    }
                }
            }
        }

        composable(
            route = Routes.REMINDERS,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) {
            RemindersScreen()
        }

        composable(
            route = Routes.PARTS,
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            val car = cars.find { it.id == carId }

            LaunchedEffect(carId) {
                partsViewModel.loadForCar(carId)
            }

            val parts by partsViewModel.parts.collectAsState()
            val partsFilters by partsViewModel.partsFilters.collectAsState()
            val partsError by partsViewModel.error.collectAsState()

            PartsScreen(
                carName = car?.displayName() ?: "—",
                parts = parts,
                categories = maintenanceCategories,
                workTypes = maintenanceWorkTypes,
                filters = partsFilters,
                error = partsError,
                onBack = { navController.popBackStack() },
                onFiltersChange = { partsViewModel.updateFilters(it) },
                onUpdateImpression = { recordId, impression ->
                    partsViewModel.updateImpression(recordId, impression)
                },
                onErrorConsumed = { partsViewModel.clearError() }
            )
        }
    }
}
