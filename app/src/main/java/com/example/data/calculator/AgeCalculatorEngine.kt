package com.example.data.calculator

import java.util.Calendar

data class UpcomingBirthday(
  val year: Int,
  val weekdayBn: String,
  val weekdayEn: String,
  val daysRemaining: Int
)

data class AgeResult(
  val years: Int,
  val months: Int,
  val days: Int,
  val totalMonths: Long,
  val totalWeeks: Long,
  val totalDays: Long,
  val totalHours: Long,
  val totalMinutes: Long,
  val totalSeconds: Long,
  val nextBirthdayMonths: Int,
  val nextBirthdayDays: Int,
  val nextBirthdayTotalDays: Int,
  val nextBirthdayWeekdayBn: String,
  val nextBirthdayWeekdayEn: String,
  val upcomingBirthdays: List<UpcomingBirthday>,
  val zodiacSignBn: String,
  val zodiacSignEn: String,
  val zodiacSymbol: String,
  val zodiacTraitsBn: String,
  val zodiacTraitsEn: String
)

object AgeCalculatorEngine {

  fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
  }

  fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
      1, 3, 5, 7, 8, 10, 12 -> 31
      4, 6, 9, 11 -> 30
      2 -> if (isLeapYear(year)) 29 else 28
      else -> 30
    }
  }

  fun calculateAge(
    bYear: Int,
    bMonth: Int, // 1-12
    bDay: Int,
    tYear: Int,
    tMonth: Int, // 1-12
    tDay: Int
  ): AgeResult {
    var curDay = tDay
    var curMonth = tMonth
    var curYear = tYear

    // Adjust days if needed
    if (curDay < bDay) {
      val prevMonth = if (curMonth == 1) 12 else curMonth - 1
      val prevYear = if (curMonth == 1) curYear - 1 else curYear
      curDay += daysInMonth(prevYear, prevMonth)
      curMonth -= 1
    }

    // Adjust months if needed
    if (curMonth < bMonth) {
      curMonth += 12
      curYear -= 1
    }

    val years = (curYear - bYear).coerceAtLeast(0)
    val months = (curMonth - bMonth).coerceAtLeast(0)
    val days = (curDay - bDay).coerceAtLeast(0)

    // Calculate total days difference using Calendar epoch
    val birthCal = Calendar.getInstance().apply {
      set(bYear, bMonth - 1, bDay, 0, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val targetCal = Calendar.getInstance().apply {
      set(tYear, tMonth - 1, tDay, 0, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }

    val diffMillis = (targetCal.timeInMillis - birthCal.timeInMillis).coerceAtLeast(0L)
    val totalDays = diffMillis / (1000L * 60 * 60 * 24)
    val totalWeeks = totalDays / 7
    val totalMonths = (years * 12L) + months
    val totalHours = totalDays * 24
    val totalMinutes = totalHours * 60
    val totalSeconds = totalMinutes * 60

    // Next Birthday Calculation
    val nextBdayCal = Calendar.getInstance().apply {
      set(tYear, bMonth - 1, bDay, 0, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    if (nextBdayCal.before(targetCal)) {
      nextBdayCal.set(Calendar.YEAR, tYear + 1)
    }

    val nextDiffMillis = (nextBdayCal.timeInMillis - targetCal.timeInMillis).coerceAtLeast(0L)
    val nextTotalDays = (nextDiffMillis / (1000L * 60 * 60 * 24)).toInt()
    val nextMonths = nextTotalDays / 30
    val nextDays = nextTotalDays % 30

    val weekday = nextBdayCal.get(Calendar.DAY_OF_WEEK)
    val (nextWdBn, nextWdEn) = getWeekdayName(weekday)

    // Upcoming 5 Birthdays
    val upcoming = mutableListOf<UpcomingBirthday>()
    val baseYear = nextBdayCal.get(Calendar.YEAR)
    for (i in 0 until 5) {
      val y = baseYear + i
      val uCal = Calendar.getInstance().apply {
        set(y, bMonth - 1, bDay, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
      }
      val uDiff = ((uCal.timeInMillis - targetCal.timeInMillis).coerceAtLeast(0L) / (1000L * 60 * 60 * 24)).toInt()
      val (wBn, wEn) = getWeekdayName(uCal.get(Calendar.DAY_OF_WEEK))
      upcoming.add(UpcomingBirthday(y, wBn, wEn, uDiff))
    }

    // Zodiac
    val (zBn, zEn, zSym, tBn, tEn) = getZodiac(bMonth, bDay)

    return AgeResult(
      years = years,
      months = months,
      days = days,
      totalMonths = totalMonths,
      totalWeeks = totalWeeks,
      totalDays = totalDays,
      totalHours = totalHours,
      totalMinutes = totalMinutes,
      totalSeconds = totalSeconds,
      nextBirthdayMonths = nextMonths,
      nextBirthdayDays = nextDays,
      nextBirthdayTotalDays = nextTotalDays,
      nextBirthdayWeekdayBn = nextWdBn,
      nextBirthdayWeekdayEn = nextWdEn,
      upcomingBirthdays = upcoming,
      zodiacSignBn = zBn,
      zodiacSignEn = zEn,
      zodiacSymbol = zSym,
      zodiacTraitsBn = tBn,
      zodiacTraitsEn = tEn
    )
  }

  private fun getWeekdayName(dayOfWeek: Int): Pair<String, String> {
    return when (dayOfWeek) {
      Calendar.SUNDAY -> "রবিবার" to "Sunday"
      Calendar.MONDAY -> "সোমবার" to "Monday"
      Calendar.TUESDAY -> "মঙ্গলবার" to "Tuesday"
      Calendar.WEDNESDAY -> "বুধবার" to "Wednesday"
      Calendar.THURSDAY -> "বৃহস্পতিবার" to "Thursday"
      Calendar.FRIDAY -> "শুক্রবার" to "Friday"
      Calendar.SATURDAY -> "শনিবার" to "Saturday"
      else -> "দিন" to "Day"
    }
  }

  private fun getZodiac(month: Int, day: Int): ZodiacData {
    return when {
      (month == 3 && day >= 21) || (month == 4 && day <= 19) ->
        ZodiacData("মেষ", "Aries", "♈", "সাহসী, উদ্যমী ও নেতৃত্বশীল", "Courageous, energetic & passionate")
      (month == 4 && day >= 20) || (month == 5 && day <= 20) ->
        ZodiacData("বৃষ", "Taurus", "♉", "ধৈর্যশীল, বিশ্বস্ত ও পরিশ্রমী", "Patient, reliable & determined")
      (month == 5 && day >= 21) || (month == 6 && day <= 20) ->
        ZodiacData("মিথুন", "Gemini", "♊", "বুদ্ধিমান, কৌতূহলী ও মিশুক", "Curious, adaptable & expressive")
      (month == 6 && day >= 21) || (month == 7 && day <= 22) ->
        ZodiacData("কর্কট", "Cancer", "♋", "সহানুভূতিশীল, যত্নশীল ও প্রতিরক্ষামূলক", "Intuitive, caring & loyal")
      (month == 7 && day >= 23) || (month == 8 && day <= 22) ->
        ZodiacData("সিংহ", "Leo", "♌", "উদার, আত্মবিশ্বাসী ও প্রাণবন্ত", "Confident, generous & charismatic")
      (month == 8 && day >= 23) || (month == 9 && day <= 22) ->
        ZodiacData("কন্যা", "Virgo", "♍", "বিশ্লেষণশীল, নিয়মানুবর্তী ও বুদ্ধিমান", "Analytical, methodical & practical")
      (month == 9 && day >= 23) || (month == 10 && day <= 22) ->
        ZodiacData("তুলা", "Libra", "♎", "শান্তিপ্রিয়, ন্যায়পরায়ণ ও ভারসাম্যপূর্ণ", "Diplomatic, fair-minded & social")
      (month == 10 && day >= 23) || (month == 11 && day <= 21) ->
        ZodiacData("বৃশ্চিক", "Scorpio", "♏", "দৃঢ়প্রতিজ্ঞ, গভীর ও অনুরাগী", "Resourceful, brave & passionate")
      (month == 11 && day >= 22) || (month == 12 && day <= 21) ->
        ZodiacData("ধনু", "Sagittarius", "♐", "স্বাধীনচেতা, আশাবাদী ও ভ্রমণপিপাসু", "Optimistic, philosophical & generous")
      (month == 12 && day >= 22) || (month == 1 && day <= 19) ->
        ZodiacData("মকর", "Capricorn", "♑", "দায়িত্ববান, শৃঙ্খলাবদ্ধ ও উচ্চাকাঙ্ক্ষী", "Disciplined, patient & ambitious")
      (month == 1 && day >= 20) || (month == 2 && day <= 18) ->
        ZodiacData("কুম্ভ", "Aquarius", "♒", "উদ্ভাবনী, মুক্তমনা ও মানবতাবাদী", "Progressive, original & independent")
      else ->
        ZodiacData("মীন", "Pisces", "♓", "কল্পনাপ্রবণ, সহানুভূতিশীল ও শিল্পমনা", "Compassionate, artistic & intuitive")
    }
  }

  private data class ZodiacData(
    val bn: String,
    val en: String,
    val symbol: String,
    val traitsBn: String,
    val traitsEn: String
  )
}
