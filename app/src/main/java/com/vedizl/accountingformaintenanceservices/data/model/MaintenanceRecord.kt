package com.vedizl.accountingformaintenanceservices.data.model

import java.util.UUID

data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val carId: String,
    val category: String,
    val type: String,
    val dateEpochDay: Long,
    val mileage: Int,
    val partNumber: String? = null,
    val partCost: Double? = null,
    val notes: String? = null,
    val partImpression: String? = null,
)

data class Category(
    val name: String,
    val types: List<Type>
)

data class Type(
    val name: String,
    val requiresParts: Boolean,
)

object MaintenanceCategories {
    val categories: List<Category> = listOf(
        Category("Двигатель", listOf(
            Type("Замена масла", requiresParts = true),
            Type("Замена масляного фильтра", requiresParts = true),
            Type("Замена воздушного фильтра", requiresParts = true),
            Type("Замена салонного фильтра", requiresParts = true),
            Type("Замена свечей зажигания", requiresParts = true),
            Type("Замена ремня ГРМ", requiresParts = true),
            Type("Замена топливного фильтра", requiresParts = true),
            Type("Диагностика двигателя", requiresParts = false),
        )),
        Category("Трансмиссия", listOf(
            Type("Замена масла в КПП", requiresParts = true),
            Type("Замена сцепления", requiresParts = true),
            Type("Замена коробки передач", requiresParts = true),
        )),
        Category("Тормозная система", listOf(
            Type("Замена тормозных колодок", requiresParts = true),
            Type("Замена тормозных дисков", requiresParts = true),
            Type("Замена тормозной жидкости", requiresParts = true),
        )),
        Category("Шины", listOf(
            Type("Шиномонтаж", requiresParts = false),
            Type("Балансировка", requiresParts = false),
            Type("Замена шин", requiresParts = true),
            Type("Сход-развал", requiresParts = false),
        )),
        Category("Подвеска", listOf(
            Type("Замена амортизаторов", requiresParts = true),
            Type("Замена сайлентблоков", requiresParts = true),
            Type("Замена рычагов", requiresParts = true),
            Type("Замена пружин", requiresParts = true),
        )),
        Category("Охлаждение", listOf(
            Type("Замена антифриза", requiresParts = true),
            Type("Замена радиатора", requiresParts = true),
            Type("Замена помпы", requiresParts = true),
            Type("Замена термостата", requiresParts = true),
        )),
        Category("Электрика", listOf(
            Type("Замена АКБ", requiresParts = true),
            Type("Замена генератора", requiresParts = true),
            Type("Замена стартера", requiresParts = true),
            Type("Диагностика электрики", requiresParts = false),
        )),
        Category("Кузов", listOf(
            Type("Мойка", requiresParts = false),
            Type("Полировка", requiresParts = false),
            Type("Антикоррозийная обработка", requiresParts = false),
            Type("Покраска", requiresParts = false),
        )),
        Category("Другое", listOf(
            Type("Другое", requiresParts = false),
        )),
    )
}
