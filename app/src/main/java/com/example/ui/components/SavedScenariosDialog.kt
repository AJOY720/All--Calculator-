package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.RewardCompoundEngine
import com.example.data.model.AppLanguage
import com.example.data.model.SavedCalculation
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandMuted
import com.example.ui.theme.ProfitGreen

@Composable
fun SaveScenarioDialog(
  language: AppLanguage,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  val isBn = language == AppLanguage.BENGALI

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isBn) "এই হিসাবটি সংরক্ষণ করুন" else "Save This Calculation",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column {
        Text(
          text = if (isBn) "ভবিষ্যতে দ্রুত দেখার জন্য একটি নাম দিন:" else "Give this calculation a memorable title:",
          style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = {
            Text(if (isBn) "যেমন: স্ট্যাকিং ১% (৩০ দিন)" else "e.g. Staking 1% (30d)")
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_scenario_title_input"),
          shape = RoundedCornerShape(10.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(title) },
        colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
        modifier = Modifier.testTag("confirm_save_button")
      ) {
        Text(if (isBn) "সংরক্ষণ করুন" else "Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBn) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun SavedScenariosListDialog(
  savedList: List<SavedCalculation>,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onLoad: (SavedCalculation) -> Unit,
  onDelete: (Long) -> Unit
) {
  val isBn = language == AppLanguage.BENGALI

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isBn) "সংরক্ষিত ও নমুনা হিসাবসমূহ" else "Saved & Sample Scenarios",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Box(modifier = Modifier.heightIn(max = 400.dp)) {
        if (savedList.isEmpty()) {
          Text(
            text = if (isBn) "কোনো সংরক্ষিত হিসাব নেই।" else "No saved scenarios yet.",
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandMuted),
            modifier = Modifier.padding(vertical = 16.dp)
          )
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(savedList, key = { it.id }) { item ->
              SavedItemCard(
                item = item,
                isBn = isBn,
                onLoad = { onLoad(item) },
                onDelete = { onDelete(item.id) }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBn) "বন্ধ করুন" else "Close")
      }
    }
  )
}

@Composable
private fun SavedItemCard(
  item: SavedCalculation,
  isBn: Boolean,
  onLoad: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onLoad)
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(2.dp))
        val modeStr = if (item.isCompound) (if (isBn) "কম্পাউন্ড" else "Compound") else (if (isBn) "সরল" else "Simple")
        Text(
          text = "${RewardCompoundEngine.formatMoney(item.principal, item.currency)} • ${item.dailyRate}%/দিন • ${item.days} দিন ($modeStr)",
          style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted, fontSize = 11.sp)
        )
        Text(
          text = "${if (isBn) "মোট ব্যালান্স:" else "Final:"} ${RewardCompoundEngine.formatMoney(item.finalBalance, item.currency)} (+${RewardCompoundEngine.formatMoney(item.totalProfit, item.currency)})",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ProfitGreen
          )
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onLoad) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Load",
            tint = BrandAccentBlue
          )
        }
        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = BrandMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun InfoDialog(
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  val isBn = language == AppLanguage.BENGALI

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isBn) "কম্পাউন্ড হিসাবের নিয়ম" else "How Compounding Works",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = if (isBn) {
            """
• কম্পাউন্ডিং (Compounding):
প্রতিদিন যে রিওয়ার্ড অর্জিত হয়, তা পুনরায় আপনার মূল ব্যালান্সে যোগ হয়। পরের দিন বর্ধিত পরিমাণের ওপর রিওয়ার্ড হিসাব হয়। ফলে দিন বাড়ার সাথে সাথে লাভের পরিমাণ বহুগুণ বাড়ে (Exponential Growth)।

• কম্পাউন্ড ছাড়া (Simple):
প্রতিদিন শুধু প্রাথমিক মূলধনের ওপর একই অঙ্কের রিওয়ার্ড অর্জিত হয়।

• গাণিতিক সূত্র:
A = P × (1 + r)ⁿ
যেখানে P = মূলধন, r = দৈনিক রিওয়ার্ড হার, n = দিন।
            """.trimIndent()
          } else {
            """
• Daily Compounding:
Each day's earned reward is automatically reinvested into your opening balance. The subsequent day generates rewards on this larger balance, producing exponential growth over time.

• Without Compounding (Simple):
Rewards are earned solely on the initial principal without reinvesting.

• Mathematical Formula:
A = P × (1 + r)ⁿ
Where P = principal, r = daily rate %, n = days.
            """.trimIndent()
          },
          style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBn) "বুঝেছি" else "Got It")
      }
    }
  )
}
