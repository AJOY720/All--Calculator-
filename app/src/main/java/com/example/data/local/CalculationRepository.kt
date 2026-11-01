package com.example.data.local

import android.content.Context
import com.example.data.model.CalculationInput
import com.example.data.model.SavedCalculation
import org.json.JSONArray
import org.json.JSONObject

class CalculationRepository(context: Context) {
  private val prefs = context.getSharedPreferences("reward_calc_prefs", Context.MODE_PRIVATE)
  private val KEY_SAVED = "saved_calculations_list"
  private val KEY_CURRENCY = "preferred_currency"

  fun getPreferredCurrency(): String {
    return prefs.getString(KEY_CURRENCY, "৳") ?: "৳"
  }

  fun savePreferredCurrency(currency: String) {
    prefs.edit().putString(KEY_CURRENCY, currency).apply()
  }

  fun getSavedCalculations(): List<SavedCalculation> {
    val jsonString = prefs.getString(KEY_SAVED, null) ?: return defaultPresets()
    return try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<SavedCalculation>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          SavedCalculation(
            id = obj.optLong("id", System.currentTimeMillis()),
            title = obj.optString("title", "হিসাব"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            principal = obj.optDouble("principal", 10000.0),
            dailyRate = obj.optDouble("dailyRate", 1.0),
            days = obj.optInt("days", 30),
            currency = obj.optString("currency", "৳"),
            isCompound = obj.optBoolean("isCompound", true),
            finalBalance = obj.optDouble("finalBalance", 0.0),
            totalProfit = obj.optDouble("totalProfit", 0.0)
          )
        )
      }
      if (list.isEmpty()) defaultPresets() else list
    } catch (e: Exception) {
      defaultPresets()
    }
  }

  fun saveCalculation(
    title: String,
    input: CalculationInput,
    finalBalance: Double,
    totalProfit: Double
  ) {
    val current = getSavedCalculations().toMutableList()
    val newSaved = SavedCalculation(
      id = System.currentTimeMillis(),
      title = title.ifBlank { "হিসাব #${current.size + 1}" },
      timestamp = System.currentTimeMillis(),
      principal = input.principal,
      dailyRate = input.dailyRatePercent,
      days = input.days,
      currency = input.currency,
      isCompound = input.isCompound,
      finalBalance = finalBalance,
      totalProfit = totalProfit
    )
    current.add(0, newSaved)
    saveList(current)
  }

  fun deleteCalculation(id: Long) {
    val current = getSavedCalculations().toMutableList()
    current.removeAll { it.id == id }
    saveList(current)
  }

  private fun saveList(list: List<SavedCalculation>) {
    val array = JSONArray()
    list.forEach { item ->
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("timestamp", item.timestamp)
        put("principal", item.principal)
        put("dailyRate", item.dailyRate)
        put("days", item.days)
        put("currency", item.currency)
        put("isCompound", item.isCompound)
        put("finalBalance", item.finalBalance)
        put("totalProfit", item.totalProfit)
      }
      array.put(obj)
    }
    prefs.edit().putString(KEY_SAVED, array.toString()).apply()
  }

  private fun defaultPresets(): List<SavedCalculation> {
    return listOf(
      SavedCalculation(
        id = 1L,
        title = "উদাহরণ: ৩০ দিনের ১% কম্পাউন্ড",
        timestamp = System.currentTimeMillis(),
        principal = 10000.0,
        dailyRate = 1.0,
        days = 30,
        currency = "৳",
        isCompound = true,
        finalBalance = 13478.49,
        totalProfit = 3478.49
      ),
      SavedCalculation(
        id = 2L,
        title = "উদাহরণ: ৯০ দিনের ০.৭৫% রিওয়ার্ড",
        timestamp = System.currentTimeMillis(),
        principal = 50000.0,
        dailyRate = 0.75,
        days = 90,
        currency = "৳",
        isCompound = true,
        finalBalance = 98016.32,
        totalProfit = 48016.32
      ),
      SavedCalculation(
        id = 3L,
        title = "উদাহরণ: USDT স্ট্যাকিং (৬০ দিন, ১.২%)",
        timestamp = System.currentTimeMillis(),
        principal = 1000.0,
        dailyRate = 1.2,
        days = 60,
        currency = "USDT",
        isCompound = true,
        finalBalance = 2045.54,
        totalProfit = 1045.54
      )
    )
  }
}
