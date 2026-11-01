package com.example.data.calculator

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

enum class LoanInterestType {
  REDUCING_BALANCE, // Standard Banking reducing rate EMI
  FLAT_RATE         // Simple flat rate per annum
}

enum class LoanTenureUnit {
  YEARS,
  MONTHS
}

enum class LoanCategoryPreset(
  val defaultRate: Double,
  val defaultTenureYears: Int,
  val defaultPrincipal: Double
) {
  HOME(defaultRate = 8.5, defaultTenureYears = 15, defaultPrincipal = 2500000.0),
  PERSONAL(defaultRate = 12.0, defaultTenureYears = 3, defaultPrincipal = 200000.0),
  CAR(defaultRate = 9.5, defaultTenureYears = 5, defaultPrincipal = 800000.0),
  EDUCATION(defaultRate = 7.5, defaultTenureYears = 5, defaultPrincipal = 500000.0),
  BUSINESS(defaultRate = 11.0, defaultTenureYears = 4, defaultPrincipal = 1000000.0),
  CUSTOM(defaultRate = 10.0, defaultTenureYears = 5, defaultPrincipal = 500000.0)
}

data class LoanMonthItem(
  val month: Int,
  val openingBalance: Double,
  val principalPaid: Double,
  val interestPaid: Double,
  val emi: Double,
  val closingBalance: Double
)

data class LoanYearSummary(
  val year: Int,
  val openingBalance: Double,
  val principalPaid: Double,
  val interestPaid: Double,
  val totalPayment: Double,
  val closingBalance: Double
)

data class PrepaymentBenefit(
  val extraMonthlyPayment: Double,
  val originalMonths: Int,
  val newMonths: Int,
  val monthsSaved: Int,
  val originalInterest: Double,
  val newTotalInterest: Double,
  val interestSaved: Double
)

data class LoanCalculationResult(
  val principal: Double,
  val annualRate: Double,
  val tenureMonths: Int,
  val interestType: LoanInterestType,
  val monthlyEmi: Double,
  val totalInterest: Double,
  val totalPayment: Double,
  val principalPercentage: Double,
  val interestPercentage: Double,
  val yearlySchedule: List<LoanYearSummary>,
  val monthlySchedule: List<LoanMonthItem>,
  val prepaymentBenefit: PrepaymentBenefit?
)

object LoanCalculatorEngine {

  fun calculateLoan(
    principal: Double,
    annualRate: Double,
    tenure: Int,
    tenureUnit: LoanTenureUnit,
    interestType: LoanInterestType,
    extraMonthlyPayment: Double = 0.0
  ): LoanCalculationResult {
    val safePrincipal = principal.coerceAtLeast(0.0)
    val safeRate = annualRate.coerceAtLeast(0.0)
    val totalMonths = when (tenureUnit) {
      LoanTenureUnit.YEARS -> (tenure * 12).coerceIn(1, 480) // up to 40 years
      LoanTenureUnit.MONTHS -> tenure.coerceIn(1, 480)
    }

    if (safePrincipal <= 0.0) {
      return emptyResult(safePrincipal, safeRate, totalMonths, interestType)
    }

    return when (interestType) {
      LoanInterestType.REDUCING_BALANCE -> calculateReducingBalance(
        principal = safePrincipal,
        annualRate = safeRate,
        months = totalMonths,
        extraMonthly = extraMonthlyPayment.coerceAtLeast(0.0)
      )
      LoanInterestType.FLAT_RATE -> calculateFlatRate(
        principal = safePrincipal,
        annualRate = safeRate,
        months = totalMonths,
        extraMonthly = extraMonthlyPayment.coerceAtLeast(0.0)
      )
    }
  }

  private fun calculateReducingBalance(
    principal: Double,
    annualRate: Double,
    months: Int,
    extraMonthly: Double
  ): LoanCalculationResult {
    val monthlyRate = (annualRate / 100.0) / 12.0
    val emi = if (monthlyRate == 0.0) {
      principal / months
    } else {
      val factor = (1.0 + monthlyRate).pow(months.toDouble())
      (principal * monthlyRate * factor) / (factor - 1.0)
    }

    // Build monthly amortization schedule
    var balance = principal
    val monthlyList = ArrayList<LoanMonthItem>(months)
    var totalInterestAccum = 0.0

    for (m in 1..months) {
      if (balance <= 0.001) {
        break
      }
      val opening = balance
      val interestMonth = opening * monthlyRate
      var principalMonth = emi - interestMonth
      if (principalMonth > opening) {
        principalMonth = opening
      }
      val actualEmi = principalMonth + interestMonth
      val closing = (opening - principalMonth).coerceAtLeast(0.0)

      monthlyList.add(
        LoanMonthItem(
          month = m,
          openingBalance = opening,
          principalPaid = principalMonth,
          interestPaid = interestMonth,
          emi = actualEmi,
          closingBalance = closing
        )
      )
      totalInterestAccum += interestMonth
      balance = closing
    }

    val totalPayment = principal + totalInterestAccum
    val principalPct = if (totalPayment > 0) (principal / totalPayment) * 100.0 else 100.0
    val interestPct = if (totalPayment > 0) (totalInterestAccum / totalPayment) * 100.0 else 0.0

    // Yearly grouping
    val yearlyList = groupMonthlyToYearly(monthlyList)

    // Prepayment simulation if user enters extra amount
    val prepayment = if (extraMonthly > 0.0 && monthlyRate > 0.0) {
      simulatePrepayment(principal, monthlyRate, emi, extraMonthly, totalInterestAccum, months)
    } else null

    return LoanCalculationResult(
      principal = principal,
      annualRate = annualRate,
      tenureMonths = months,
      interestType = LoanInterestType.REDUCING_BALANCE,
      monthlyEmi = emi,
      totalInterest = totalInterestAccum,
      totalPayment = totalPayment,
      principalPercentage = principalPct,
      interestPercentage = interestPct,
      yearlySchedule = yearlyList,
      monthlySchedule = monthlyList,
      prepaymentBenefit = prepayment
    )
  }

  private fun calculateFlatRate(
    principal: Double,
    annualRate: Double,
    months: Int,
    extraMonthly: Double
  ): LoanCalculationResult {
    val tenureYears = months / 12.0
    val totalInterest = principal * (annualRate / 100.0) * tenureYears
    val totalPayment = principal + totalInterest
    val emi = totalPayment / months

    val monthlyPrincipal = principal / months
    val monthlyInterest = totalInterest / months

    var balance = principal
    val monthlyList = ArrayList<LoanMonthItem>(months)
    for (m in 1..months) {
      val opening = balance
      val closing = (opening - monthlyPrincipal).coerceAtLeast(0.0)
      monthlyList.add(
        LoanMonthItem(
          month = m,
          openingBalance = opening,
          principalPaid = monthlyPrincipal,
          interestPaid = monthlyInterest,
          emi = emi,
          closingBalance = closing
        )
      )
      balance = closing
    }

    val principalPct = if (totalPayment > 0) (principal / totalPayment) * 100.0 else 100.0
    val interestPct = if (totalPayment > 0) (totalInterest / totalPayment) * 100.0 else 0.0
    val yearlyList = groupMonthlyToYearly(monthlyList)

    val prepayment = if (extraMonthly > 0.0) {
      // In flat rate, extra payment directly reduces remaining tenure
      val newMonthly = emi + extraMonthly
      val newMonths = (totalPayment / newMonthly).roundToInt().coerceAtLeast(1)
      val saved = (months - newMonths).coerceAtLeast(0)
      PrepaymentBenefit(
        extraMonthlyPayment = extraMonthly,
        originalMonths = months,
        newMonths = newMonths,
        monthsSaved = saved,
        originalInterest = totalInterest,
        newTotalInterest = totalInterest, // flat interest contracts usually keep interest
        interestSaved = 0.0
      )
    } else null

    return LoanCalculationResult(
      principal = principal,
      annualRate = annualRate,
      tenureMonths = months,
      interestType = LoanInterestType.FLAT_RATE,
      monthlyEmi = emi,
      totalInterest = totalInterest,
      totalPayment = totalPayment,
      principalPercentage = principalPct,
      interestPercentage = interestPct,
      yearlySchedule = yearlyList,
      monthlySchedule = monthlyList,
      prepaymentBenefit = prepayment
    )
  }

  private fun groupMonthlyToYearly(monthlyList: List<LoanMonthItem>): List<LoanYearSummary> {
    if (monthlyList.isEmpty()) return emptyList()

    val yearly = mutableListOf<LoanYearSummary>()
    var currentYear = 1
    var yearOpening = monthlyList.first().openingBalance
    var yearPrincipal = 0.0
    var yearInterest = 0.0
    var yearClosing = 0.0

    for ((index, item) in monthlyList.withIndex()) {
      if (index % 12 == 0) {
        yearOpening = item.openingBalance
        yearPrincipal = 0.0
        yearInterest = 0.0
      }

      yearPrincipal += item.principalPaid
      yearInterest += item.interestPaid
      yearClosing = item.closingBalance

      val isEndOfYear = (index + 1) % 12 == 0 || index == monthlyList.size - 1
      if (isEndOfYear) {
        yearly.add(
          LoanYearSummary(
            year = currentYear,
            openingBalance = yearOpening,
            principalPaid = yearPrincipal,
            interestPaid = yearInterest,
            totalPayment = yearPrincipal + yearInterest,
            closingBalance = yearClosing
          )
        )
        currentYear++
      }
    }
    return yearly
  }

  private fun simulatePrepayment(
    principal: Double,
    monthlyRate: Double,
    baseEmi: Double,
    extraMonthly: Double,
    originalTotalInterest: Double,
    originalMonths: Int
  ): PrepaymentBenefit {
    val higherEmi = baseEmi + extraMonthly
    var balance = principal
    var newInterestTotal = 0.0
    var monthsCount = 0

    while (balance > 0.001 && monthsCount < 1200) {
      monthsCount++
      val intMonth = balance * monthlyRate
      var princMonth = higherEmi - intMonth
      if (princMonth > balance) {
        princMonth = balance
      }
      newInterestTotal += intMonth
      balance = (balance - princMonth).coerceAtLeast(0.0)
    }

    val savedMonths = (originalMonths - monthsCount).coerceAtLeast(0)
    val savedInterest = (originalTotalInterest - newInterestTotal).coerceAtLeast(0.0)

    return PrepaymentBenefit(
      extraMonthlyPayment = extraMonthly,
      originalMonths = originalMonths,
      newMonths = monthsCount,
      monthsSaved = savedMonths,
      originalInterest = originalTotalInterest,
      newTotalInterest = newInterestTotal,
      interestSaved = savedInterest
    )
  }

  private fun emptyResult(
    principal: Double,
    rate: Double,
    months: Int,
    interestType: LoanInterestType
  ): LoanCalculationResult {
    return LoanCalculationResult(
      principal = principal,
      annualRate = rate,
      tenureMonths = months,
      interestType = interestType,
      monthlyEmi = 0.0,
      totalInterest = 0.0,
      totalPayment = 0.0,
      principalPercentage = 100.0,
      interestPercentage = 0.0,
      yearlySchedule = emptyList(),
      monthlySchedule = emptyList(),
      prepaymentBenefit = null
    )
  }

  fun formatCurrency(amount: Double, currencySymbol: String = "৳"): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
      maximumFractionDigits = 0
      minimumFractionDigits = 0
    }
    val safeAmount = if (amount.isNaN() || amount.isInfinite()) 0L else amount.coerceAtLeast(0.0).roundToLong()
    return "$currencySymbol ${formatter.format(safeAmount)}"
  }

  fun formatCurrencyWithDecimal(amount: Double, currencySymbol: String = "৳"): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
      maximumFractionDigits = 2
      minimumFractionDigits = 2
    }
    val safeAmount = if (amount.isNaN() || amount.isInfinite()) 0.0 else amount.coerceAtLeast(0.0)
    return "$currencySymbol ${formatter.format(safeAmount)}"
  }
}
