package com.example.data.calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

data class ExpressionStats(
  val count: Int,
  val sum: Double,
  val average: Double,
  val min: Double,
  val max: Double,
  val numbers: List<Double>
)

object StandardCalculatorEngine {

  fun toBengaliDigits(input: String): String {
    val bnDigits = "০১২৩৪৫৬৭৮৯"
    val sb = StringBuilder()
    for (ch in input) {
      if (ch in '0'..'9') {
        sb.append(bnDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  fun toEnglishDigits(input: String): String {
    val bnDigits = "০১২৩৪৫৬৭৮৯"
    val sb = StringBuilder()
    for (ch in input) {
      val idx = bnDigits.indexOf(ch)
      if (idx >= 0) {
        sb.append(('0' + idx))
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  fun formatDisplay(value: Double): String {
    if (!value.isFinite()) return "Error"
    if (value == 0.0) return "0"

    // If it's an integer
    if (value % 1.0 == 0.0 && value in -1e12..1e12) {
      val symbols = DecimalFormatSymbols(Locale.US)
      val df = DecimalFormat("#,###", symbols)
      return df.format(value.toLong())
    }

    // Floating point format
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0.########", symbols)
    return df.format(value)
  }

  fun evaluate(expression: String): Result<Double> {
    return try {
      val clean = cleanExpression(expression)
      if (clean.isBlank()) return Result.success(0.0)
      val tokens = tokenize(clean)
      val rpn = toRpn(tokens)
      val res = evaluateRpn(rpn)
      if (res.isInfinite() || res.isNaN()) {
        Result.failure(ArithmeticException("Cannot divide by zero"))
      } else {
        Result.success(res)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Resilient running evaluation: strips trailing operators or unfinished parts
   * so the user always sees the live running sum while typing long additions or formulas.
   */
  fun evaluateRunning(expression: String): Result<Double> {
    if (expression.isBlank()) return Result.success(0.0)

    var s = cleanExpression(expression)
    if (s.isBlank()) return Result.success(0.0)

    // Remove any trailing whitespace and dangling binary/unary operators (e.g. "150 + 250 + ")
    val trailingOpChars = setOf('+', '-', '*', '/', '^', '(', '√', ' ')
    while (s.isNotEmpty() && s.last() in trailingOpChars) {
      s = s.dropLast(1).trimEnd()
    }

    if (s.isBlank()) return Result.success(0.0)

    // Balance unclosed opening parentheses
    val openCount = s.count { it == '(' }
    val closeCount = s.count { it == ')' }
    if (openCount > closeCount) {
      s = s + ")".repeat(openCount - closeCount)
    }

    val directEval = evaluate(s)
    if (directEval.isSuccess) {
      return directEval
    }

    // Fallback: If direct evaluation fails due to mid-formula editing, try evaluating all valid numbers
    // if the expression is primarily a sum
    val numbers = extractNumbers(expression)
    if (numbers.isNotEmpty()) {
      val sum = numbers.sum()
      return Result.success(sum)
    }

    return directEval
  }

  fun extractNumbers(expr: String): List<Double> {
    if (expr.isBlank()) return emptyList()
    val clean = toEnglishDigits(expr).replace(",", "")
    val regex = Regex("""[0-9]+(?:\.[0-9]+)?""")
    return regex.findAll(clean).mapNotNull { it.value.toDoubleOrNull() }.toList()
  }

  fun calculateStats(expr: String): ExpressionStats? {
    val numbers = extractNumbers(expr)
    if (numbers.isEmpty()) return null
    val count = numbers.size
    val sum = numbers.sum()
    val avg = sum / count
    val min = numbers.minOrNull() ?: 0.0
    val max = numbers.maxOrNull() ?: 0.0
    return ExpressionStats(
      count = count,
      sum = sum,
      average = avg,
      min = min,
      max = max,
      numbers = numbers
    )
  }

  private fun cleanExpression(expr: String): String {
    var s = toEnglishDigits(expr)
      .replace(",", "") // Strip thousand separator commas
      .replace("×", "*")
      .replace("÷", "/")
      .replace("−", "-")
      .replace("π", Math.PI.toString())
      .replace("e", Math.E.toString())

    return s.trim()
  }

  private fun tokenize(expr: String): List<String> {
    val tokens = mutableListOf<String>()
    var i = 0
    val n = expr.length

    while (i < n) {
      val c = expr[i]
      when {
        c.isWhitespace() -> i++
        c.isDigit() || c == '.' -> {
          if (tokens.isNotEmpty() && (tokens.last() == "%" || tokens.last() == ")")) {
            tokens.add("*")
          }
          val sb = StringBuilder()
          while (i < n && (expr[i].isDigit() || expr[i] == '.')) {
            sb.append(expr[i])
            i++
          }
          tokens.add(sb.toString())
        }
        c == '(' -> {
          if (tokens.isNotEmpty() && (tokens.last().toDoubleOrNull() != null || tokens.last() == ")" || tokens.last() == "%")) {
            tokens.add("*")
          }
          tokens.add("(")
          i++
        }
        c == '%' -> {
          tokens.add("%")
          i++
        }
        c in "+-*/^" || c == '√' || c == ')' -> {
          if (c == '-' && (tokens.isEmpty() || tokens.last() in "+-*/^(" || tokens.last() == "√")) {
            val sb = StringBuilder("-")
            i++
            while (i < n && (expr[i].isDigit() || expr[i] == '.')) {
              sb.append(expr[i])
              i++
            }
            if (sb.length > 1) {
              tokens.add(sb.toString())
            } else {
              tokens.add("-1")
              tokens.add("*")
            }
          } else {
            tokens.add(c.toString())
            i++
          }
        }
        else -> i++
      }
    }
    return tokens
  }

  private fun precedence(op: String): Int {
    return when (op) {
      "+", "-" -> 1
      "*", "/" -> 2
      "^" -> 3
      "√" -> 4
      else -> 0
    }
  }

  private fun toRpn(tokens: List<String>): List<String> {
    val output = mutableListOf<String>()
    val ops = Stack<String>()

    for (token in tokens) {
      val num = token.toDoubleOrNull()
      if (num != null) {
        output.add(token)
      } else if (token == "%") {
        output.add("%")
      } else if (token == "(") {
        ops.push(token)
      } else if (token == ")") {
        while (ops.isNotEmpty() && ops.peek() != "(") {
          output.add(ops.pop())
        }
        if (ops.isNotEmpty() && ops.peek() == "(") {
          ops.pop()
        }
      } else {
        while (ops.isNotEmpty() && ops.peek() != "(" &&
          (precedence(ops.peek()) > precedence(token) ||
            (precedence(ops.peek()) == precedence(token) && token != "^" && token != "√"))
        ) {
          output.add(ops.pop())
        }
        ops.push(token)
      }
    }

    while (ops.isNotEmpty()) {
      output.add(ops.pop())
    }

    return output
  }

  private fun evaluateRpn(rpn: List<String>): Double {
    val stack = Stack<Double>()

    for (index in rpn.indices) {
      val token = rpn[index]
      val num = token.toDoubleOrNull()
      if (num != null) {
        stack.push(num)
      } else if (token == "√") {
        if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for √")
        val a = stack.pop()
        stack.push(sqrt(a))
      } else if (token == "%") {
        if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for %")
        val b = stack.pop()
        val nextOp = rpn.getOrNull(index + 1)
        if ((nextOp == "+" || nextOp == "-") && stack.isNotEmpty()) {
          val base = stack.peek()
          stack.push(base * (b / 100.0))
        } else {
          stack.push(b / 100.0)
        }
      } else {
        if (stack.size < 2) throw IllegalArgumentException("Invalid syntax")
        val b = stack.pop()
        val a = stack.pop()
        val res = when (token) {
          "+" -> a + b
          "-" -> a - b
          "*" -> a * b
          "/" -> if (b == 0.0) throw ArithmeticException("Division by zero") else a / b
          "^" -> a.pow(b)
          else -> throw IllegalArgumentException("Unknown operator: $token")
        }
        stack.push(res)
      }
    }

    if (stack.isEmpty()) return 0.0
    return stack.pop()
  }

  // --- Numbers to Words in Bengali and English ---

  private val bnUnits = arrayOf(
    "", "এক", "দুই", "তিন", "চার", "পাঁচ", "ছয়", "সাত", "আট", "নয়",
    "দশ", "এগারো", "বারো", "তেরো", "চৌদ্দ", "পনেরো", "ষোল", "সতেরো", "আঠারো", "উনিশ",
    "বিশ", "একুশ", "বাইশ", "তেইশ", "চব্বিশ", "পঁচিশ", "ছাব্বিশ", "সাতাশ", "আঠাশ", "ঊনত্রিশ",
    "ত্রিশ", "একত্রিশ", "বত্রিশ", "তেত্রিশ", "চৌত্রিশ", "পঁয়ত্রিশ", "ছত্রিশ", "সাঁইত্রিশ", "আটত্রিশ", "ঊনচল্লিশ",
    "চল্লিশ", "একচল্লিশ", "বিয়াল্লিশ", "তেতাল্লিশ", "চুয়াল্লিশ", "পঁয়তাল্লিশ", "ছেচল্লিশ", "সাতচল্লিশ", "আটচল্লিশ", "ঊনপঞ্চাশ",
    "পঞ্চাশ", "একান্ন", "বায়ান্ন", "তিপ্পান্ন", "চুয়ান্ন", "পঞ্চান্ন", "ছাপ্পান্ন", "সাতান্ন", "আটান্ন", "ঊনষাট",
    "ষাট", "একষট্টি", "বাষট্টি", "তেষট্টি", "চৌষট্টি", "পঁয়ষট্টি", "ছেষট্টি", "সাতষট্টি", "আটষট্টি", "ঊনসত্তর",
    "সত্তর", "একাত্তর", "বাহাত্তর", "তিয়াত্তর", "চুয়াত্তর", "পঁচাত্তর", "ছিয়াত্তর", "সাতাত্তর", "আটাত্তর", "ঊনআশি",
    "আশি", "একাশি", "বিরাশি", "তিরাশি", "চুরাশি", "পঁচাশি", "ছিয়াশি", "সাতাশি", "অষ্টআশি", "ঊননব্বই",
    "নব্বই", "একানব্বই", "বানব্বই", "তিরানব্বই", "চুরানব্বই", "পঁচানব্বই", "ছিয়ানব্বই", "সাতানব্বই", "আটানব্বই", "নিরানব্বই"
  )

  fun amountInWordsBengali(amount: Double): String {
    if (!amount.isFinite() || amount < 0) return ""
    val wholePart = amount.roundToLong()
    if (wholePart == 0L) return "শূন্য টাকা"

    val sb = StringBuilder()
    var n = wholePart

    val crore = n / 10000000L
    n %= 10000000L

    val lakh = n / 100000L
    n %= 100000L

    val thousand = n / 1000L
    n %= 1000L

    val hundred = n / 100L
    val remainder = (n % 100L).toInt()

    if (crore > 0) {
      sb.append(amountInWordsBengali(crore.toDouble()).replace(" টাকা", "")).append(" কোটি ")
    }
    if (lakh > 0) {
      val lk = lakh.toInt()
      sb.append(bnUnits.getOrElse(lk) { "$lk" }).append(" লক্ষ ")
    }
    if (thousand > 0) {
      val th = thousand.toInt()
      sb.append(bnUnits.getOrElse(th) { "$th" }).append(" হাজার ")
    }
    if (hundred > 0) {
      val h = hundred.toInt()
      sb.append(bnUnits.getOrElse(h) { "$h" }).append(" শত ")
    }
    if (remainder > 0) {
      sb.append(bnUnits.getOrElse(remainder) { "$remainder" }).append(" ")
    }

    val result = sb.toString().trim()
    return if (result.isNotEmpty()) "$result টাকা" else "শূন্য টাকা"
  }

  fun amountInWords(amount: Double, isBengali: Boolean): String {
    return if (isBengali) amountInWordsBengali(amount) else amountInWordsEnglish(amount)
  }

  private fun amountInWordsEnglish(amount: Double): String {
    if (!amount.isFinite() || amount < 0) return ""
    val n = amount.roundToLong()
    if (n == 0L) return "Zero Taka"
    return "${convertNumberToEnglishWords(n)} Taka"
  }

  private fun convertNumberToEnglishWords(n: Long): String {
    val units = arrayOf(
      "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
      "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
      "Seventeen", "Eighteen", "Nineteen"
    )
    val tens = arrayOf(
      "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    if (n == 0L) return ""
    if (n < 20) return units[n.toInt()]
    if (n < 100) return tens[(n / 10).toInt()] + (if (n % 10 != 0L) " " + units[(n % 10).toInt()] else "")
    if (n < 1000) return units[(n / 100).toInt()] + " Hundred" + (if (n % 100 != 0L) " " + convertNumberToEnglishWords(n % 100) else "")
    if (n < 1000000) return convertNumberToEnglishWords(n / 1000) + " Thousand" + (if (n % 1000 != 0L) " " + convertNumberToEnglishWords(n % 1000) else "")
    if (n < 1000000000) return convertNumberToEnglishWords(n / 1000000) + " Million" + (if (n % 1000000 != 0L) " " + convertNumberToEnglishWords(n % 1000000) else "")
    return convertNumberToEnglishWords(n / 1000000000) + " Billion" + (if (n % 1000000000 != 0L) " " + convertNumberToEnglishWords(n % 1000000000) else "")
  }
}
