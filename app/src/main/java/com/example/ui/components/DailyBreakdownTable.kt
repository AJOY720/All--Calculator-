package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.RewardCompoundEngine
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationResult
import com.example.data.model.DailyRecord
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.theme.ProfitGreen
import com.example.ui.util.Strings
import kotlinx.coroutines.launch

@Composable
fun DailyBreakdownTable(
  result: CalculationResult?,
  language: AppLanguage,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onSelectScrubDay: (Int?) -> Unit,
  modifier: Modifier = Modifier
) {
  if (result == null || result.dailyRecords.isEmpty()) return

  val records = result.dailyRecords
  val currency = result.input.currency
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val filteredRecords = if (searchQuery.isBlank()) {
    records
  } else {
    records.filter { it.day.toString().contains(searchQuery.trim()) }
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = Strings.breakdownTitle(language),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )
        Text(
          text = "${records.size} ${if (language == AppLanguage.BENGALI) "দিন" else "Days"}",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = BrandAccentBlue
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search & Quick Jump Row
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
          Text(
            text = if (language == AppLanguage.BENGALI) "নির্দিষ্ট দিন খুঁজুন (যেমন: ১৫)" else "Search day (e.g. 15)",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted)
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = BrandMuted, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
            }
          }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("table_search_input"),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandAccentBlue,
          unfocusedBorderColor = BrandLine
        )
      )

      // Milestone Jump Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 12.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val milestones = listOf(1, 7, 14, 30, 60, 90, 180, 365).filter { it <= records.size }
        milestones.forEach { mDay ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                coroutineScope.launch {
                  val targetIndex = (mDay - 1).coerceIn(0, records.size - 1)
                  listState.animateScrollToItem(targetIndex)
                  onSelectScrubDay(mDay)
                }
              }
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "দিন $mDay" else "Day $mDay",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandAccentBlue
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Table Header Row
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = Strings.dayHeader(language),
            modifier = Modifier.weight(0.7f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandMuted),
            textAlign = TextAlign.Start
          )
          Text(
            text = Strings.openingHeader(language),
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandMuted),
            textAlign = TextAlign.End
          )
          Text(
            text = Strings.rewardHeader(language),
            modifier = Modifier.weight(1.1f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandMuted),
            textAlign = TextAlign.End
          )
          Text(
            text = Strings.closingHeader(language),
            modifier = Modifier.weight(1.4f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandMuted),
            textAlign = TextAlign.End
          )
        }
      }

      // Scrollable Rows
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp)
          .border(1.dp, BrandLine, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
      ) {
        if (filteredRecords.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "কোনো দিন পাওয়া যায়নি" else "No day matched",
              style = MaterialTheme.typography.bodyMedium.copy(color = BrandMuted)
            )
          }
        } else {
          LazyColumn(
            state = listState,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("daily_table_list")
          ) {
            items(filteredRecords, key = { it.day }) { rec ->
              TableRowItem(
                record = rec,
                currency = currency,
                onClick = { onSelectScrubDay(rec.day) }
              )
              Divider(color = BrandLine.copy(alpha = 0.5f), thickness = 0.8.dp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TableRowItem(
  record: DailyRecord,
  currency: String,
  onClick: () -> Unit
) {
  val isZebra = record.day % 2 == 0
  val isMilestone = record.day in listOf(7, 14, 30, 60, 90, 180, 365)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        if (isMilestone) BrandAccentBlue.copy(alpha = 0.08f)
        else if (isZebra) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        else Color.Transparent
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 9.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "${record.day}",
      modifier = Modifier.weight(0.7f),
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = if (isMilestone) FontWeight.Bold else FontWeight.Normal,
        color = if (isMilestone) BrandAccentBlue else MaterialTheme.colorScheme.onSurface
      ),
      textAlign = TextAlign.Start
    )
    Text(
      text = RewardCompoundEngine.formatMoney(record.opening, ""),
      modifier = Modifier.weight(1.3f),
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
      textAlign = TextAlign.End,
      maxLines = 1
    )
    Text(
      text = "+${RewardCompoundEngine.formatMoney(record.reward, "")}",
      modifier = Modifier.weight(1.1f),
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = ProfitGreen
      ),
      textAlign = TextAlign.End,
      maxLines = 1
    )
    Text(
      text = RewardCompoundEngine.formatMoney(record.closing, ""),
      modifier = Modifier.weight(1.4f),
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface
      ),
      textAlign = TextAlign.End,
      maxLines = 1
    )
  }
}
