package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String) {
  BENGALI("bn", "বাংলা"),
  ENGLISH("en", "English")
}

data class CurrencyOption(
  val symbol: String,
  val label: String,
  val code: String
)

val SupportedCurrencies = listOf(
  CurrencyOption("৳", "BDT (৳)", "BDT"),
  CurrencyOption("$", "USD ($)", "USD"),
  CurrencyOption("₹", "INR (₹)", "INR"),
  CurrencyOption("USDT", "USDT", "USDT"),
  CurrencyOption("LGNS", "LGNS", "LGNS"),
  CurrencyOption("€", "EUR (€)", "EUR"),
  CurrencyOption("", "ইউনিট নেই / None", "NONE")
)

data class CalculationInput(
  val principal: Double = 10000.0,
  val dailyRatePercent: Double = 1.0,
  val days: Int = 30,
  val currency: String = "৳",
  val isCompound: Boolean = true,
  val dailyAddition: Double = 0.0
)

data class DailyRecord(
  val day: Int,
  val opening: Double,
  val reward: Double,
  val closing: Double,
  val cumulativeReward: Double,
  val simpleComparisonClosing: Double
)

data class CalculationResult(
  val input: CalculationInput,
  val finalBalance: Double,
  val totalProfit: Double,
  val initialPrincipal: Double,
  val totalReturnPercent: Double,
  val simpleProfit: Double,
  val compoundDifference: Double,
  val dailyAverageReward: Double,
  val dailyRecords: List<DailyRecord>
)

data class SavedCalculation(
  val id: Long,
  val title: String,
  val timestamp: Long,
  val principal: Double,
  val dailyRate: Double,
  val days: Int,
  val currency: String,
  val isCompound: Boolean,
  val finalBalance: Double,
  val totalProfit: Double
)
