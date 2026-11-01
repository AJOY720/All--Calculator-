package com.example.data.calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

object StandardCalculatorEngine {

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

  private fun cleanExpression(expr: String): String {
    var s = expr
      .replace(",", "") // Strip thousand separator commas so "3,000" becomes "3000"
      .replace("×", "*")
      .replace("÷", "/")
      .replace("−", "-")
      .replace("π", Math.PI.toString())
      .replace("e", Math.E.toString())

    val bnDigits = "০১২৩৪৫৬৭৮৯"
    for (i in 0..9) {
      s = s.replace(bnDigits[i], ('0' + i))
    }
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
          // If previous token was "%" or ")", insert implicit multiplication
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
          // If previous token was a number, ")", or "%", insert implicit multiplication
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
          // Check for unary minus: at start or after another operator / '('
          if (c == '-' && (tokens.isEmpty() || tokens.last() in "+-*/^(" || tokens.last() == "√")) {
            // Unary minus: parse with number or treat as negation
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
        // Unary postfix percentage operator: directly applies to preceding output value
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
        // Binary or prefix operator
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
        // Percentage operator
        if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for %")
        val b = stack.pop()
        // Check if next operation in RPN is + or - with an existing base value on stack
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
}
