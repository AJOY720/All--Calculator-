package com.example.ui.normalcalc

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.HistoryDateFormatter
import com.example.ui.util.Strings

fun countNumbersInExpression(expr: String): Int {
  if (expr.isBlank()) return 0
  val clean = expr.replace(",", "")
  val regex = Regex("""\d+(\.\d+)?""")
  return regex.findAll(clean).count()
}

fun extractNumbersFromExpression(expr: String): List<String> {
  if (expr.isBlank()) return emptyList()
  val clean = expr.replace(",", "")
  val regex = Regex("""\d+(\.\d+)?""")
  return regex.findAll(clean).map { it.value }.toList()
}

@Composable
fun FullHistoryDetailDialog(
  item: NormalCalcHistoryItem,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onLoadExpression: () -> Unit,
  onLoadResult: () -> Unit,
  onCopy: () -> Unit
) {
  val isBn = language == AppLanguage.BENGALI
  val count = remember(item.expression) { countNumbersInExpression(item.expression) }
  val numbers = remember(item.expression) { extractNumbersFromExpression(item.expression) }
  var isSingleLine by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(onClick = onLoadExpression) {
        Text(Strings.loadExpression(language))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(Strings.closeButton(language))
      }
    },
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (count >= 3) Strings.numbersCountBadge(count, language) else Strings.historyTitle(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
          )
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = BrandMuted, modifier = Modifier.size(18.dp))
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Date & Time Information Card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
          border = BorderStroke(1.dp, BrandLine.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = Strings.calculationDateLabel(language),
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                  )
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandAccentBlue.copy(alpha = 0.12f)
              ) {
                Text(
                  text = HistoryDateFormatter.getRelativeTimeSpan(item.timestamp, language),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandAccentBlue,
                    fontSize = 10.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = HistoryDateFormatter.formatDateTime(item.timestamp, language),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
              )
            )
          }
        }

        // Calculation Result Card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = BrandAccentBlue.copy(alpha = 0.1f),
          border = BorderStroke(1.dp, BrandAccentBlue.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isBn) "মোট যোগফল:" else "Total Result:",
                style = MaterialTheme.typography.labelMedium.copy(color = BrandMuted, fontSize = 12.sp)
              )
              Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                  text = "= ${item.result}",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandAccentBlue,
                    fontSize = 22.sp
                  )
                )
              }
            }
            IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandAccentBlue, modifier = Modifier.size(18.dp))
            }
          }
        }

        // View Mode Toggle & Hint
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = Strings.scrollTip(language),
            style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted, fontSize = 11.sp)
          )

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (!isSingleLine) BrandAccentBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { isSingleLine = false }
            ) {
              Text(
                text = Strings.wrapMode(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (!isSingleLine) BrandAccentBlue else BrandMuted,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSingleLine) BrandAccentBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { isSingleLine = true }
            ) {
              Text(
                text = Strings.singleLineMode(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSingleLine) BrandAccentBlue else BrandMuted,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }

        // Deep Scrollable Container for the complete 50-number chain
        if (!isSingleLine) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 100.dp, max = 200.dp)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .border(BorderStroke(1.dp, BrandLine.copy(alpha = 0.6f)), RoundedCornerShape(8.dp))
              .verticalScroll(rememberScrollState())
              .padding(10.dp)
          ) {
            Text(
              text = item.expression,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .border(BorderStroke(1.dp, BrandLine.copy(alpha = 0.6f)), RoundedCornerShape(8.dp))
              .horizontalScroll(rememberScrollState())
              .padding(10.dp)
          ) {
            Text(
              text = item.expression,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              ),
              maxLines = 1,
              softWrap = false
            )
          }
        }

        // Itemized breakdown of the numbers if count >= 3
        if (numbers.isNotEmpty()) {
          Text(
            text = Strings.itemizedNumbersTitle(language),
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 12.sp
            )
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(0.8.dp, BrandLine.copy(alpha = 0.4f)),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 80.dp, max = 180.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              numbers.chunked(2).forEachIndexed { rowIndex, pair ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  val idx1 = rowIndex * 2 + 1
                  val num1 = pair[0]
                  ItemNumberChip(index = idx1, number = num1, isBn = isBn, modifier = Modifier.weight(1f))

                  if (pair.size > 1) {
                    val idx2 = rowIndex * 2 + 2
                    val num2 = pair[1]
                    ItemNumberChip(index = idx2, number = num2, isBn = isBn, modifier = Modifier.weight(1f))
                  } else {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            }
          }
        }
      }
    }
  )
}

@Composable
fun ItemNumberChip(
  index: Int,
  number: String,
  isBn: Boolean,
  modifier: Modifier = Modifier
) {
  val bnDigits = listOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
  val indexStr = if (isBn) {
    index.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
  } else {
    index.toString()
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
    border = BorderStroke(0.6.dp, BrandLine.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "#$indexStr",
        style = MaterialTheme.typography.labelSmall.copy(
          color = BrandAccentBlue,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      )
      Text(
        text = number,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
    }
  }
}
