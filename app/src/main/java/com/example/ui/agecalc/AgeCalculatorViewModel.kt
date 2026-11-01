package com.example.ui.agecalc

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.data.calculator.AgeCalculatorEngine
import com.example.data.calculator.AgeResult
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class AgeCalcUiState(
  val birthYear: Int = 2000,
  val birthMonth: Int = 1, // 1-12
  val birthDay: Int = 1,
  val targetYear: Int = 2026,
  val targetMonth: Int = 10,
  val targetDay: Int = 10,
  val result: AgeResult? = null,
  val showBirthDatePicker: Boolean = false,
  val showTargetDatePicker: Boolean = false
)

class AgeCalculatorViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(AgeCalcUiState())
  val uiState: StateFlow<AgeCalcUiState> = _uiState.asStateFlow()

  init {
    val cal = Calendar.getInstance()
    val curY = cal.get(Calendar.YEAR)
    val curM = cal.get(Calendar.MONTH) + 1
    val curD = cal.get(Calendar.DAY_OF_MONTH)

    _uiState.value = _uiState.value.copy(
      birthYear = (curY - 24).coerceAtLeast(1900),
      birthMonth = 1,
      birthDay = 1,
      targetYear = curY,
      targetMonth = curM,
      targetDay = curD
    )
    calculate()
  }

  fun onBirthDateSelected(year: Int, month: Int, day: Int) {
    _uiState.value = _uiState.value.copy(
      birthYear = year,
      birthMonth = month,
      birthDay = day,
      showBirthDatePicker = false
    )
    calculate()
  }

  fun onTargetDateSelected(year: Int, month: Int, day: Int) {
    _uiState.value = _uiState.value.copy(
      targetYear = year,
      targetMonth = month,
      targetDay = day,
      showTargetDatePicker = false
    )
    calculate()
  }

  fun setTargetToToday() {
    val cal = Calendar.getInstance()
    _uiState.value = _uiState.value.copy(
      targetYear = cal.get(Calendar.YEAR),
      targetMonth = cal.get(Calendar.MONTH) + 1,
      targetDay = cal.get(Calendar.DAY_OF_MONTH)
    )
    calculate()
  }

  fun openBirthDatePicker() {
    _uiState.value = _uiState.value.copy(showBirthDatePicker = true)
  }

  fun closeBirthDatePicker() {
    _uiState.value = _uiState.value.copy(showBirthDatePicker = false)
  }

  fun openTargetDatePicker() {
    _uiState.value = _uiState.value.copy(showTargetDatePicker = true)
  }

  fun closeTargetDatePicker() {
    _uiState.value = _uiState.value.copy(showTargetDatePicker = false)
  }

  fun calculate() {
    val s = _uiState.value
    val res = AgeCalculatorEngine.calculateAge(
      bYear = s.birthYear,
      bMonth = s.birthMonth,
      bDay = s.birthDay,
      tYear = s.targetYear,
      tMonth = s.targetMonth,
      tDay = s.targetDay
    )
    _uiState.value = _uiState.value.copy(result = res)
  }

  fun copyAgeSummary(language: AppLanguage) {
    val text = generateSummaryText(language)
    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Age Summary", text)
    clipboard.setPrimaryClip(clip)

    val msg = if (language == AppLanguage.BENGALI) "বয়সের হিসাব ক্লিপবোর্ডে কপি করা হয়েছে!" else "Age summary copied!"
    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
  }

  fun shareAgeSummary(language: AppLanguage) {
    val text = generateSummaryText(language)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "My Exact Age Breakdown")
      putExtra(Intent.EXTRA_TEXT, text)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooser = Intent.createChooser(intent, "Share Age Summary").apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    getApplication<Application>().startActivity(chooser)
  }

  private fun generateSummaryText(language: AppLanguage): String {
    val s = _uiState.value
    val res = s.result ?: return ""
    val isBn = language == AppLanguage.BENGALI

    return if (isBn) {
      """
🎂 আমার সঠিক বয়স ও জীবন পরিসংখ্যান:
• জন্ম তারিখ: ${s.birthDay}/${s.birthMonth}/${s.birthYear}
• বর্তমান বয়স: ${res.years} বছর, ${res.months} মাস, ${res.days} দিন
• পরবর্তী জন্মদিন: ${res.nextBirthdayMonths} মাস ${res.nextBirthdayDays} দিন বাকি (${res.nextBirthdayWeekdayBn})
• মোট দিন বেঁচেছি: ${res.totalDays} দিন
• মোট সপ্তাহ: ${res.totalWeeks} সপ্তাহ
• মোট ঘণ্টা: ${res.totalHours} ঘণ্টা
• রাশি: ${res.zodiacSignBn} (${res.zodiacSymbol}) - ${res.zodiacTraitsBn}

Age Calculator অ্যাপ দিয়ে বের করা হয়েছে।
      """.trimIndent()
    } else {
      """
🎂 My Exact Age & Life Milestones:
• Date of Birth: ${s.birthDay}/${s.birthMonth}/${s.birthYear}
• Exact Age: ${res.years} Years, ${res.months} Months, ${res.days} Days
• Next Birthday: in ${res.nextBirthdayMonths} months and ${res.nextBirthdayDays} days (${res.nextBirthdayWeekdayEn})
• Total Days Lived: ${res.totalDays} Days
• Total Weeks: ${res.totalWeeks} Weeks
• Total Hours: ${res.totalHours} Hours
• Zodiac: ${res.zodiacSignEn} (${res.zodiacSymbol}) - ${res.zodiacTraitsEn}

Generated by Age Calculator.
      """.trimIndent()
    }
  }
}
