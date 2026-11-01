package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.RewardCompoundEngine
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationResult
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

@Composable
fun ResultMetricsCard(
  result: CalculationResult?,
  language: AppLanguage,
  onCopySummary: () -> Unit,
  onShareSummary: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(18.dp)
    ) {
      Text(
        text = Strings.resultsTitle(language),
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      val curr = result?.input?.currency ?: "৳"
      val finalVal = result?.let { RewardCompoundEngine.formatMoney(it.finalBalance, curr) } ?: "—"
      val profitVal = result?.let { RewardCompoundEngine.formatMoney(it.totalProfit, curr) } ?: "—"
      val startVal = result?.let { RewardCompoundEngine.formatMoney(it.initialPrincipal, curr) } ?: "—"
      val returnVal = result?.let { RewardCompoundEngine.formatPercent(it.totalReturnPercent) } ?: "—"

      // 2x2 Results Grid
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Row 1: Ending Balance & Total Profit (Highlight)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            title = Strings.finalBalance(language),
            value = finalVal,
            isHighlight = false,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_final_balance")
          )

          MetricBox(
            title = Strings.totalProfit(language),
            value = profitVal,
            isHighlight = true,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_total_profit")
          )
        }

        // Row 2: Starting Capital & Total Return %
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            title = Strings.initialPrincipal(language),
            value = startVal,
            isHighlight = false,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_initial_principal")
          )

          MetricBox(
            title = Strings.totalReturn(language),
            value = returnVal,
            isHighlight = false,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_total_return")
          )
        }
      }

      // Extra Compounding Advantage Callout if compound enabled
      if (result != null && result.input.isCompound && result.compoundDifference > 0.05) {
        Spacer(modifier = Modifier.height(12.dp))
        val isDark = isSystemInDarkTheme()
        val bannerBg = if (isDark) Color(0xFF0D3327) else Color(0xFFE8F7F0)
        val bannerBorder = if (isDark) Color(0xFF1E5B45) else Color(0xFFB5EAD0)
        val bannerText = if (isDark) Color(0xFF26E3A6) else Color(0xFF087F5B)

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = bannerBg,
          border = BorderStroke(1.dp, bannerBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = bannerText,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = Strings.compoundBoost(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = bannerText,
                  fontWeight = FontWeight.Bold
                )
              )
              val diffFormatted = RewardCompoundEngine.formatMoney(result.compoundDifference, curr)
              val percentMore = if (result.simpleProfit > 0) {
                ((result.compoundDifference / result.simpleProfit) * 100).toInt()
              } else 0
              Text(
                text = "+$diffFormatted" + if (percentMore > 0) " (${percentMore}% বেশি লাভ)" else "",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = bannerText,
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }
          }
        }
      }

      // Summary Sentence matching HTML
      if (result != null) {
        Spacer(modifier = Modifier.height(10.dp))
        val modeStr = if (result.input.isCompound) {
          if (language == AppLanguage.BENGALI) "কম্পাউন্ড" else "compounded"
        } else {
          if (language == AppLanguage.BENGALI) "কম্পাউন্ড ছাড়া" else "non-compounded (simple)"
        }
        val rateFmt = RewardCompoundEngine.formatMoney(result.input.dailyRatePercent, "").trim()
        val summaryText = if (language == AppLanguage.BENGALI) {
          "${result.input.days} দিনে $modeStr হিসাব। দৈনিক হার $rateFmt%। দৈনিক গড় লাভ ${RewardCompoundEngine.formatMoney(result.dailyAverageReward, curr)}।"
        } else {
          "${result.input.days} days $modeStr projection. Daily rate $rateFmt%. Daily average profit: ${RewardCompoundEngine.formatMoney(result.dailyAverageReward, curr)}."
        }

        Text(
          text = summaryText,
          style = MaterialTheme.typography.bodySmall.copy(
            color = BrandMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp
          ),
          modifier = Modifier.testTag("result_summary_text")
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action row: Copy & Share
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onCopySummary,
          modifier = Modifier
            .weight(1f)
            .testTag("copy_summary_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = BrandAccentBlue
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = Strings.copySummaryButton(language),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        OutlinedButton(
          onClick = onShareSummary,
          modifier = Modifier
            .weight(1f)
            .testTag("share_summary_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = BrandAccentBlue
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = Strings.shareSummaryButton(language),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }
    }
  }
}

@Composable
private fun MetricBox(
  title: String,
  value: String,
  isHighlight: Boolean,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()

  // Background color - theme adaptive
  val bgColor = when {
    isHighlight && isDark -> Color(0xFF0D3327)
    isHighlight -> Color(0xFFE8F7F0)
    isDark -> Color(0xFF16253B)
    else -> Color(0xFFEDF3FC)
  }

  // Border color - crisp outline
  val borderColor = when {
    isHighlight && isDark -> Color(0xFF1E5B45)
    isHighlight -> Color(0xFFB5EAD0)
    isDark -> Color(0xFF2B4265)
    else -> Color(0xFFD3E2F8)
  }

  // Primary value text color - high contrast bold text!
  val valueColor = when {
    isHighlight && isDark -> Color(0xFF26E3A6)
    isHighlight -> Color(0xFF087F5B)
    isDark -> Color(0xFFFFFFFF)
    else -> Color(0xFF123B72)
  }

  // Label title text color
  val labelColor = when {
    isHighlight && isDark -> Color(0xFFA5E6CE)
    isHighlight -> Color(0xFF087F5B).copy(alpha = 0.85f)
    isDark -> Color(0xFF9FB2CA)
    else -> Color(0xFF5A6F8D)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = labelColor,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = valueColor
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
