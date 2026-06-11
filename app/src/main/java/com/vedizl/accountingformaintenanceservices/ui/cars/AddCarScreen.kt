package com.vedizl.accountingformaintenanceservices.ui.cars

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vedizl.accountingformaintenanceservices.data.local.CarMakeEntity
import com.vedizl.accountingformaintenanceservices.data.local.CarModelEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    makes: List<CarMakeEntity>,
    models: List<CarModelEntity>,
    error: String?,
    onErrorConsumed: () -> Unit,
    onBack: () -> Unit,
    onSave: (brand: String, model: String, year: Int, licensePlate: String?, mileage: Int?) -> Unit,
) {
    val years = remember { (1990..2026).toList().sortedDescending() }

    var selectedBrand by remember { mutableStateOf<CarMakeEntity?>(null) }
    var isCustomBrand by remember { mutableStateOf(false) }
    var customBrandText by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var isCustomModel by remember { mutableStateOf(false) }
    var customModelText by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var licensePlate by remember { mutableStateOf("") }
    var mileageText by remember { mutableStateOf("") }
    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var licensePlateError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            onErrorConsumed()
        }
    }

    val brandValid = if (isCustomBrand) customBrandText.isNotBlank() else selectedBrand != null
    val modelValid = if (isCustomModel) customModelText.isNotBlank() else selectedModel != null
    val isFormValid = brandValid && modelValid && selectedYear != null && !licensePlateError

    val filteredModels = remember(selectedBrand, models) {
        if (selectedBrand != null) models.filter { it.makeId == selectedBrand!!.id }
        else emptyList()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Добавить автомобиль",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Информация об автомобиле",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isCustomBrand) {
                        OutlinedTextField(
                            value = customBrandText,
                            onValueChange = { customBrandText = it },
                            label = { Text("Марка") },
                            placeholder = { Text("Введите марку автомобиля") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        TextButton(onClick = {
                            isCustomBrand = false
                            customBrandText = ""
                        }) {
                            Text("Выбрать из списка", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = brandExpanded,
                            onExpandedChange = { brandExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedBrand?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Марка") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = brandExpanded,
                                onDismissRequest = { brandExpanded = false }
                            ) {
                                makes.forEach { make ->
                                    DropdownMenuItem(
                                        text = { Text(make.name) },
                                        onClick = {
                                            selectedBrand = make
                                            selectedModel = null
                                            isCustomModel = false
                                            brandExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("— Ввести марку —") },
                                    onClick = {
                                        isCustomBrand = true
                                        selectedBrand = null
                                        selectedModel = null
                                        isCustomModel = false
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isCustomBrand) {
                        OutlinedTextField(
                            value = customModelText,
                            onValueChange = { customModelText = it },
                            label = { Text("Модель") },
                            placeholder = { Text("Введите модель автомобиля") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else if (isCustomModel) {
                        OutlinedTextField(
                            value = customModelText,
                            onValueChange = { customModelText = it },
                            label = { Text("Модель") },
                            placeholder = { Text("Введите модель автомобиля") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        TextButton(onClick = {
                            isCustomModel = false
                            customModelText = ""
                        }) {
                            Text("Выбрать из списка", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = modelExpanded,
                            onExpandedChange = { if (selectedBrand != null) modelExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedModel ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Модель") },
                                enabled = selectedBrand != null,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = modelExpanded,
                                onDismissRequest = { modelExpanded = false }
                            ) {
                                filteredModels.forEach { model ->
                                    DropdownMenuItem(
                                        text = { Text(model.name) },
                                        onClick = {
                                            selectedModel = model.name
                                            modelExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("— Ввести модель —") },
                                    onClick = {
                                        isCustomModel = true
                                        selectedModel = null
                                        modelExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = yearExpanded,
                        onExpandedChange = { yearExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedYear?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Год выпуска") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year.toString()) },
                                    onClick = {
                                        selectedYear = year
                                        yearExpanded = false
                                    }
                                )
                            }
                        }
                    }

                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Пробег и госномер (необязательно)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mileageText,
                        onValueChange = { newVal ->
                            if (newVal.isEmpty() || newVal.all { it.isDigit() }) {
                                mileageText = newVal
                            }
                        },
                        label = { Text("Пробег (км)") },
                        placeholder = { Text("например, 50000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = licensePlate,
                        onValueChange = { newVal ->
                            val filtered = newVal.take(7).filter { it.isLetterOrDigit() }
                            licensePlate = filtered
                            licensePlateError = filtered.isNotEmpty() && !filtered.matches(
                                Regex("^[0-9]{0,4}[A-Za-zА-Яа-яЁё]{0,2}[0-9]{0,1}$")
                            )
                        },
                        label = { Text("Госномер") },
                        placeholder = { Text("например, 1234AB1") },
                        singleLine = true,
                        isError = licensePlateError,
                        supportingText = if (licensePlateError) {
                            { Text("Формат: 1234AB1") }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Отмена")
                }
                Button(
                    onClick = {
                        if (isFormValid) {
                            val resolvedBrand = if (isCustomBrand) customBrandText else selectedBrand!!.name
                            val resolvedModel = if (isCustomModel || isCustomBrand) {
                                if (isCustomModel) customModelText else if (isCustomBrand) customModelText else selectedModel!!
                            } else selectedModel!!
                            onSave(
                                resolvedBrand,
                                resolvedModel,
                                selectedYear!!,
                                licensePlate.ifEmpty { null },
                                mileageText.toIntOrNull()
                            )
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.width(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сохранить")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
