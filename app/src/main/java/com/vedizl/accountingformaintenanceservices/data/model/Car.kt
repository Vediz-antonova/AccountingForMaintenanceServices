package com.vedizl.accountingformaintenanceservices.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val brand: String,
    val model: String,
    val year: Int,
    val licensePlate: String? = null,
    val mileage: Int? = null,
) {
    fun displayName(): String = "$brand $model"
}

data class CarMake(
    val name: String,
    val models: List<String>
)

object CarMakes {
    val makes: List<CarMake> = listOf(
        CarMake("Audi", listOf("A3", "A4", "A5", "A6", "A7", "A8", "Q3", "Q5", "Q7", "Q8", "e-tron", "TT")),
        CarMake("BMW", listOf("1 Series", "2 Series", "3 Series", "4 Series", "5 Series", "6 Series", "7 Series", "X1", "X3", "X5", "X6", "i4", "iX")),
        CarMake("Chery", listOf("Tiggo 2", "Tiggo 4", "Tiggo 7", "Tiggo 8", "Tiggo 9", "Tiggo FL", "Tiggo T11", "Arrizo 5", "Arrizo 6", "Arrizo 8")),
        CarMake("Chevrolet", listOf("Aveo", "Camaro", "Captiva", "Cruze", "Equinox", "Lacetti", "Malibu", "Niva", "Spark", "Tahoe", "Traverse")),
        CarMake("Citroen", listOf("Berlingo", "C1", "C3", "C4", "C5", "DS3", "DS4", "DS7", "Jumper", "SpaceTourer")),
        CarMake("Daewoo", listOf("Gentra", "Lanos", "Matiz", "Nexia", "Leganza")),
        CarMake("Fiat", listOf("500", "Doblo", "Ducato", "Panda", "Punto", "Tipo", "Ulysse")),
        CarMake("Ford", listOf("Escape", "Expedition", "Explorer", "Fiesta", "Focus", "Fusion", "Kuga", "Mondeo", "Mustang", "Ranger", "S-Max", "Tourneo")),
        CarMake("Geely", listOf("Atlas", "Coolray", "Emgrand", "Monjaro", "Okavango", "Tugella", "Preface")),
        CarMake("Honda", listOf("Accord", "Civic", "CR-V", "HR-V", "Jazz", "Odyssey", "Pilot")),
        CarMake("Hyundai", listOf("Accent", "Elantra", "Grandeur", "i10", "i20", "i30", "Kona", "Santa Fe", "Solaris", "Sonata", "Tucson", "Creta")),
        CarMake("Infiniti", listOf("Q50", "Q60", "Q70", "QX50", "QX60", "QX80")),
        CarMake("Jaguar", listOf("E-Pace", "F-Pace", "F-Type", "I-Pace", "XE", "XF", "XJ")),
        CarMake("Jeep", listOf("Cherokee", "Compass", "Gladiator", "Grand Cherokee", "Renegade", "Wrangler")),
        CarMake("Kia", listOf("Ceed", "Cerato", "K5", "K9", "Mohave", "Niro", "Optima", "Picanto", "Rio", "Sorento", "Soul", "Sportage", "Stinger")),
        CarMake("Land Rover", listOf("Defender", "Discovery", "Discovery Sport", "Evoque", "Range Rover", "Range Rover Sport", "Velar")),
        CarMake("Lexus", listOf("ES", "GS", "IS", "LS", "LX", "NX", "RC", "RX", "UX")),
        CarMake("Mazda", listOf("2", "3", "5", "6", "CX-3", "CX-5", "CX-7", "CX-9", "CX-30", "MX-5")),
        CarMake("Mercedes-Benz", listOf("A-Class", "C-Class", "E-Class", "S-Class", "G-Class", "GLA", "GLC", "GLE", "GLS", "ML", "V-Class", "Sprinter")),
        CarMake("Mitsubishi", listOf("ASX", "L200", "Lancer", "Outlander", "Pajero", "Pajero Sport")),
        CarMake("Nissan", listOf("Almera", "Juke", "Leaf", "Murano", "Navara", "Note", "Pathfinder", "Patrol", "Qashqai", "Sentra", "Teana", "X-Trail")),
        CarMake("Opel", listOf("Astra", "Corsa", "Insignia", "Mokka", "Vivaro", "Zafira")),
        CarMake("Peugeot", listOf("108", "208", "3008", "308", "4008", "408", "5008", "508", "Boxer", "Partner", "Rifter")),
        CarMake("Porsche", listOf("718", "911", "Cayenne", "Macan", "Panamera", "Taycan")),
        CarMake("Renault", listOf("Arkana", "Captur", "Clio", "Duster", "Espace", "Kadjar", "Kangoo", "Koleos", "Laguna", "Logan", "Megane", "Sandero", "Scenic")),
        CarMake("Skoda", listOf("Citigo", "Enyaq", "Fabia", "Kamiq", "Karoq", "Kodiaq", "Octavia", "Rapid", "Scala", "Superb")),
        CarMake("Subaru", listOf("BRZ", "Forester", "Impreza", "Legacy", "Outback", "XV")),
        CarMake("Suzuki", listOf("Ignis", "Jimny", "S-Cross", "Swift", "Vitara")),
        CarMake("Tesla", listOf("Model 3", "Model S", "Model X", "Model Y", "Cybertruck")),
        CarMake("Toyota", listOf("4Runner", "Auris", "Avensis", "Camry", "Celica", "Corolla", "Fortuner", "Highlander", "Hilux", "Land Cruiser", "Prius", "RAV4", "Supra", "Yaris")),
        CarMake("Volkswagen", listOf("Amarok", "Arteon", "Beetle", "Caddy", "Crafter", "Golf", "ID.3", "ID.4", "ID.5", "Jetta", "Multivan", "Passat", "Polo", "T-Cross", "T-Roc", "Tiguan", "Touareg", "Touran", "Transporter")),
        CarMake("Volvo", listOf("C40", "S40", "S60", "S80", "S90", "V40", "V60", "V90", "XC40", "XC60", "XC90")),
        CarMake("ВАЗ (Lada)", listOf("2101", "2105", "2106", "2107", "2109", "21099", "2110", "2111", "2112", "2114", "2115", "Granta", "Kalina", "Largus", "Niva", "Priora", "Vesta", "X-Ray")),
        CarMake("ГАЗ", listOf("21 Волга", "24 Волга", "3102 Волга", "3110 Волга", "Sobol", "Gazelle", "Gazelle Next")),
        CarMake("УАЗ", listOf("469", "Hunter", "Patriot", "Pickup", "Profy")),
        CarMake("ЗАЗ", listOf("965", "968", "1102 Таврия", "1103 Славута", "Chance", "Sens", "Vida")),
    )

    val years: List<Int> = (1990..2026).toList()
}
