package com.example.ui.util

import com.example.data.model.AppLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object HistoryDateFormatter {

  private val bnDigits = "০১২৩৪৫৬৭৮৯"

  fun toBengaliDigits(input: String): String {
    var out = input
    for (i in 0..9) {
      out = out.replace(('0' + i).toString(), bnDigits[i].toString())
    }
    return out
  }

  /**
   * Formats timestamp into readable date and time, localized for Bengali or English.
   * e.g. "১১ অক্টো ২০২৬, ০৫:৩০ PM" or "11 Oct 2026, 05:30 PM"
   */
  fun formatDateTime(timestamp: Long, language: AppLanguage): String {
    val date = Date(timestamp)
    val cal = Calendar.getInstance().apply { time = date }
    val now = Calendar.getInstance()

    val isToday = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
      cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

    now.add(Calendar.DAY_OF_YEAR, -1)
    val isYesterday = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
      cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

    val timeSdf = SimpleDateFormat("hh:mm a", Locale.US)
    val timeStr = timeSdf.format(date)

    return when (language) {
      AppLanguage.BENGALI -> {
        val bnTime = toBengaliDigits(timeStr)
        if (isToday) {
          "আজ, $bnTime"
        } else if (isYesterday) {
          "গতকাল, $bnTime"
        } else {
          val day = cal.get(Calendar.DAY_OF_MONTH)
          val month = getBengaliMonthShort(cal.get(Calendar.MONTH))
          val year = cal.get(Calendar.YEAR)
          "${toBengaliDigits(day.toString())} $month ${toBengaliDigits(year.toString())}, $bnTime"
        }
      }
      AppLanguage.ENGLISH -> {
        if (isToday) {
          "Today, $timeStr"
        } else if (isYesterday) {
          "Yesterday, $timeStr"
        } else {
          val dateSdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
          "${dateSdf.format(date)}, $timeStr"
        }
      }
    }
  }

  /**
   * Returns relative label like "আজ" / "গতকাল" / "৫ দিন আগে" / "২ মাস আগে".
   */
  fun getRelativeTimeSpan(timestamp: Long, language: AppLanguage): String {
    val now = System.currentTimeMillis()
    val diff = (now - timestamp).coerceAtLeast(0L)
    val oneMinute = 60 * 1000L
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour
    val oneMonth = 30 * oneDay

    return when (language) {
      AppLanguage.BENGALI -> when {
        diff < 5 * oneMinute -> "এইমাত্র"
        diff < oneHour -> "${toBengaliDigits((diff / oneMinute).toString())} মিনিট আগে"
        diff < oneDay -> {
          val cal = Calendar.getInstance().apply { time = Date(timestamp) }
          val nowCal = Calendar.getInstance()
          if (cal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)) "আজ" else "গতকাল"
        }
        diff < 2 * oneDay -> "গতকাল"
        diff < 7 * oneDay -> "${toBengaliDigits((diff / oneDay).toString())} দিন আগে"
        diff < 30 * oneDay -> "${toBengaliDigits((diff / (7 * oneDay)).toString())} সপ্তাহ আগে"
        diff < 6 * oneMonth -> "${toBengaliDigits((diff / oneMonth).toString())} মাস আগে"
        else -> "৬ মাস আগে"
      }
      AppLanguage.ENGLISH -> when {
        diff < 5 * oneMinute -> "Just now"
        diff < oneHour -> "${diff / oneMinute}m ago"
        diff < oneDay -> {
          val cal = Calendar.getInstance().apply { time = Date(timestamp) }
          val nowCal = Calendar.getInstance()
          if (cal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)) "Today" else "Yesterday"
        }
        diff < 2 * oneDay -> "Yesterday"
        diff < 7 * oneDay -> "${diff / oneDay} days ago"
        diff < 30 * oneDay -> "${diff / (7 * oneDay)} weeks ago"
        diff < 6 * oneMonth -> "${diff / oneMonth} months ago"
        else -> "6 months ago"
      }
    }
  }

  /**
   * Returns a month-year grouping string, e.g. "অক্টোবর ২০২৬" / "October 2026".
   */
  fun getMonthYearGroup(timestamp: Long, language: AppLanguage): String {
    val cal = Calendar.getInstance().apply { time = Date(timestamp) }
    val month = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)

    return when (language) {
      AppLanguage.BENGALI -> "${getBengaliMonthFull(month)} ${toBengaliDigits(year.toString())}"
      AppLanguage.ENGLISH -> {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.US)
        sdf.format(Date(timestamp))
      }
    }
  }

  private fun getBengaliMonthShort(monthIndex: Int): String {
    return when (monthIndex) {
      0 -> "জানু"
      1 -> "ফেব্রু"
      2 -> "মার্চ"
      3 -> "এপ্রিল"
      4 -> "মে"
      5 -> "জুন"
      6 -> "জুলাই"
      7 -> "আগস্ট"
      8 -> "সেপ্টে"
      9 -> "অক্টো"
      10 -> "নভে"
      11 -> "ডিসে"
      else -> ""
    }
  }

  private fun getBengaliMonthFull(monthIndex: Int): String {
    return when (monthIndex) {
      0 -> "জানুয়ারি"
      1 -> "ফেব্রুয়ারি"
      2 -> "মার্চ"
      3 -> "এপ্রিল"
      4 -> "মে"
      5 -> "জুন"
      6 -> "জুলাই"
      7 -> "আগস্ট"
      8 -> "সেপ্টেম্বর"
      9 -> "অক্টোবর"
      10 -> "নভেম্বর"
      11 -> "ডিসেম্বর"
      else -> ""
    }
  }
}
