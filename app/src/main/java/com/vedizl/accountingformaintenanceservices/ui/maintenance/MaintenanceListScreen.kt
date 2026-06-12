package com.vedizl.accountingformaintenanceservices.ui.maintenance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vedizl.accountingformaintenanceservices.data.local.CategoryEntity
import com.vedizl.accountingformaintenanceservices.data.local.WorkTypeEntity
import com.vedizl.accountingformaintenanceservices.data.model.MaintenanceRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    carId: String,
    carName: String,
    records: List<MaintenanceRecord>,
    filters: MaintenanceFilters,
    categories: List<CategoryEntity>,
    workTypes: List<WorkTypeEntity>,
    error: String?,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRecordClick: (String) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onFiltersChange: (MaintenanceFilters) -> Unit,
    onRemindersClick: () -> Unit,
    onPartsClick: () -> Unit,
    onUpdateMileage: (carId: String, mileage: Int) -> Unit,
    onErrorConsumed: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var showMileageDialog by remember { mutableStateOf(false) }
    var mileageText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            onErrorConsumed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = carName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (records.isNotEmpty()) {
                            Text(
                                text = "${records.size} ${pluralizeRecords(records.size)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтр",
                            tint = if (filters != MaintenanceFilters())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        mileageText = ""
                        showMileageDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Обновить пробег"
                        )
                    }
                    IconButton(onClick = onRemindersClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Напоминания"
                        )
                    }
                    IconButton(onClick = onPartsClick) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Артикулы"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить запись"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showFilters) {
                FilterSection(
                    filters = filters,
                    categories = categories,
                    workTypes = workTypes,
                    onFiltersChange = onFiltersChange
                )
            }

            if (records.isEmpty()) {
                EmptyRecordsState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordCard(
                            record = record,
                            onClick = { onRecordClick(record.id) },
                            onDelete = { showDeleteDialog = record.id }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { recordId ->
        val record = records.find { it.id == recordId }
        if (record != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Удалить запись?") },
                text = { Text("Вы уверены, что хотите удалить «${record.type}» от ${formatDate(record.dateEpochDay)}?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteRecord(recordId)
                        showDeleteDialog = null
                    }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }

    if (showMileageDialog) {
        AlertDialog(
            onDismissRequest = { showMileageDialog = false },
            title = { Text("Обновить пробег", fontWeight = FontWeight.SemiBold) },
            text = {
                OutlinedTextField(
                    value = mileageText,
                    onValueChange = { if (it.all { c -> c.isDigit() } || it.isEmpty()) mileageText = it },
                    label = { Text("Пробег (км)") },
                    placeholder = { Text("например, 50000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (mileageText.isNotEmpty()) {
                        onUpdateMileage(carId, mileageText.toInt())
                        showMileageDialog = false
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMileageDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    filters: MaintenanceFilters,
    categories: List<CategoryEntity>,
    workTypes: List<WorkTypeEntity>,
    onFiltersChange: (MaintenanceFilters) -> Unit,
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }

    val selectedCategory = filters.category
    val selectedType = filters.type

    val availableTypes = if (selectedCategory != null) {
        categories.find { it.name == selectedCategory }
            ?.let { cat -> workTypes.filter { it.categoryId == cat.id }.map { it.name } }
            ?: emptyList()
    } else {
        workTypes.map { it.name }.distinct()
    }

    val hasActiveFilters = selectedCategory != null || selectedType != null || filters.dateFrom != null || filters.dateTo != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Фильтры",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory ?: "Все категории",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Все категории") },
                        onClick = {
                            onFiltersChange(filters.copy(category = null, type = null))
                            categoryExpanded = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onFiltersChange(filters.copy(
                                    category = category.name,
                                    type = if (selectedCategory != category.name) null else selectedType
                                ))
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType ?: "Все виды",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Вид работы") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Все виды") },
                        onClick = {
                            onFiltersChange(filters.copy(type = null))
                            typeExpanded = false
                        }
                    )
                    availableTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                onFiltersChange(filters.copy(type = type))
                                typeExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = if (filters.dateFrom != null) formatFilterDate(filters.dateFrom) else "—",
                onValueChange = {},
                readOnly = true,
                label = { Text("От") },
                trailingIcon = {
                    TextButton(onClick = {
                        if (filters.dateFrom != null) {
                            onFiltersChange(filters.copy(dateFrom = null))
                        } else {
                            showDateFromPicker = true
                        }
                    }) {
                        Text(if (filters.dateFrom != null) "✕" else "Выбрать")
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = if (filters.dateTo != null) formatFilterDate(filters.dateTo) else "—",
                onValueChange = {},
                readOnly = true,
                label = { Text("До") },
                trailingIcon = {
                    TextButton(onClick = {
                        if (filters.dateTo != null) {
                            onFiltersChange(filters.copy(dateTo = null))
                        } else {
                            showDateToPicker = true
                        }
                    }) {
                        Text(if (filters.dateTo != null) "✕" else "Выбрать")
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onFiltersChange(MaintenanceFilters()) }) {
                Text("Сбросить фильтры", color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showDateFromPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = filters.dateFrom?.times(86400000L)
        )
        DatePickerDialog(
            onDismissRequest = { showDateFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onFiltersChange(filters.copy(dateFrom = millis / 86400000L))
                    }
                    showDateFromPicker = false
                }) { Text("Выбрать") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (filters.dateFrom != null) {
                        onFiltersChange(filters.copy(dateFrom = null))
                    }
                    showDateFromPicker = false
                }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDateToPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = filters.dateTo?.times(86400000L)
        )
        DatePickerDialog(
            onDismissRequest = { showDateToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onFiltersChange(filters.copy(dateTo = millis / 86400000L))
                    }
                    showDateToPicker = false
                }) { Text("Выбрать") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (filters.dateTo != null) {
                        onFiltersChange(filters.copy(dateTo = null))
                    }
                    showDateToPicker = false
                }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatFilterDate(epochDay: Long): String {
    return try {
        LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: Exception) {
        "—"
    }
}

@Composable
private fun RecordCard(
    record: MaintenanceRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatDate(record.dateEpochDay),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = record.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyRecordsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет записей об обслуживании",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Нажмите + чтобы добавить первую запись",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun pluralizeRecords(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "запись"
        count % 10 in 2..4 && (count % 100 < 10 || count % 100 >= 20) -> "записи"
        else -> "записей"
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun formatDate(epochDay: Long): String {
    return try {
        LocalDate.ofEpochDay(epochDay).format(dateFormatter)
    } catch (e: Exception) {
        "—"
    }
}
