package com.example.ui.normalcalc

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
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
  val resultPreview: String = "",
  val history: List<NormalCalcHistoryItem> = emptyList(),
  val selectedFilter: HistoryFilterPeriod = HistoryFilterPeriod.ALL,
  val historySearchQuery: String = "",
  val isScientific: Boolean = false,
  val isFullScreenHistoryOpen: Boolean = false,
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
      val regex = Regex("""[0-9]+(?:\.[0-9]+)?""")
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
    val cleanNew = newValue.trim().filter { it.isDigit() || it == '.' }
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
      // Re-scan matching token by ID
      val tokens = NormalCalcUiState.parseNumberTokens(current)
      val matching = tokens.find { it.id == token.id }
      if (matching != null) {
        updated = current.substring(0, matching.startIndex) + cleanNew + current.substring(matching.endIndex)
        newCursor = matching.startIndex + cleanNew.length
      } else {
        // Fallback: replace first occurrence
        updated = current.replaceFirst(token.value, cleanNew)
        newCursor = updated.length
      }
    }

    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newCursor.coerceIn(0, updated.length),
      editingToken = null,
      errorMessage = null
    )
    updatePreview(updated)
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
      errorMessage = null
    )
    updatePreview(newExpr)
  }

  // --- Keypad Actions (Cursor-Aware) ---

  fun onDigit(d: String) {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)

    val before = current.substring(0, pos)
    val after = current.substring(pos)

    // Prevent multiple decimals in the same number chunk
    if (d == ".") {
      val leftDigits = before.takeLastWhile { it.isDigit() || it == '.' }
      val rightDigits = after.takeWhile { it.isDigit() || it == '.' }
      if (leftDigits.contains('.') || rightDigits.contains('.')) return
    }

    val updated = before + d + after
    val newPos = pos + d.length
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos,
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onOperator(op: String) {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)

    if (current.isBlank()) {
      if (op == "−") {
        _uiState.value = _uiState.value.copy(expression = "−", cursorPosition = 1)
      }
      return
    }

    val before = current.substring(0, pos)
    val after = current.substring(pos)

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
    } else if (openCount > closeCount && (before.last().isDigit() || before.last() == ')')) {
      ")"
    } else {
      " × ("
    }

    val updated = before + insert + after
    val newPos = pos + insert.length
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length)
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
      cursorPosition = newPos.coerceIn(0, updated.length)
    )
    updatePreview(updated)
  }

  fun onSquare() {
    val current = _uiState.value.expression
    val pos = _uiState.value.cursorPosition.coerceIn(0, current.length)
    val before = current.substring(0, pos)
    val after = current.substring(pos)

    if (before.isNotBlank() && (before.last().isDigit() || before.last() == ')')) {
      val insert = " ^ 2"
      val updated = before + insert + after
      val newPos = pos + insert.length
      _uiState.value = _uiState.value.copy(
        expression = updated,
        cursorPosition = newPos.coerceIn(0, updated.length)
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
      if (trimmed.last().isDigit() || trimmed.last() == ')' || trimmed.last() == '.') {
        val insert = "%"
        val updated = trimmed + insert + after
        val newPos = trimmed.length + insert.length
        _uiState.value = _uiState.value.copy(
          expression = updated,
          cursorPosition = newPos.coerceIn(0, updated.length),
          errorMessage = null
        )
        updatePreview(updated)
      }
    }
  }

  fun onNegate() {
    val current = _uiState.value.expression
    if (current.isNotBlank()) {
      val evalRes = StandardCalculatorEngine.evaluate(current)
      if (evalRes.isSuccess) {
        val neg = -evalRes.getOrThrow()
        val formatted = StandardCalculatorEngine.formatDisplay(neg)
        _uiState.value = _uiState.value.copy(
          expression = formatted,
          cursorPosition = formatted.length,
          resultPreview = ""
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

    // Delete spaces and operators together if adjacent (e.g. " + ")
    val (newBefore, newPos) = if (before.endsWith(" ") && before.length >= 3 && before[before.length - 2] in "+−×÷^") {
      Pair(before.dropLast(3), pos - 3)
    } else {
      Pair(before.dropLast(1), pos - 1)
    }

    val updated = newBefore + after
    _uiState.value = _uiState.value.copy(
      expression = updated,
      cursorPosition = newPos.coerceIn(0, updated.length),
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
      errorMessage = null
    )
    updatePreview(updated)
  }

  fun onClear() {
    _uiState.value = _uiState.value.copy(
      expression = "",
      cursorPosition = 0,
      resultPreview = "",
      errorMessage = null
    )
  }

  fun onEquals() {
    val current = _uiState.value.expression
    if (current.isBlank()) return

    val evalRes = StandardCalculatorEngine.evaluate(current)
    if (evalRes.isSuccess) {
      val resValue = evalRes.getOrThrow()
      val formatted = StandardCalculatorEngine.formatDisplay(resValue)
      val updatedHistory = historyRepository.addCalculation(expression = current, result = formatted)

      _uiState.value = _uiState.value.copy(
        expression = formatted,
        cursorPosition = formatted.length,
        resultPreview = "",
        history = updatedHistory,
        errorMessage = null
      )
    } else {
      _uiState.value = _uiState.value.copy(
        errorMessage = evalRes.exceptionOrNull()?.message ?: "Error"
      )
    }
  }

  fun onSelectHistory(item: NormalCalcHistoryItem) {
    _uiState.value = _uiState.value.copy(
      expression = item.expression,
      cursorPosition = item.expression.length,
      resultPreview = item.result,
      isFullScreenHistoryOpen = false,
      errorMessage = null
    )
  }

  fun loadHistoryExpression(item: NormalCalcHistoryItem) {
    _uiState.value = _uiState.value.copy(
      expression = item.expression,
      cursorPosition = item.expression.length,
      resultPreview = item.result,
      isFullScreenHistoryOpen = false,
      errorMessage = null
    )
  }

  fun loadHistoryResult(item: NormalCalcHistoryItem) {
    _uiState.value = _uiState.value.copy(
      expression = item.result,
      cursorPosition = item.result.length,
      resultPreview = "",
      isFullScreenHistoryOpen = false,
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
    val textToCopy = _uiState.value.expression.ifBlank { _uiState.value.resultPreview }
    if (textToCopy.isBlank()) return

    val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Calculator Result", textToCopy)
    clipboard.setPrimaryClip(clip)

    val msg = if (language == AppLanguage.BENGALI) "ক্লিপবোর্ডে কপি করা হয়েছে!" else "Copied to clipboard!"
    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
  }

  private fun updatePreview(expr: String) {
    if (expr.isBlank() || expr.last() in "+−×÷^( ") {
      _uiState.value = _uiState.value.copy(resultPreview = "")
      return
    }
    val res = StandardCalculatorEngine.evaluate(expr)
    if (res.isSuccess) {
      _uiState.value = _uiState.value.copy(
        resultPreview = StandardCalculatorEngine.formatDisplay(res.getOrThrow())
      )
    } else {
      _uiState.value = _uiState.value.copy(resultPreview = "")
    }
  }
}
