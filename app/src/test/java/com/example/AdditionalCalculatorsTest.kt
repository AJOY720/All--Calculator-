package com.example

import com.example.data.calculator.AgeCalculatorEngine
import com.example.data.calculator.StandardCalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdditionalCalculatorsTest {

  @Test
  fun testStandardCalculatorBasicOperations() {
    val res1 = StandardCalculatorEngine.evaluate("2 + 3 * 4")
    assertTrue(res1.isSuccess)
    assertEquals(14.0, res1.getOrThrow(), 0.001)

    val res2 = StandardCalculatorEngine.evaluate("(2 + 3) * 4")
    assertTrue(res2.isSuccess)
    assertEquals(20.0, res2.getOrThrow(), 0.001)

    val res3 = StandardCalculatorEngine.evaluate("100 - 25 / 5")
    assertTrue(res3.isSuccess)
    assertEquals(95.0, res3.getOrThrow(), 0.001)
  }

  @Test
  fun testStandardCalculatorScientificOperations() {
    val resSqrt = StandardCalculatorEngine.evaluate("√(16)")
    assertTrue(resSqrt.isSuccess)
    assertEquals(4.0, resSqrt.getOrThrow(), 0.001)

    val resPow = StandardCalculatorEngine.evaluate("2 ^ 3")
    assertTrue(resPow.isSuccess)
    assertEquals(8.0, resPow.getOrThrow(), 0.001)

    val resPercent = StandardCalculatorEngine.evaluate("200 % 10")
    assertTrue(resPercent.isSuccess)
    assertEquals(20.0, resPercent.getOrThrow(), 0.001)

    // Unary standalone percentages
    val p1 = StandardCalculatorEngine.evaluate("50%")
    assertTrue(p1.isSuccess)
    assertEquals(0.5, p1.getOrThrow(), 0.001)

    val p2 = StandardCalculatorEngine.evaluate("50 %")
    assertTrue(p2.isSuccess)
    assertEquals(0.5, p2.getOrThrow(), 0.001)

    // Additive percentages (e.g. tax: 100 + 10% = 110)
    val pAdd = StandardCalculatorEngine.evaluate("100 + 10%")
    assertTrue(pAdd.isSuccess)
    assertEquals(110.0, pAdd.getOrThrow(), 0.001)

    // Subtractive percentages (e.g. discount: 1000 - 15% = 850)
    val pSub = StandardCalculatorEngine.evaluate("1000 - 15%")
    assertTrue(pSub.isSuccess)
    assertEquals(850.0, pSub.getOrThrow(), 0.001)

    // Multiplicative percentage (e.g. 500 * 20% = 100)
    val pMul = StandardCalculatorEngine.evaluate("500 * 20%")
    assertTrue(pMul.isSuccess)
    assertEquals(100.0, pMul.getOrThrow(), 0.001)

    // Formatted numbers with thousand-separator commas (Screenshot bug case: 3,000 × 20% = 600)
    val pCommaMul = StandardCalculatorEngine.evaluate("3,000 × 20%")
    assertTrue(pCommaMul.isSuccess)
    assertEquals(600.0, pCommaMul.getOrThrow(), 0.001)

    val pCommaAdd = StandardCalculatorEngine.evaluate("3,000 + 20%")
    assertTrue(pCommaAdd.isSuccess)
    assertEquals(3600.0, pCommaAdd.getOrThrow(), 0.001)

    val pCommaSub = StandardCalculatorEngine.evaluate("3,000 − 20%")
    assertTrue(pCommaSub.isSuccess)
    assertEquals(2400.0, pCommaSub.getOrThrow(), 0.001)

    val pCommaDiv = StandardCalculatorEngine.evaluate("3,000 ÷ 20%")
    assertTrue(pCommaDiv.isSuccess)
    assertEquals(15000.0, pCommaDiv.getOrThrow(), 0.001)

    // Large number with multiple commas: 1,000,000 * 5% = 50000
    val pLargeComma = StandardCalculatorEngine.evaluate("1,000,000 × 5%")
    assertTrue(pLargeComma.isSuccess)
    assertEquals(50000.0, pLargeComma.getOrThrow(), 0.001)

    // Bengali numerals with commas: ৩,০০০ × ২০% = 600
    val pBengali = StandardCalculatorEngine.evaluate("৩,০০০ × ২০%")
    assertTrue(pBengali.isSuccess)
    assertEquals(600.0, pBengali.getOrThrow(), 0.001)

    // Division with percentage (e.g. 500 / 20% = 2500)
    val pDiv = StandardCalculatorEngine.evaluate("500 / 20%")
    assertTrue(pDiv.isSuccess)
    assertEquals(2500.0, pDiv.getOrThrow(), 0.001)

    // Parenthesized percentage
    val pParen = StandardCalculatorEngine.evaluate("(50 + 50)%")
    assertTrue(pParen.isSuccess)
    assertEquals(1.0, pParen.getOrThrow(), 0.001)
  }

  @Test
  fun testAgeCalculatorExactCalculation() {
    // Born: 2000-01-01, Target: 2026-10-10
    val age = AgeCalculatorEngine.calculateAge(
      bYear = 2000,
      bMonth = 1,
      bDay = 1,
      tYear = 2026,
      tMonth = 10,
      tDay = 10
    )

    assertEquals(26, age.years)
    assertEquals(9, age.months)
    assertEquals(9, age.days)
    assertTrue(age.totalDays > 9700)
    assertEquals("Capricorn", age.zodiacSignEn)
    assertEquals("মকর", age.zodiacSignBn)
    assertEquals(5, age.upcomingBirthdays.size)
  }

  @Test
  fun testFiftyNumbersChainSum() {
    val numbers = (1..50).toList()
    val expr = numbers.joinToString(" + ")
    val result = StandardCalculatorEngine.evaluate(expr)
    assertTrue(result.isSuccess)
    // Sum of 1..50 = 50 * 51 / 2 = 1275
    assertEquals(1275.0, result.getOrThrow(), 0.001)

    // Formatted display
    val formatted = StandardCalculatorEngine.formatDisplay(result.getOrThrow())
    assertEquals("1,275", formatted)
  }

  @Test
  fun testLeapYearRules() {
    assertTrue(AgeCalculatorEngine.isLeapYear(2024))
    assertTrue(AgeCalculatorEngine.isLeapYear(2000))
    assertTrue(!AgeCalculatorEngine.isLeapYear(1900))
    assertTrue(!AgeCalculatorEngine.isLeapYear(2025))
    assertEquals(29, AgeCalculatorEngine.daysInMonth(2024, 2))
    assertEquals(28, AgeCalculatorEngine.daysInMonth(2025, 2))
  }

  @Test
  fun testHistoryDateFormatterBengaliDigits() {
    val bnStr = com.example.ui.util.HistoryDateFormatter.toBengaliDigits("1234567890")
    assertEquals("১২৩৪৫৬৭৮৯০", bnStr)
  }

  @Test
  fun testHistoryDateFormatterRelativeTime() {
    val now = System.currentTimeMillis()
    val justNowBn = com.example.ui.util.HistoryDateFormatter.getRelativeTimeSpan(now - 1000L, com.example.data.model.AppLanguage.BENGALI)
    assertEquals("এইমাত্র", justNowBn)

    val justNowEn = com.example.ui.util.HistoryDateFormatter.getRelativeTimeSpan(now - 1000L, com.example.data.model.AppLanguage.ENGLISH)
    assertEquals("Just now", justNowEn)

    val twoDaysAgo = now - (2 * 24 * 60 * 60 * 1000L)
    val twoDaysBn = com.example.ui.util.HistoryDateFormatter.getRelativeTimeSpan(twoDaysAgo, com.example.data.model.AppLanguage.BENGALI)
    assertTrue(twoDaysBn.contains("দিন আগে"))

    val twoDaysEn = com.example.ui.util.HistoryDateFormatter.getRelativeTimeSpan(twoDaysAgo, com.example.data.model.AppLanguage.ENGLISH)
    assertTrue(twoDaysEn.contains("days ago"))
  }

  @Test
  fun testSixMonthsCutoffCalculation() {
    val now = System.currentTimeMillis()
    val sixMonthsMillis = com.example.data.local.NormalCalcHistoryRepository.SIX_MONTHS_MILLIS
    val withinSixMonths = now - (100L * 24 * 60 * 60 * 1000L) // 100 days ago
    val olderThanSixMonths = now - (200L * 24 * 60 * 60 * 1000L) // 200 days ago

    assertTrue(withinSixMonths >= (now - sixMonthsMillis))
    assertTrue(olderThanSixMonths < (now - sixMonthsMillis))
  }

  @Test
  fun testLoanCalculatorReducingBalanceEmi() {
    // 500,000 at 10% for 5 years (60 months)
    val result = com.example.data.calculator.LoanCalculatorEngine.calculateLoan(
      principal = 500000.0,
      annualRate = 10.0,
      tenure = 5,
      tenureUnit = com.example.data.calculator.LoanTenureUnit.YEARS,
      interestType = com.example.data.calculator.LoanInterestType.REDUCING_BALANCE
    )

    // Standard formula: E = 500000 * (0.10/12) * (1 + 0.10/12)^60 / ((1 + 0.10/12)^60 - 1) ≈ 10,623.52
    assertEquals(10624.0, result.monthlyEmi, 2.0)
    assertEquals(60, result.tenureMonths)
    assertTrue(result.totalInterest > 137000.0 && result.totalInterest < 138000.0)
    assertEquals(result.principal + result.totalInterest, result.totalPayment, 1.0)
    assertEquals(5, result.yearlySchedule.size)
    assertEquals(60, result.monthlySchedule.size)
  }

  @Test
  fun testLoanCalculatorFlatRateEmi() {
    // 100,000 at 12% for 1 year flat
    val result = com.example.data.calculator.LoanCalculatorEngine.calculateLoan(
      principal = 100000.0,
      annualRate = 12.0,
      tenure = 1,
      tenureUnit = com.example.data.calculator.LoanTenureUnit.YEARS,
      interestType = com.example.data.calculator.LoanInterestType.FLAT_RATE
    )

    // Interest = 100,000 * 12% = 12,000
    // Total = 112,000, EMI = 112,000 / 12 = 9333.33
    assertEquals(12000.0, result.totalInterest, 0.01)
    assertEquals(112000.0, result.totalPayment, 0.01)
    assertEquals(9333.33, result.monthlyEmi, 0.5)
  }

  @Test
  fun testLoanCalculatorPrepaymentSimulation() {
    // 500,000 at 10% for 5 years (60 months), adding 2,000 extra per month
    val result = com.example.data.calculator.LoanCalculatorEngine.calculateLoan(
      principal = 500000.0,
      annualRate = 10.0,
      tenure = 5,
      tenureUnit = com.example.data.calculator.LoanTenureUnit.YEARS,
      interestType = com.example.data.calculator.LoanInterestType.REDUCING_BALANCE,
      extraMonthlyPayment = 2000.0
    )

    val prep = result.prepaymentBenefit
    assertTrue(prep != null)
    assertTrue(prep!!.monthsSaved > 0)
    assertTrue(prep.interestSaved > 0)
    assertTrue(prep.newMonths < prep.originalMonths)
  }
}

