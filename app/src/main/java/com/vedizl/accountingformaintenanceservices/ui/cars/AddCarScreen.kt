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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vedizl.accountingformaintenanceservices.data.model.CarMake
import com.vedizl.accountingformaintenanceservices.data.model.CarMakes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    onBack: () -> Unit,
    onSave: (brand: String, model: String, year: Int, licensePlate: String?, mileage: Int?) -> Unit,
) {
    val makes = remember { CarMakes.makes }
    val years = remember { CarMakes.years }

    var selectedBrand by remember { mutableStateOf<CarMake?>(null) }
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var licensePlate by remember { mutableStateOf("") }
    var mileageText by remember { mutableStateOf("") }
    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var licensePlateError by remember { mutableStateOf(false) }

    val isFormValid = selectedBrand != null
            && selectedModel != null
            && selectedYear != null
            && !licensePlateError

    Scaffold(
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
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
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
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = modelExpanded,
                        onExpandedChange = {
                            if (selectedBrand != null) modelExpanded = it
                        }
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
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = modelExpanded,
                            onDismissRequest = { modelExpanded = false }
                        ) {
                            selectedBrand?.models?.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        selectedModel = model
                                        modelExpanded = false
                                    }
                                )
                            }
                        }
                    }

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
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            years.reversed().forEach { year ->
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
                        text = "Дополнительная информация",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Поля можно заполнить позже",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = licensePlate,
                        onValueChange = { value ->
                            val filtered = value.filter { it.isDigit() || it.isLetter() }.uppercase().take(7)
                            licensePlate = filtered
                            if (filtered.isNotEmpty()) {
                                val digits = filtered.takeWhile { it.isDigit() }.length
                                val letters = filtered.drop(digits).takeWhile { it.isLetter() }.length
                                val lastDigit = filtered.drop(digits + letters).takeWhile { it.isDigit() }.length
                                licensePlateError = !(
                                        digits in 1..4
                                                && letters in 0..2
                                                && lastDigit in 0..1
                                                && digits + letters + lastDigit == filtered.length
                                        )
                            } else {
                                licensePlateError = false
                            }
                        },
                        label = { Text("Госномер") },
                        placeholder = { Text("1234AB1") },
                        supportingText = {
                            Text("Формат: 4 цифры, 2 буквы, 1 цифра")
                        },
                        isError = licensePlateError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (licensePlateError) {
                        Text(
                            text = "Неверный формат. Используйте формат: 1234AB1",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mileageText,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() } || value.isEmpty()) {
                                mileageText = value
                            }
                        },
                        label = { Text("Пробег (км)") },
                        placeholder = { Text("например, 50000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            onSave(
                                selectedBrand!!.name,
                                selectedModel!!,
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
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(18.dp)
                    )
                    Text("Сохранить")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
