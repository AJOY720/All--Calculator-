package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calculator.RewardCompoundEngine
import com.example.data.local.CalculationRepository
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationInput
import com.example.data.model.CalculationResult
import com.example.data.model.SavedCalculation
import com.example.ui.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class CalculatorUiState(
  val principalText: String = "10000",
  val rateText: String = "1",
  val daysText: String = "30",
  val currency: String = "৳",
  val isCompound: Boolean = true,
  val isDepositSeparate: Boolean = false,
  val dailyAdditionText: String = "0",
  val showAdvanced: Boolean = false,
  val language: AppLanguage = AppLanguage.BENGALI,
  val result: CalculationResult? = null,
  val errorMessage: String? = null,
  val savedList: List<SavedCalculation> = emptyList(),
  val showSaveDialog: Boolean = false,
  val showSavedListDialog: Boolean = false,
  val selectedScrubDay: Int? = null,
  val tableSearchQuery: String = ""
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = CalculationRepository(application)

  private val _uiState = MutableStateFlow(
    CalculatorUiState(currency = repository.getPreferredCurrency())
  )
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  init {
    loadSavedList()
    calculate()
  }

  fun onPrincipalChange(value: String) {
    _uiState.value = _uiState.value.copy(principalText = value, errorMessage = null)
    autoCalculateIfValid()
  }

  fun onRateChange(value: String) {
    _uiState.value = _uiState.value.copy(rateText = value, errorMessage = null)
    autoCalculateIfValid()
  }

  fun onDaysChange(value: String) {
    _uiState.value = _uiState.value.copy(daysText = value, errorMessage = null)
    autoCalculateIfValid()
  }

  fun onCurrencyChange(currency: String) {
    repository.savePreferredCurrency(currency)
    _uiState.value = _uiState.value.copy(currency = currency)
    calculate()
  }

  fun onCompoundToggled(checked: Boolean) {
    _uiState.value = _uiState.value.copy(
      isCompound = checked,
      isDepositSeparate = if (checked) false else _uiState.value.isDepositSeparate
    )
    calculate()
  }

  fun onDepositSeparateToggled(checked: Boolean) {
    _uiState.value = _uiState.value.copy(
      isDepositSeparate = checked,
      isCompound = if (checked) false else _uiState.value.isCompound
    )
    calculate()
  }

  fun onDailyAdditionChange(value: String) {
    _uiState.value = _uiState.value.copy(dailyAdditionText = value)
    autoCalculateIfValid()
  }

  private fun autoCalculateIfValid() {
    val p = _uiState.value.principalText.toDoubleOrNull() ?: return
    val rate = _uiState.value.rateText.toDoubleOrNull() ?: return
    val days = _uiState.value.daysText.toIntOrNull() ?: return
    val addition = _uiState.value.dailyAdditionText.toDoubleOrNull() ?: 0.0

    if (p < 0 || rate < 0 || days < 1 || days > 10000) return

    val input = CalculationInput(
      principal = p,
      dailyRatePercent = rate,
      days = days,
      currency = _uiState.value.currency,
      isCompound = _uiState.value.isCompound && !_uiState.value.isDepositSeparate,
      dailyAddition = addition
    )

    val res = RewardCompoundEngine.calculate(input)
    _uiState.value = _uiState.value.copy(
      result = res,
      errorMessage = null,
      selectedScrubDay = null
    )
  }

  fun toggleAdvanced() {
    _uiState.value = _uiState.value.copy(showAdvanced = !_uiState.value.showAdvanced)
  }

  fun toggleLanguage() {
    val nextLang = if (_uiState.value.language == AppLanguage.BENGALI) {
      AppLanguage.ENGLISH
    } else {
      AppLanguage.BENGALI
    }
    _uiState.value = _uiState.value.copy(language = nextLang)
  }

  fun onScrubDay(day: Int?) {
    _uiState.value = _uiState.value.copy(selectedScrubDay = day)
  }

  fun onTableSearch(query: String) {
    _uiState.value = _uiState.value.copy(tableSearchQuery = query)
  }

  fun applyPresetDays(days: Int) {
    _uiState.value = _uiState.value.copy(daysText = days.toString())
    calculate()
  }

  fun applyPresetRate(rate: Double) {
    _uiState.value = _uiState.value.copy(rateText = if (rate % 1.0 == 0.0) rate.toInt().toString() else rate.toString())
    calculate()
  }

  fun addPrincipal(delta: Double) {
    val current = _uiState.value.principalText.toDoubleOrNull() ?: 0.0
    val updated = (current + delta).coerceAtLeast(0.0)
    _uiState.value = _uiState.value.copy(
      principalText = if (updated % 1.0 == 0.0) updated.toLong().toString() else updated.toString()
    )
    calculate()
  }

  fun calculate() {
    val p = _uiState.value.principalText.toDoubleOrNull()
    val rate = _uiState.value.rateText.toDoubleOrNull()
    val days = _uiState.value.daysText.toIntOrNull()
    val addition = _uiState.value.dailyAdditionText.toDoubleOrNull() ?: 0.0

    if (p == null || p < 0 || rate == null || rate < 0 || days == null || days < 1 || days > 10000) {
      val isBn = _uiState.value.language == AppLanguage.BENGALI
      _uiState.value = _uiState.value.copy(
        errorMessage = if (isBn) {
          "সঠিক মূলধন, রিওয়ার্ড হার এবং ১–১০,০০০ দিনের সংখ্যা দিন।"
        } else {
          "Please enter a valid principal, reward rate, and days (1–10,000)."
        }
      )
      return
    }

    val input = CalculationInput(
      principal = p,
      dailyRatePercent = rate,
      days = days,
      currency = _uiState.value.currency,
      isCompound = _uiState.value.isCompound && !_uiState.value.isDepositSeparate,
      dailyAddition = addition
    )

    val res = RewardCompoundEngine.calculate(input)
    _uiState.value = _uiState.value.copy(
      result = res,
      errorMessage = null,
      selectedScrubDay = null
    )
  }

  fun openSaveDialog() {
    _uiState.value = _uiState.value.copy(showSaveDialog = true)
  }

  fun closeSaveDialog() {
    _uiState.value = _uiState.value.copy(showSaveDialog = false)
  }

  fun openSavedListDialog() {
    loadSavedList()
    _uiState.value = _uiState.value.copy(showSavedListDialog = true)
  }

  fun closeSavedListDialog() {
    _uiState.value = _uiState.value.copy(showSavedListDialog = false)
  }

  fun saveScenario(title: String) {
    val currentResult = _uiState.value.result ?: return
    repository.saveCalculation(
      title = title,
      input = currentResult.input,
      finalBalance = currentResult.finalBalance,
      totalProfit = currentResult.totalProfit
    )
    loadSavedList()
    closeSaveDialog()
    val isBn = _uiState.value.language == AppLanguage.BENGALI
    Toast.makeText(
      getApplication(),
      if (isBn) "হিসাবটি সফলভাবে সংরক্ষিত হয়েছে!" else "Scenario saved successfully!",
      Toast.LENGTH_SHORT
    ).show()
  }

  fun deleteScenario(id: Long) {
    repository.deleteCalculation(id)
    loadSavedList()
  }

  fun loadScenario(saved: SavedCalculation) {
    _uiState.value = _uiState.value.copy(
      principalText = if (saved.principal % 1.0 == 0.0) saved.principal.toLong().toString() else saved.principal.toString(),
      rateText = if (saved.dailyRate % 1.0 == 0.0) saved.dailyRate.toInt().toString() else saved.dailyRate.toString(),
      daysText = saved.days.toString(),
      currency = saved.currency,
      isCompound = saved.isCompound,
      isDepositSeparate = !saved.isCompound,
      showSavedListDialog = false
    )
    calculate()
  }

  private fun loadSavedList() {
    _uiState.value = _uiState.value.copy(savedList = repository.getSavedCalculations())
  }

  fun copySummaryToClipboard() {
    val result = _uiState.value.result ?: return
    val text = RewardCompoundEngine.generateSummaryText(result, _uiState.value.language)
    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Reward Compound Summary", text)
    clipboard.setPrimaryClip(clip)

    val isBn = _uiState.value.language == AppLanguage.BENGALI
    Toast.makeText(
      getApplication(),
      if (isBn) "সারাংশ ক্লিপবোর্ডে কপি করা হয়েছে!" else "Summary copied to clipboard!",
      Toast.LENGTH_SHORT
    ).show()
  }

  fun shareSummaryText() {
    val result = _uiState.value.result ?: return
    val text = RewardCompoundEngine.generateSummaryText(result, _uiState.value.language)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Reward Compound Calculation")
      putExtra(Intent.EXTRA_TEXT, text)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooser = Intent.createChooser(intent, "Share Summary").apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    getApplication<Application>().startActivity(chooser)
  }

  fun shareCsvReport() {
    val result = _uiState.value.result ?: return
    viewModelScope.launch {
      try {
        val csvContent = RewardCompoundEngine.generateCsv(result, _uiState.value.language)
        val reportsDir = File(getApplication<Application>().cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val file = File(reportsDir, "reward-compound-report.csv")
        FileOutputStream(file).use { out ->
          out.write(csvContent.toByteArray(Charsets.UTF_8))
        }

        val uri: Uri = FileProvider.getUriForFile(
          getApplication(),
          "${getApplication<Application>().packageName}.fileprovider",
          file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
          type = "text/csv"
          putExtra(Intent.EXTRA_STREAM, uri)
          putExtra(Intent.EXTRA_SUBJECT, "Reward Compound Report CSV")
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share CSV Report").apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(chooser)
      } catch (e: Exception) {
        Toast.makeText(getApplication(), "Error exporting CSV: ${e.message}", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
