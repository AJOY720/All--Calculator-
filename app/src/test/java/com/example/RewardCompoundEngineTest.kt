package com.example

import com.example.data.calculator.RewardCompoundEngine
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardCompoundEngineTest {

  @Test
  fun testCompoundCalculationMatchesWeb() {
    val input = CalculationInput(
      principal = 10000.0,
      dailyRatePercent = 1.0,
      days = 30,
      currency = "৳",
      isCompound = true
    )

    val result = RewardCompoundEngine.calculate(input)

    assertEquals(30, result.dailyRecords.size)
    // 10000 * (1.01)^30 = 13478.489...
    assertEquals(13478.49, result.finalBalance, 0.05)
    assertEquals(3478.49, result.totalProfit, 0.05)
    assertEquals(34.78, result.totalReturnPercent, 0.05)
    assertTrue(result.compoundDifference > 478.0)
  }

  @Test
  fun testSimpleCalculationWithoutCompounding() {
    val input = CalculationInput(
      principal = 10000.0,
      dailyRatePercent = 1.0,
      days = 30,
      currency = "৳",
      isCompound = false
    )

    val result = RewardCompoundEngine.calculate(input)

    assertEquals(30, result.dailyRecords.size)
    assertEquals(10000.0, result.finalBalance, 0.01)
    assertEquals(3000.0, result.totalProfit, 0.01)
    assertEquals(30.0, result.totalReturnPercent, 0.01)
    assertEquals(0.0, result.compoundDifference, 0.001)
  }

  @Test
  fun testCsvGenerationContainsBomAndHeaders() {
    val input = CalculationInput(principal = 1000.0, dailyRatePercent = 1.0, days = 5)
    val result = RewardCompoundEngine.calculate(input)

    val csvBn = RewardCompoundEngine.generateCsv(result, AppLanguage.BENGALI)
    assertTrue(csvBn.startsWith("\uFEFF"))
    assertTrue(csvBn.contains("দিন,শুরুর ব্যালান্স,রিওয়ার্ড,শেষের ব্যালান্স"))

    val csvEn = RewardCompoundEngine.generateCsv(result, AppLanguage.ENGLISH)
    assertTrue(csvEn.contains("Day,Opening Balance,Reward,Closing Balance"))
  }
}
