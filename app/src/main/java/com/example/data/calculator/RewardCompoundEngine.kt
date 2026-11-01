package com.example.data.calculator

import com.example.data.model.AppLanguage
import com.example.data.model.CalculationInput
import com.example.data.model.CalculationResult
import com.example.data.model.DailyRecord
import java.text.NumberFormat
import java.util.Locale

object RewardCompoundEngine {

  fun calculate(input: CalculationInput): CalculationResult {
    val p = input.principal.coerceAtLeast(0.0)
    val rate = input.dailyRatePercent.coerceAtLeast(0.0)
    val days = input.days.coerceIn(1, 10000)
    val compound = input.isCompound

    var bal = p
    var simpleBal = p
    var totalReward = 0.0
    var simpleTotalReward = 0.0
    val records = ArrayList<DailyRecord>(days)

    for (d in 1..days) {
      val opening = bal
      // Daily reward calculation
      val reward = if (compound) (bal * rate / 100.0) else (p * rate / 100.0)
      totalReward += reward
      if (compound) {
        bal += reward
      }
      bal += input.dailyAddition

      // Simple calculation for comparison
      val simpleDaily = p * rate / 100.0
      simpleTotalReward += simpleDaily
      simpleBal = p + simpleTotalReward + (input.dailyAddition * d)

      // Guard against infinite/NaN
      if (!bal.isFinite() || bal > 1e100) {
        bal = Double.MAX_VALUE
      }

      records.add(
        DailyRecord(
          day = d,
          opening = opening,
          reward = reward,
          closing = bal,
          cumulativeReward = totalReward,
          simpleComparisonClosing = simpleBal
        )
      )
    }

    val totalReturnPercent = if (p > 0) (totalReward / p) * 100.0 else 0.0
    val simpleProfit = (p * rate / 100.0) * days
    val compoundDifference = (totalReward - simpleProfit).coerceAtLeast(0.0)
    val dailyAverageReward = if (days > 0) totalReward / days else 0.0

    return CalculationResult(
      input = input,
      finalBalance = bal,
      totalProfit = totalReward,
      initialPrincipal = p,
      totalReturnPercent = totalReturnPercent,
      simpleProfit = simpleProfit,
      compoundDifference = compoundDifference,
      dailyAverageReward = dailyAverageReward,
      dailyRecords = records
    )
  }

  fun formatMoney(amount: Double, currency: String): String {
    if (!amount.isFinite()) return "সীমার বাইরে"
    val nf = NumberFormat.getNumberInstance(Locale.US).apply {
      minimumFractionDigits = 2
      maximumFractionDigits = 2
    }
    val formatted = nf.format(amount)
    return if (currency.isBlank()) formatted else "$currency $formatted"
  }

  fun formatPercent(percent: Double): String {
    if (!percent.isFinite()) return "—"
    val nf = NumberFormat.getNumberInstance(Locale.US).apply {
      minimumFractionDigits = 2
      maximumFractionDigits = 2
    }
    return "${nf.format(percent)}%"
  }

  fun generateCsv(result: CalculationResult, language: AppLanguage): String {
    val sb = StringBuilder()
    // UTF-8 BOM
    sb.append('\uFEFF')

    val isBn = language == AppLanguage.BENGALI
    val header = if (isBn) {
      "দিন,শুরুর ব্যালান্স,রিওয়ার্ড,শেষের ব্যালান্স,মোট সঞ্চিত রিওয়ার্ড\n"
    } else {
      "Day,Opening Balance,Reward,Closing Balance,Cumulative Reward\n"
    }
    sb.append(header)

    val nf = NumberFormat.getNumberInstance(Locale.US).apply {
      minimumFractionDigits = 2
      maximumFractionDigits = 2
      isGroupingUsed = false
    }

    result.dailyRecords.forEach { r ->
      sb.append(r.day).append(',')
        .append(nf.format(r.opening)).append(',')
        .append(nf.format(r.reward)).append(',')
        .append(nf.format(r.closing)).append(',')
        .append(nf.format(r.cumulativeReward)).append('\n')
    }

    return sb.toString()
  }

  fun generateSummaryText(result: CalculationResult, language: AppLanguage): String {
    val curr = result.input.currency
    val isBn = language == AppLanguage.BENGALI
    return if (isBn) {
      """
📊 রিওয়ার্ড কম্পাউন্ড হিসাব বিবরণী:
• প্রাথমিক মূলধন: ${formatMoney(result.initialPrincipal, curr)}
• দৈনিক রিওয়ার্ড হার: ${result.input.dailyRatePercent}%
• মোট সময়কাল: ${result.input.days} দিন
• পদ্ধতি: ${if (result.input.isCompound) "প্রতিদিনের কম্পাউন্ড (Compound)" else "কম্পাউন্ড ছাড়া (Simple)"}
• শেষের মোট ব্যালান্স: ${formatMoney(result.finalBalance, curr)}
• মোট অর্জিত লাভ: ${formatMoney(result.totalProfit, curr)}
• মোট রিটার্ন: ${formatPercent(result.totalReturnPercent)}
${if (result.input.isCompound && result.compoundDifference > 0.01) "• কম্পাউন্ডিংয়ের অতিরিক্ত সুবিধা: +${formatMoney(result.compoundDifference, curr)}" else ""}

হিসাবটি Reward Compound Calculator অ্যাপ দ্বারা প্রস্তুত।
      """.trimIndent()
    } else {
      """
📊 Reward Compound Summary:
• Initial Principal: ${formatMoney(result.initialPrincipal, curr)}
• Daily Reward Rate: ${result.input.dailyRatePercent}%
• Duration: ${result.input.days} Days
• Mode: ${if (result.input.isCompound) "Daily Compound" else "Simple Reward (No Compounding)"}
• Final Balance: ${formatMoney(result.finalBalance, curr)}
• Total Profit: ${formatMoney(result.totalProfit, curr)}
• Total Return: ${formatPercent(result.totalReturnPercent)}
${if (result.input.isCompound && result.compoundDifference > 0.01) "• Extra Compound Advantage: +${formatMoney(result.compoundDifference, curr)}" else ""}

Generated by Reward Compound Calculator app.
      """.trimIndent()
    }
  }
}
