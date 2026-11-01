package com.example.data.local

import android.content.Context
import com.example.ui.normalcalc.NormalCalcHistoryItem
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages persistent storage for Normal Calculator history.
 * Retains all calculation history for up to 6 months (180 days).
 * Automatically cleans up entries older than 6 months.
 */
class NormalCalcHistoryRepository(context: Context) {
  private val prefs = context.getSharedPreferences("normal_calc_history_prefs", Context.MODE_PRIVATE)
  private val KEY_HISTORY = "history_list_json"

  companion object {
    // 6 months in milliseconds (approx 180 days)
    const val SIX_MONTHS_MILLIS = 180L * 24L * 60L * 60L * 1000L
  }

  /**
   * Retrieves all calculations saved within the last 6 months.
   * Older records are automatically pruned.
   */
  fun getHistory(): List<NormalCalcHistoryItem> {
    val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
    return try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<NormalCalcHistoryItem>()
      val cutoffTime = System.currentTimeMillis() - SIX_MONTHS_MILLIS
      var hasPruned = false

      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
        if (timestamp >= cutoffTime) {
          list.add(
            NormalCalcHistoryItem(
              id = obj.optLong("id", timestamp),
              expression = obj.optString("expression", ""),
              result = obj.optString("result", ""),
              timestamp = timestamp
            )
          )
        } else {
          hasPruned = true
        }
      }

      val sorted = list.sortedByDescending { it.timestamp }
      if (hasPruned) {
        saveList(sorted)
      }
      sorted
    } catch (e: Exception) {
      emptyList()
    }
  }

  /**
   * Adds a new calculation to persistent history, preserving records up to 6 months.
   */
  fun addCalculation(expression: String, result: String): List<NormalCalcHistoryItem> {
    val current = getHistory().toMutableList()
    val now = System.currentTimeMillis()
    val newItem = NormalCalcHistoryItem(
      id = now,
      expression = expression,
      result = result,
      timestamp = now
    )
    current.add(0, newItem)
    val cutoffTime = now - SIX_MONTHS_MILLIS
    val retained = current.filter { it.timestamp >= cutoffTime }
    saveList(retained)
    return retained
  }

  /**
   * Deletes a specific history record by ID or timestamp.
   */
  fun deleteCalculation(timestamp: Long): List<NormalCalcHistoryItem> {
    val current = getHistory().toMutableList()
    current.removeAll { it.timestamp == timestamp || it.id == timestamp }
    saveList(current)
    return current
  }

  /**
   * Clears all history.
   */
  fun clearAll(): List<NormalCalcHistoryItem> {
    prefs.edit().remove(KEY_HISTORY).apply()
    return emptyList()
  }

  /**
   * Saves raw list of history items after 6-month pruning.
   */
  fun saveAll(items: List<NormalCalcHistoryItem>): List<NormalCalcHistoryItem> {
    val cutoffTime = System.currentTimeMillis() - SIX_MONTHS_MILLIS
    val retained = items.filter { it.timestamp >= cutoffTime }
    saveList(retained)
    return retained
  }

  private fun saveList(list: List<NormalCalcHistoryItem>) {
    val array = JSONArray()
    list.forEach { item ->
      val obj = JSONObject().apply {
        put("id", item.id)
        put("expression", item.expression)
        put("result", item.result)
        put("timestamp", item.timestamp)
      }
      array.put(obj)
    }
    prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
  }

  /**
   * Generates a realistic set of sample calculations across 6 months
   * (today, 2 days ago, 1 week ago, 1 month ago, 3 months ago, 5 months ago)
   * so users can immediately test the 6-month history and date features.
   */
  fun generateSampleSixMonthsData(): List<NormalCalcHistoryItem> {
    val now = System.currentTimeMillis()
    val oneDay = 24L * 60L * 60L * 1000L

    val sample50Numbers = listOf(
      150L, 230L, 85L, 420L, 95L, 310L, 500L, 120L, 650L, 275L,
      80L, 190L, 340L, 210L, 950L, 160L, 75L, 430L, 320L, 580L,
      115L, 240L, 360L, 490L, 70L, 820L, 130L, 290L, 450L, 600L,
      175L, 380L, 520L, 90L, 260L, 710L, 840L, 140L, 330L, 470L,
      225L, 590L, 185L, 315L, 465L, 625L, 780L, 890L, 105L, 960L
    )

    val samples = listOf(
      // 1. Today: 50-number sum
      NormalCalcHistoryItem(
        id = now,
        expression = sample50Numbers.joinToString(" + "),
        result = "21,630",
        timestamp = now - (30 * 60 * 1000L) // 30 minutes ago today
      ),
      // 2. Today: Commercial percent calculation
      NormalCalcHistoryItem(
        id = now - 1,
        expression = "3,000 × 20%",
        result = "600",
        timestamp = now - (2 * 60 * 60 * 1000L) // 2 hours ago
      ),
      // 3. Yesterday: Compound & tax computation
      NormalCalcHistoryItem(
        id = now - 2,
        expression = "25,000 + 15%",
        result = "28,750",
        timestamp = now - oneDay - (3 * 60 * 60 * 1000L) // Yesterday
      ),
      // 4. Last week: 5 days ago
      NormalCalcHistoryItem(
        id = now - 3,
        expression = "1,200 × 45 − 10%",
        result = "48,600",
        timestamp = now - (5 * oneDay)
      ),
      // 5. 2 weeks ago
      NormalCalcHistoryItem(
        id = now - 4,
        expression = "85,000 ÷ 12 + 500",
        result = "7,583.33",
        timestamp = now - (14 * oneDay)
      ),
      // 6. 1 month ago
      NormalCalcHistoryItem(
        id = now - 5,
        expression = "50,000 × 1.08 ^ 3",
        result = "62,985.60",
        timestamp = now - (32 * oneDay)
      ),
      // 7. 2 months ago: Multi-item addition
      NormalCalcHistoryItem(
        id = now - 6,
        expression = "4500 + 7200 + 8900 + 12500 + 3400 + 9100",
        result = "45,600",
        timestamp = now - (65 * oneDay)
      ),
      // 8. 3 months ago: Large calculation
      NormalCalcHistoryItem(
        id = now - 7,
        expression = "150,000 − 28% + 12,000",
        result = "120,000",
        timestamp = now - (95 * oneDay)
      ),
      // 9. 4 months ago
      NormalCalcHistoryItem(
        id = now - 8,
        expression = "√(14400) × 25 + 1000",
        result = "4,000",
        timestamp = now - (125 * oneDay)
      ),
      // 10. 5 months ago (well within 6-month retention)
      NormalCalcHistoryItem(
        id = now - 9,
        expression = "2,500,000 × 7.5% ÷ 12",
        result = "15,625",
        timestamp = now - (155 * oneDay)
      )
    )

    saveAll(samples)
    return samples
  }
}
