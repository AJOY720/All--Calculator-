package com.example.ui.normalcalc

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.data.calculator.ExpressionStats
import com.example.data.calculator.StandardCalculatorEngine
import com.example.data.local.NormalCalcHistoryRepository
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NormalCalcHistoryItem(
  val id: Long = System.currentTimeMillis(),
  val expression: String,
  val result: String,
  val timestamp: Long = System.currentTimeMillis()
)

enum class HistoryFilterPeriod {
  ALL,
  TODAY,
  WEEK,
  MONTH,
  OLDER
}

/**
 * Represents an individual number token inside a chained expression like "150 + 230 + 300".
 * Allows pinpoint editing of any specific number (e.g. changing 150's 1, 5, or 0).
 */
data class NumberToken(
  val id: Int,           // 1-based sequential index (#1, #2, #3...)
  val value: String,     // literal value e.g. "150"
  val startIndex: Int,   // start index in the expression string
  val endIndex: Int      // end index in the expression string (exclusive)
)

data class NormalCalcUiState(
  val expression: String = "",
  val cursorPosition: Int = 0,
  val resultPreview: String = "",         // e.g. "12,729"
  val runningSumBengali: String = "",     // e.g. "১২,৭২৯"
  val amountInWords: String = "",         // e.g. "বারো হাজার সাতশত ঊনত্রিশ টাকা"
  val isCalculated: Boolean = false,      // true when '=' was pressed
  val stats: ExpressionStats? = null,
  val history: List<NormalCalcHistoryItem> = emptyList(),
  val selectedFilter: HistoryFilterPeriod = HistoryFilterPeriod.ALL,
  val historySearchQuery: String = "",
  val isScientific: Boolean = false,
  val isFullScreenHistoryOpen: Boolean = false,
  val isItemizedListOpen: Boolean = false,
  val isStatsDialogOpen: Boolean = false,
  val editingToken: NumberToken? = null,
  val errorMessage: String? = null
) {
  val filteredHistory: List<NormalCalcHistoryItem>
    get() {
      val now = System.currentTimeMillis()
      val oneDay = 24L * 60L * 60L * 1000L
      val periodList = when (selectedFilter) {
        HistoryFilterPeriod.ALL -> history
        HistoryFilterPeriod.TODAY -> {
          history.filter { now - it.timestamp <= oneDay }
        }
        HistoryFilterPeriod.WEEK -> {
          history.filter { now - it.timestamp <= 7 * oneDay }
        }
        HistoryFilterPeriod.MONTH -> {
          history.filter { now - it.timestamp <= 30 * oneDay }
        }
        HistoryFilterPeriod.OLDER -> {
          history.filter { now - it.timestamp > 30 * oneDay }
        }
      }

      val q = historySearchQuery.trim()
      return if (q.isEmpty()) {
        periodList
      } else {
        periodList.filter { item ->
          item.expression.contains(q, ignoreCase = true) ||
          item.result.contains(q, ignoreCase = true)
        }
      }
    }

  /**
   * Extracts all distinct number tokens from the current expression so the user
   * can interactively tap any number in the chain to inspect or edit.
   */
  val numberTokens: List<NumberToken>
    get() = parseNumberTokens(expression)

  companion object {
    fun parseNumberTokens(expr: String): List<NumberToken> {
      if (expr.isBlank()) return emptyList()
      val tokens = mutableListOf<NumberToken>()
      val regex = Regex("""[0-9০-৯]+(?:\.[0-9০-৯]+)?""")
      var count = 1
      for (match in regex.findAll(expr)) {
        tokens.add(
          NumberToken(
            id = count++,
            value = match.value,
            startIndex = match.range.first,
            endIndex = match.range.last + 1
          )
        )
      }
      return tokens
    }
  }
}

class NormalCalculatorViewModel(application: Application) : AndroidViewModel(application) {

  private val historyRepository = NormalCalcHistoryRepository(application)

  private val _uiState = MutableStateFlow(
    NormalCalcUiState(history = historyRepository.getHistory())
  )
  val uiState: StateFlow<NormalCalcUiState> = _uiState.asStateFlow()

  fun setFilter(filter: HistoryFilterPeriod) {
    _uiState.value = _uiState.value.copy(selectedFilter = filter)
  }

  fun setHistorySearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(historySearchQuery = query)
  }

  fun openFullScreenHistory() {
    _uiState.value = _uiState.value.copy(isFullScreenHistoryOpen = true)
  }

  fun closeFullScreenHistory() {
    _uiState.value = _uiState.value.copy(isFullScreenHistoryOpen = false)
  }

  fun toggleFullScreenHistory() {
    _uiState.value = _uiState.value.copy(isFullScreenHistoryOpen = !_uiState.value.isFullScreenHistoryOpen)
  }

  fun openItemizedList() {
    _uiState.value = _uiState.value.copy(isItemizedListOpen = true)
  }

  fun closeItemizedList() {
    _uiState.value = _uiState.value.copy(isItemizedListOpen = false)
  }

  fun openStatsDialog() {
    _uiState.value = _uiState.value.copy(isStatsDialogOpen = true)
  }

  fun closeStatsDialog() {
    _uiState.value = _uiState.value.copy(isStatsDialogOpen = false)
  }

  fun openTokenEditor(token: NumberToken) {
    _uiState.value = _uiState.value.copy(
      editingToken = token,
      cursorPosition = token.startIndex
    )
  }

  fun dismissTokenEditor() {
    _uiState.value = _uiState.value.copy(editingToken = null)
  }

  /**
   * Updates a specific number token inside the chained sequence.
   * E.g. changing the first "150" to "180" or "15" in "150 + 230 + 300 = 680".
   */
  fun updateTokenValue(token: NumberToken, newValue: String) {
    val current = _uiState.value.expression
    val cleanNew = newValue.trim().filter { it.isDigit() || it in '০'..'৯' || it == '.' }
    if (cleanNew.isBlank()) {
      dismissTokenEditor()
      return
    }

    val updated: String
    val newCursor: Int

    if (token.startIndex >= 0 && token.endIndex <= current.length &&
        current.substring(token.startIndex, token.endIndex) == token.value) {
      updated = current.substring(0, token.startIndex) + cleanNew + current.substring(token.endIndex)
      newCursor = token.startIndex + cleanNew.length
    } else {
      val tokens = NormalCalcUiState.parseNumberTokens(current)
      val matching = tokens.find { it.id == token.id }
      if (matching != null) {
        updated = current.substring(0, matching.startIndex) + cleanNew + current.substring(matching.endIndex)
        newCursor = matching.startIndex + cleanNew.length
      } else {
        updated = current.replaceFirst(token.value, cleanNew)
        newCursor = updated.length
      }
    }

    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newCursor.coerceIn(0, updated.length),
      editingToken = null,
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(updated)
  }

  /**
   * Removes a specific item by index from the chained list.
   */
  fun removeItemToken(token: NumberToken) {
    val current = _uiState.value.expression
    val tokens = NormalCalcUiState.parseNumberTokens(current)
    if (tokens.isEmpty()) return

    // If only one token, clear
    if (tokens.size == 1 && tokens[0].id == token.id) {
      onClear()
      return
    }

    // Reconstruct without this token
    val remainingValues = tokens.filterNot { it.id == token.id }.map { it.value }
    val newExpr = remainingValues.joinToString(" + ")

    _uiState.value = _uiState.value.copy(
      expression = newExpr,
      cursorPosition = newExpr.length,
      editingToken = null,
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(newExpr)
  }

  // --- Interactive Cursor Navigation & Direct Editing ---

  fun setCursorPosition(position: Int) {
    val expr = _uiState.value.expression
    _uiState.value = _uiState.value.copy(
      cursorPosition = position.coerceIn(0, expr.length)
    )
  }

  fun moveCursorLeft() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition
    val newPos = (pos - 1).coerceAtLeast(0)
    _uiState.value = _uiState.value.copy(cursorPosition = newPos)
  }

  fun moveCursorRight() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition
    val newPos = (pos + 1).coerceAtMost(current.length)
    _uiState.value = _uiState.value.copy(cursorPosition = newPos)
  }

  fun moveCursorToStart() {
    _uiState.value = _uiState.value.copy(cursorPosition = 0)
  }

  fun moveCursorToEnd() {
    val current = _uiState.value.expression
    _uiState.value = _uiState.value.copy(cursorPosition = current.length)
  }

  fun onExpressionAndCursorChanged(newExpr: String, newCursor: Int) {
    _uiState.value = _uiState.value.copy(
      expression = newExpr,
      cursorPosition = newCursor.coerceIn(0, newExpr.length),
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(newExpr)
  }

  // --- Keypad Actions (Cursor-Aware) ---

  fun onDigit(d: String) {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)

    // If user previously completed calculation and enters a digit, start fresh
    val base = if (_uiState.value.isCalculated) {
      _uiState.value = _uiState.value.copy(isCalculated = false)
      ""
    } else {
      current
    }
    val effectivePos = if (base == "") 0 else pos.coerceIn(0, base.length)

    val before = base.substring(0, effectivePos)
    val after = base.substring(effectivePos)

    // Prevent multiple decimals in the same number chunk
    if (d == ".") {
      val leftDigits = before.takeLastWhile { it.isDigit() || it in '০'..'৯' || it == '.' }
      val rightDigits = after.takeWhile { it.isDigit() || it in '০'..'৯' || it == '.' }
      if (leftDigits.contains('.') || rightDigits.contains('.')) return
    }

    val updated = before + d + after
    val newPos = effectivePos + d.length
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos,
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onOperator(op: String) {
    val current = _uiState.value.expression

    // If calculation was completed, continue chain with the computed result!
    val base = if (_uiState.value.isCalculated && _uiState.value.resultPreview.isNotBlank()) {
      _uiState.value.resultPreview.replace(",", "")
    } else {
      current
    }

    val pos = if (_uiState.value.isCalculated) base.length else _uiState.value.cursorPosition.coerceIn(0, base.length)

    if (base.isBlank()) {
      if (op == "−") {
        _uiState.value = _uiState.value.copy(expression = "−", cursorPosition = 1, isCalculated = false)
      }
      return
    }

    val before = base.substring(0, pos)
    val after = base.substring(pos)

    val trimmedBefore = before.trimEnd()
    val updated: String
    val newPos: Int

    if (trimmedBefore.isNotEmpty() && trimmedBefore.last() in "+−×÷^") {
      val dropOp = trimmedBefore.dropLast(1).trimEnd()
      updated = "$dropOp $op $after"
      newPos = ("$dropOp $op ").length
    } else {
      val insertStr = if (before.endsWith(" ")) "$op " else " $op "
      updated = before + insertStr + after
      newPos = before.length + insertStr.length
    }

    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length),
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onParenthesis() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    val before = current.substring(0, pos)
    val after = current.substring(pos)

    val openCount = current.count { it == '(' }
    val closeCount = current.count { it == ')' }

    val insert = if (before.isEmpty() || before.last() in "+−×÷^( ") {
      "("
    } else if (openCount > closeCount && (before.last().isDigit() || before.last() in '০'..'৯' || before.last() == ')')) {
      ")"
    } else {
      " × ("
    }

    val updated = before + insert + after
    val newPos = pos + insert.length
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length),
      isCalculated = false
    )
    updatePreview(updated)
  }

  fun onSquareRoot() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    val before = current.substring(0, pos)
    val after = current.substring(pos)

    val insert = if (before.isEmpty() || before.last() in "+−×÷^( ") {
      "√("
    } else {
      " × √("
    }

    val updated = before + insert + after
    val newPos = pos + insert.length
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length),
      isCalculated = false
    )
    updatePreview(updated)
  }

  fun onSquare() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    val before = current.substring(0, pos)
    val after = current.substring(pos)

    if (before.isNotBlank() && (before.last().isDigit() || before.last() in '০'..'৯' || before.last() == ')')) {
      val insert = " ^ 2"
      val updated = before + insert + after
      val newPos = pos + insert.length
      _uiState.value = _uiState.value.copy(
        expression = updated,
        cursorPosition = newPos.coerceIn(0, updated.length),
        isCalculated = false
      )
      updatePreview(updated)
    }
  }

  fun onPercentage() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    val before = current.substring(0, pos)
    val after = current.substring(pos)

    if (before.isNotBlank()) {
      val trimmed = before.trimEnd()
      if (trimmed.endsWith("%")) return
      if (trimmed.last().isDigit() || trimmed.last() in '০'..'৯' || trimmed.last() == ')' || trimmed.last() == '.') {
        val insert = "%"
        val updated = trimmed + insert + after
        val newPos = trimmed.length + insert.length
        _uiState.value = _uiState.value.copy(
          expression = updated,
          cursorPosition = newPos.coerceIn(0, updated.length),
          isCalculated = false,
          errorMessage = null
        )
        updatePreview(updated)
      }
    }
  }

  fun onNegate() {
    val current = _uiState.value.expression
    if (current.isNotBlank()) {
      val evalRes = StandardCalculatorEngine.evaluateRunning(current)
      if (evalRes.isSuccess) {
        val neg = -evalRes.getOrThrow()
        val formatted = StandardCalculatorEngine.formatDisplay(neg)
        _uiState.value = _uiState.value.copy(
          expression = formatted,
          cursorPosition = formatted.length,
          resultPreview = formatted,
          runningSumBengali = StandardCalculatorEngine.toBengaliDigits(formatted),
          isCalculated = false
        )
      }
    }
  }

  fun onBackspace() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    if (pos == 0 || current.isEmpty()) return

    val before = current.substring(0, pos)
    val after = current.substring(pos)

    val (newBefore, newPos) = if (before.endsWith(" ") && before.length >= 3 && before[before.length - 2] in "+−×÷^") {
      Pair(before.dropLast(3), pos - 3)
    } else {
      Pair(before.dropLast(1), pos - 1)
    }

    val updated = newBefore + after
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length),
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onDeleteForward() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    if (pos >= current.length || current.isEmpty()) return

    val before = current.substring(0, pos)
    val after = current.substring(pos)

    val newAfter = if (after.startsWith(" ") && after.length >= 3 && after[1] in "+−×÷^") {
      after.drop(3)
    } else {
      after.drop(1)
    }

    val updated = before + newAfter
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = pos.coerceIn(0, updated.length),
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onClear() {
    _uiState.value = _uiState.value.copy(
      expression = "",
      cursorPosition = 0,
      resultPreview = "",
      runningSumBengali = "",
      amountInWords = "",
      stats = null,
      isCalculated = false,
      errorMessage = null
    )
  }

  fun onEquals() {
    val current = _uiState.value.expression
    if (current.isBlank()) return

    val evalRes = StandardCalculatorEngine.evaluateRunning(current)
    if (evalRes.isSuccess) {
      val resValue = evalRes.getOrThrow()
      val formattedEn = StandardCalculatorEngine.formatDisplay(resValue)
      val formattedBn = StandardCalculatorEngine.toBengaliDigits(formattedEn)
      val inWords = StandardCalculatorEngine.amountInWordsBengali(resValue)
      val stats = StandardCalculatorEngine.calculateStats(current)

      val updatedHistory = historyRepository.addCalculation(
        expression = current.trim(),
        result = formattedEn
      )

      _uiState.value = _uiState.value.copy(
        resultPreview = formattedEn,
        runningSumBengali = formattedBn,
        amountInWords = inWords,
        stats = stats,
        isCalculated = true,
        history = updatedHistory,
        errorMessage = null
      )
    } else {
      _uiState.value = _uiState.value.copy(
        errorMessage = evalRes.exceptionOrNull()?.message ?: "হিসাবে ভুল আছে"
      )
    }
  }

  fun useResultAsNewExpression() {
    val res = _uiState.value.resultPreview.replace(",", "")
    if (res.isBlank()) return
    _uiState.value = _uiState.value.copy(
      expression = res,
      cursorPosition = res.length,
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(res)
  }

  fun onSelectHistory(item: NormalCalcHistoryItem) {
    _uiState.value = _uiState.value.copy(
      expression = item.expression,
      cursorPosition = item.expression.length,
      resultPreview = item.result,
      runningSumBengali = StandardCalculatorEngine.toBengaliDigits(item.result),
      isFullScreenHistoryOpen = false,
      isCalculated = true,
      errorMessage = null
    )
    updatePreview(item.expression)
  }

  fun loadHistoryExpression(item: NormalCalcHistoryItem) {
    _uiState.value = _uiState.value.copy(
      expression = item.expression,
      cursorPosition = item.expression.length,
      isFullScreenHistoryOpen = false,
      isCalculated = false,
      errorMessage = null
    )
    updatePreview(item.expression)
  }

  fun loadHistoryResult(item: NormalCalcHistoryItem) {
    val clean = item.result.replace(",", "")
    _uiState.value = _uiState.value.copy(
      expression = clean,
      cursorPosition = clean.length,
      resultPreview = item.result,
      runningSumBengali = StandardCalculatorEngine.toBengaliDigits(item.result),
      isFullScreenHistoryOpen = false,
      isCalculated = false,
      errorMessage = null
    )
  }

  fun copyHistoryItem(item: NormalCalcHistoryItem, language: AppLanguage) {
    val textToCopy = "${item.expression} = ${item.result}"
    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Calculation History", textToCopy)
    clipboard.setPrimaryClip(clip)

    val msg = if (language == AppLanguage.BENGALI) "হিসাবটি ক্লিপবোর্ডে কপি করা হয়েছে!" else "Calculation copied to clipboard!"
    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
  }

  fun deleteHistoryItem(item: NormalCalcHistoryItem) {
    val updated = historyRepository.deleteCalculation(item.timestamp)
    _uiState.value = _uiState.value.copy(history = updated)
  }

  fun copyText(text: String, toastMessage: String) {
    if (text.isBlank()) return
    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Calculator Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(getApplication(), toastMessage, Toast.LENGTH_SHORT).show()
  }

  fun copyReceiptText(language: AppLanguage) {
    val state = _uiState.value
    val isBn = language == AppLanguage.BENGALI
    val tokens = state.numberTokens
    if (tokens.isEmpty()) return

    val sb = StringBuilder()
    sb.append(if (isBn) "🧾 বাংলা ক্যালকুলেটর হিসাবের রসিদ:\n" else "🧾 Calculation Receipt:\n")
    sb.append("-----------------------------\n")

    var runningTotal = 0.0
    for ((index, token) in tokens.withIndex()) {
      val num = StandardCalculatorEngine.toEnglishDigits(token.value).toDoubleOrNull() ?: 0.0
      runningTotal += num
      val itemNumStr = if (isBn) StandardCalculatorEngine.toBengaliDigits("${index + 1}") else "${index + 1}"
      val valStr = if (isBn) StandardCalculatorEngine.toBengaliDigits(token.value) else token.value
      val runStr = if (isBn) StandardCalculatorEngine.toBengaliDigits(StandardCalculatorEngine.formatDisplay(runningTotal)) else StandardCalculatorEngine.formatDisplay(runningTotal)
      sb.append("$itemNumStr. $valStr  (চলতি যোগফল: $runStr)\n")
    }

    sb.append("-----------------------------\n")
    val totalCountStr = if (isBn) "${StandardCalculatorEngine.toBengaliDigits("${tokens.size}")} টি" else "${tokens.size}"
    val sumStr = if (isBn) state.runningSumBengali else state.resultPreview
    sb.append(if (isBn) "মোট সংখ্যা: $totalCountStr\n" else "Total Items: $totalCountStr\n")
    sb.append(if (isBn) "মোট যোগফল: $sumStr (${state.resultPreview})\n" else "Total Sum: ${state.resultPreview}\n")
    if (state.amountInWords.isNotBlank()) {
      sb.append(if (isBn) "কথায়: ${state.amountInWords}\n" else "In Words: ${state.amountInWords}\n")
    }

    copyText(sb.toString(), if (isBn) "রসিদ ক্লিপবোর্ডে কপি করা হয়েছে!" else "Receipt copied to clipboard!")
  }

  fun loadSample50NumbersCalculation(addToCalculatorToo: Boolean = false) {
    val sampleNumbers = listOf(
      150L, 230L, 85L, 420L, 95L, 310L, 500L, 120L, 650L, 275L,
      80L, 190L, 340L, 210L, 950L, 160L, 75L, 430L, 320L, 580L,
      115L, 240L, 360L, 490L, 70L, 820L, 130L, 290L, 450L, 600L,
      175L, 380L, 520L, 90L, 260L, 710L, 840L, 140L, 330L, 470L,
      225L, 590L, 185L, 315L, 465L, 625L, 780L, 890L, 105L, 960L
    )
    val expr = sampleNumbers.joinToString(" + ")
    val totalSum = sampleNumbers.sum()
    val formattedSum = StandardCalculatorEngine.formatDisplay(totalSum.toDouble())

    val sampleItem = NormalCalcHistoryItem(
      id = System.currentTimeMillis(),
      expression = expr,
      result = formattedSum,
      timestamp = System.currentTimeMillis()
    )

    val updatedHistory = historyRepository.saveAll(
      listOf(sampleItem) + _uiState.value.history.filterNot { it.expression == expr }
    )
    _uiState.value = _uiState.value.copy(
      history = updatedHistory,
      expression = if (addToCalculatorToo) expr else _uiState.value.expression,
      cursorPosition = if (addToCalculatorToo) expr.length else _uiState.value.cursorPosition,
      resultPreview = if (addToCalculatorToo) formattedSum else _uiState.value.resultPreview,
      runningSumBengali = if (addToCalculatorToo) StandardCalculatorEngine.toBengaliDigits(formattedSum) else _uiState.value.runningSumBengali,
      errorMessage = null
    )
  }

  fun loadSampleSixMonthHistory() {
    val samples = historyRepository.generateSampleSixMonthsData()
    _uiState.value = _uiState.value.copy(
      history = samples,
      selectedFilter = HistoryFilterPeriod.ALL,
      errorMessage = null
    )
  }

  fun onClearHistory() {
    val cleared = historyRepository.clearAll()
    _uiState.value = _uiState.value.copy(history = cleared)
  }

  fun toggleScientific() {
    _uiState.value = _uiState.value.copy(isScientific = !_uiState.value.isScientific)
  }

  fun copyToClipboard(language: AppLanguage) {
    val state = _uiState.value
    val textToCopy = when {
      state.expression.isNotBlank() && state.resultPreview.isNotBlank() ->
        "${state.expression} = ${state.resultPreview}"
      state.expression.isNotBlank() -> state.expression
      else -> state.resultPreview
    }
    if (textToCopy.isBlank()) return

    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Calculator Result", textToCopy)
    clipboard.setPrimaryClip(clip)

    val msg = if (language == AppLanguage.BENGALI) "হিসাব ও যোগফল কপি করা হয়েছে!" else "Calculation & Sum copied!"
    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
  }

  private fun updatePreview(expr: String) {
    if (expr.isBlank()) {
      _uiState.value = _uiState.value.copy(
        resultPreview = "",
        runningSumBengali = "",
        amountInWords = "",
        stats = null
      )
      return
    }

    val res = StandardCalculatorEngine.evaluateRunning(expr)
    if (res.isSuccess) {
      val value = res.getOrThrow()
      val formattedEn = StandardCalculatorEngine.formatDisplay(value)
      val formattedBn = StandardCalculatorEngine.toBengaliDigits(formattedEn)
      val inWords = StandardCalculatorEngine.amountInWordsBengali(value)
      val stats = StandardCalculatorEngine.calculateStats(expr)

      _uiState.value = _uiState.value.copy(
        resultPreview = formattedEn,
        runningSumBengali = formattedBn,
        amountInWords = inWords,
        stats = stats,
        errorMessage = null
      )
    } else {
      val stats = StandardCalculatorEngine.calculateStats(expr)
      if (stats != null && stats.count > 0) {
        val formattedEn = StandardCalculatorEngine.formatDisplay(stats.sum)
        val formattedBn = StandardCalculatorEngine.toBengaliDigits(formattedEn)
        _uiState.value = _uiState.value.copy(
          resultPreview = formattedEn,
          runningSumBengali = formattedBn,
          amountInWords = StandardCalculatorEngine.amountInWordsBengali(stats.sum),
          stats = stats
        )
      } else {
        _uiState.value = _uiState.value.copy(
          resultPreview = "",
          runningSumBengali = "",
          amountInWords = "",
          stats = null
        )
      }
    }
  }
}
