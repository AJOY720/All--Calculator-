package com.example.ui.normalcalc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.StandardCalculatorEngine
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

@Composable
fun ItemizedTallyDialog(
  state: NormalCalcUiState,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onEditToken: (NumberToken) -> Unit,
  onDeleteToken: (NumberToken) -> Unit,
  onCopyReceipt: () -> Unit
) {
  val isBn = language == AppLanguage.BENGALI
  val tokens = state.numberTokens
  val stats = state.stats

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onCopyReceipt,
        colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(Strings.copyReceiptButton(language), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(Strings.closeButton(language), color = BrandMuted)
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
            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = Strings.itemizedListTitle(language),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
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
          .heightIn(max = 480.dp)
      ) {
        // Prominent Total Sum Banner
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = BrandAccentBlue.copy(alpha = 0.12f)),
          border = BorderStroke(1.dp, BrandAccentBlue.copy(alpha = 0.35f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = Strings.totalSumLabel(language),
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = BrandAccentBlue,
                  fontSize = 13.sp
                )
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandAccentBlue
              ) {
                Text(
                  text = if (isBn) "${StandardCalculatorEngine.toBengaliDigits("${tokens.size}")} টি সংখ্যা" else "${tokens.size} items",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "= ",
                style = MaterialTheme.typography.headlineMedium.copy(
                  color = BrandAccentBlue,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp
                )
              )
              Text(
                text = if (isBn) state.runningSumBengali.ifBlank { state.resultPreview } else state.resultPreview,
                style = MaterialTheme.typography.headlineMedium.copy(
                  color = MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 26.sp
                )
              )
              if (isBn && state.resultPreview != state.runningSumBengali) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "(${state.resultPreview})",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = BrandMuted,
                    fontSize = 14.sp
                  ),
                  modifier = Modifier.padding(bottom = 2.dp)
                )
              }
            }

            if (state.amountInWords.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${Strings.amountInWordsLabel(language)} ${state.amountInWords}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = BrandMuted,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }

        // Quick Stats Strip
        if (stats != null && stats.count >= 2) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = Strings.averageLabel(language),
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = BrandMuted)
                )
                val avgFormatted = StandardCalculatorEngine.formatDisplay(stats.average)
                Text(
                  text = if (isBn) StandardCalculatorEngine.toBengaliDigits(avgFormatted) else avgFormatted,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = Strings.maxLabel(language),
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = BrandMuted)
                )
                val maxFormatted = StandardCalculatorEngine.formatDisplay(stats.max)
                Text(
                  text = if (isBn) StandardCalculatorEngine.toBengaliDigits(maxFormatted) else maxFormatted,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF2E7D32))
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = Strings.minLabel(language),
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = BrandMuted)
                )
                val minFormatted = StandardCalculatorEngine.formatDisplay(stats.min)
                Text(
                  text = if (isBn) StandardCalculatorEngine.toBengaliDigits(minFormatted) else minFormatted,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFD32F2F))
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = BrandLine.copy(alpha = 0.5f), thickness = 0.8.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Itemized List with Running Totals
        var currentRunningTotal = 0.0
        val runningTotals = tokens.map { token ->
          val num = StandardCalculatorEngine.toEnglishDigits(token.value).toDoubleOrNull() ?: 0.0
          currentRunningTotal += num
          currentRunningTotal
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          itemsIndexed(tokens) { index, token ->
            val runTotal = runningTotals.getOrElse(index) { 0.0 }
            val runTotalFormatted = StandardCalculatorEngine.formatDisplay(runTotal)

            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
              border = BorderStroke(0.6.dp, BrandLine.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  // Serial badge
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandAccentBlue.copy(alpha = 0.15f),
                    modifier = Modifier.size(24.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = if (isBn) StandardCalculatorEngine.toBengaliDigits("${index + 1}") else "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = BrandAccentBlue,
                          fontSize = 11.sp
                        )
                      )
                    }
                  }

                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = if (isBn) StandardCalculatorEngine.toBengaliDigits(token.value) else token.value,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    )
                    Text(
                      text = "${Strings.runningSumLabel(language)}: " +
                        if (isBn) StandardCalculatorEngine.toBengaliDigits(runTotalFormatted) else runTotalFormatted,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = BrandMuted,
                        fontSize = 11.sp
                      )
                    )
                  }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(
                    onClick = { onEditToken(token) },
                    modifier = Modifier.size(28.dp).testTag("dialog_edit_token_${token.id}")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Edit",
                      tint = BrandAccentBlue,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  IconButton(
                    onClick = { onDeleteToken(token) },
                    modifier = Modifier.size(28.dp).testTag("dialog_delete_token_${token.id}")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete",
                      tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                      modifier = Modifier.size(16.dp)
                    )
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
