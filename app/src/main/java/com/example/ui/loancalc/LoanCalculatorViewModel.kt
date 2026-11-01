package com.example.ui.loancalc

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.data.calculator.LoanCalculationResult
import com.example.data.calculator.LoanCalculatorEngine
import com.example.data.calculator.LoanCategoryPreset
import com.example.data.calculator.LoanInterestType
import com.example.data.calculator.LoanTenureUnit
import com.example.data.model.AppLanguage
import com.example.ui.util.HistoryDateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

enum class ScheduleViewMode {
  YEARLY,
  MONTHLY
}

data class LoanCalcUiState(
  val principalInput: String = "500000",
  val interestRateInput: String = "9.5",
  val tenureInput: String = "5",
  val tenureUnit: LoanTenureUnit = LoanTenureUnit.YEARS,
  val interestType: LoanInterestType = LoanInterestType.REDUCING_BALANCE,
  val selectedPreset: LoanCategoryPreset = LoanCategoryPreset.CUSTOM,
  val currencySymbol: String = "৳",
  val extraMonthlyPaymentInput: String = "",
  val scheduleViewMode: ScheduleViewMode = ScheduleViewMode.YEARLY,
  val showSchedule: Boolean = true,
  val result: LoanCalculationResult? = null
)

class LoanCalculatorViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = com.example.data.local.CalculationRepository(application)

  private val _uiState = MutableStateFlow(
    LoanCalcUiState(currencySymbol = repository.getPreferredCurrency())
  )
  val uiState: StateFlow<LoanCalcUiState> = _uiState.asStateFlow()

  init {
    calculate()
  }

  fun onPrincipalChange(newValue: String) {
    val clean = newValue.filter { it.isDigit() || it == '.' }
    _uiState.value = _uiState.value.copy(
      principalInput = clean,
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun addPrincipal(amount: Double) {
    val current = _uiState.value.principalInput.toDoubleOrNull() ?: 0.0
    val newTotal = (current + amount).coerceAtLeast(0.0)
    _uiState.value = _uiState.value.copy(
      principalInput = newTotal.roundToInt().toString(),
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun clearPrincipal() {
    _uiState.value = _uiState.value.copy(
      principalInput = "",
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun onInterestRateChange(newValue: String) {
    val clean = newValue.filter { it.isDigit() || it == '.' }
    _uiState.value = _uiState.value.copy(
      interestRateInput = clean,
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun adjustRate(delta: Double) {
    val current = _uiState.value.interestRateInput.toDoubleOrNull() ?: 0.0
    val updated = (current + delta).coerceIn(0.1, 50.0)
    _uiState.value = _uiState.value.copy(
      interestRateInput = String.format(java.util.Locale.US, "%.1f", updated),
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun onTenureChange(newValue: String) {
    val clean = newValue.filter { it.isDigit() }
    _uiState.value = _uiState.value.copy(
      tenureInput = clean,
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun adjustTenure(delta: Int) {
    val current = _uiState.value.tenureInput.toIntOrNull() ?: 1
    val minVal = 1
    val maxVal = if (_uiState.value.tenureUnit == LoanTenureUnit.YEARS) 40 else 480
    val updated = (current + delta).coerceIn(minVal, maxVal)
    _uiState.value = _uiState.value.copy(
      tenureInput = updated.toString(),
      selectedPreset = LoanCategoryPreset.CUSTOM
    )
    calculate()
  }

  fun onTenureUnitChange(unit: LoanTenureUnit) {
    val currentTenure = _uiState.value.tenureInput.toIntOrNull() ?: 5
    val convertedTenure = if (unit == LoanTenureUnit.MONTHS && _uiState.value.tenureUnit == LoanTenureUnit.YEARS) {
      (currentTenure * 12).toString()
    } else if (unit == LoanTenureUnit.YEARS && _uiState.value.tenureUnit == LoanTenureUnit.MONTHS) {
      (currentTenure / 12).coerceAtLeast(1).toString()
    } else {
      _uiState.value.tenureInput
    }

    _uiState.value = _uiState.value.copy(
      tenureUnit = unit,
      tenureInput = convertedTenure
    )
    calculate()
  }

  fun onInterestTypeChange(type: LoanInterestType) {
    _uiState.value = _uiState.value.copy(interestType = type)
    calculate()
  }

  fun onPresetSelected(preset: LoanCategoryPreset) {
    if (preset == LoanCategoryPreset.CUSTOM) {
      _uiState.value = _uiState.value.copy(selectedPreset = preset)
      return
    }

    val isYears = _uiState.value.tenureUnit == LoanTenureUnit.YEARS
    val tenureStr = if (isYears) {
      preset.defaultTenureYears.toString()
    } else {
      (preset.defaultTenureYears * 12).toString()
    }

    _uiState.value = _uiState.value.copy(
      selectedPreset = preset,
      principalInput = preset.defaultPrincipal.roundToInt().toString(),
      interestRateInput = String.format(java.util.Locale.US, "%.1f", preset.defaultRate),
      tenureInput = tenureStr
    )
    calculate()
  }

  fun onCurrencyChange(symbol: String) {
    repository.savePreferredCurrency(symbol)
    _uiState.value = _uiState.value.copy(currencySymbol = symbol)
  }

  fun onExtraMonthlyPaymentChange(newValue: String) {
    val clean = newValue.filter { it.isDigit() || it == '.' }
    _uiState.value = _uiState.value.copy(extraMonthlyPaymentInput = clean)
    calculate()
  }

  fun setScheduleViewMode(mode: ScheduleViewMode) {
    _uiState.value = _uiState.value.copy(scheduleViewMode = mode)
  }

  fun toggleSchedule() {
    _uiState.value = _uiState.value.copy(showSchedule = !_uiState.value.showSchedule)
  }

  fun calculate() {
    val s = _uiState.value
    val principal = s.principalInput.toDoubleOrNull() ?: 0.0
    val rate = s.interestRateInput.toDoubleOrNull() ?: 0.0
    val tenure = s.tenureInput.toIntOrNull() ?: 1
    val extraMonthly = s.extraMonthlyPaymentInput.toDoubleOrNull() ?: 0.0

    val res = LoanCalculatorEngine.calculateLoan(
      principal = principal,
      annualRate = rate,
      tenure = tenure,
      tenureUnit = s.tenureUnit,
      interestType = s.interestType,
      extraMonthlyPayment = extraMonthly
    )
    _uiState.value = _uiState.value.copy(result = res)
  }

  fun copySummary(language: AppLanguage) {
    val text = generateSummaryText(language)
    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Loan Summary", text)
    clipboard.setPrimaryClip(clip)

    val msg = if (language == AppLanguage.BENGALI) "লোনের হিসাব কপি করা হয়েছে!" else "Loan summary copied to clipboard!"
    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
  }

  fun shareSummary(language: AppLanguage) {
    val text = generateSummaryText(language)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Loan & EMI Calculation Report")
      putExtra(Intent.EXTRA_TEXT, text)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooser = Intent.createChooser(intent, "Share Loan Report").apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    getApplication<Application>().startActivity(chooser)
  }

  private fun generateSummaryText(language: AppLanguage): String {
    val s = _uiState.value
    val res = s.result ?: return ""
    val isBn = language == AppLanguage.BENGALI
    val curr = s.currencySymbol

    val emiStr = LoanCalculatorEngine.formatCurrency(res.monthlyEmi, curr)
    val princStr = LoanCalculatorEngine.formatCurrency(res.principal, curr)
    val intStr = LoanCalculatorEngine.formatCurrency(res.totalInterest, curr)
    val totalStr = LoanCalculatorEngine.formatCurrency(res.totalPayment, curr)

    val tenureText = if (s.tenureUnit == LoanTenureUnit.YEARS) {
      if (isBn) "${s.tenureInput} বছর (${res.tenureMonths} মাস)" else "${s.tenureInput} Years (${res.tenureMonths} Months)"
    } else {
      if (isBn) "${s.tenureInput} মাস" else "${s.tenureInput} Months"
    }

    val typeText = when (s.interestType) {
      LoanInterestType.REDUCING_BALANCE -> if (isBn) "হ্রাসমান ব্যালান্স (Reducing EMI)" else "Reducing Balance (Standard EMI)"
      LoanInterestType.FLAT_RATE -> if (isBn) "ফ্ল্যাট রেট (Flat Rate)" else "Flat Rate"
    }

    val prepText = res.prepaymentBenefit?.let { pb ->
      if (isBn) {
        """
        
• অতিরিক্ত কিস্তি: ${LoanCalculatorEngine.formatCurrency(pb.extraMonthlyPayment, curr)} / মাস
• সময় সাশ্রয়: ${pb.monthsSaved} মাস (${String.format(java.util.Locale.US, "%.1f", pb.monthsSaved / 12.0)} বছর)
• সুদ সাশ্রয়: ${LoanCalculatorEngine.formatCurrency(pb.interestSaved, curr)}
        """.trimIndent()
      } else {
        """
        
• Extra Monthly: ${LoanCalculatorEngine.formatCurrency(pb.extraMonthlyPayment, curr)} / month
• Tenure Saved: ${pb.monthsSaved} months (${String.format(java.util.Locale.US, "%.1f", pb.monthsSaved / 12.0)} yrs)
• Interest Saved: ${LoanCalculatorEngine.formatCurrency(pb.interestSaved, curr)}
        """.trimIndent()
      }
    } ?: ""

    return if (isBn) {
      val raw = """
🏦 লোন ও EMI হিসাবের সারাংশ:
----------------------------------
• মূল ঋণ (Principal): $princStr
• বার্ষিক সুদের হার: ${s.interestRateInput}%
• ঋণের মেয়াদ: $tenureText
• সুদের পদ্ধতি: $typeText
----------------------------------
💵 প্রতি মাসের কিস্তি (EMI): $emiStr
💰 মোট প্রদেয় সুদ: $intStr (${String.format(java.util.Locale.US, "%.1f", res.interestPercentage)}%)
💳 সুদসহ মোট পরিশোধ: $totalStr
$prepText

All Calculator অ্যাপ দ্বারা তৈরি।
      """.trimIndent()
      HistoryDateFormatter.toBengaliDigits(raw)
    } else {
      """
🏦 Loan & EMI Calculation Summary:
----------------------------------
• Principal Amount: $princStr
• Interest Rate: ${s.interestRateInput}% p.a.
• Tenure: $tenureText
• Method: $typeText
----------------------------------
💵 Monthly EMI: $emiStr
💰 Total Interest: $intStr (${String.format(java.util.Locale.US, "%.1f", res.interestPercentage)}%)
💳 Total Repayment: $totalStr
$prepText

Generated by All Calculator.
      """.trimIndent()
    }
  }
}
