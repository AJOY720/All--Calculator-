package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.RewardCompoundEngine
import com.example.data.model.AppLanguage
import com.example.data.model.CalculationResult
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.util.Strings
import kotlin.math.roundToInt

@Composable
fun ProjectionChart(
  result: CalculationResult?,
  language: AppLanguage,
  selectedDay: Int?,
  onSelectDay: (Int?) -> Unit,
  modifier: Modifier = Modifier
) {
  if (result == null || result.dailyRecords.isEmpty()) return

  val records = result.dailyRecords
  val currency = result.input.currency

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = Strings.chartTitle(language),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        // Legend
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = BrandAccentBlue,
              modifier = Modifier.size(8.dp)
            ) {}
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (result.input.isCompound) "Compound" else "Total",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = BrandMuted)
            )
          }

          if (result.input.isCompound) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = CircleShape,
                color = WarningAmber,
                modifier = Modifier.size(8.dp)
              ) {}
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Simple",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = BrandMuted)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Scrubber info banner
      val scrubRecord = selectedDay?.let { day -> records.getOrNull(day - 1) }
        ?: records.lastOrNull()

      if (scrubRecord != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${Strings.dayHeader(language)} ${scrubRecord.day}/${records.size}",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandAccentBlue
              )
            )
            Text(
              text = "${Strings.closingHeader(language)}: ${RewardCompoundEngine.formatMoney(scrubRecord.closing, currency)}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
            Text(
              text = "+${RewardCompoundEngine.formatMoney(scrubRecord.reward, currency)}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ProfitGreen
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Canvas Plot
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        val minVal = (result.initialPrincipal * 0.95).coerceAtLeast(0.0)
        val maxVal = (result.finalBalance * 1.05).coerceAtLeast(minVal + 1.0)
        val valRange = (maxVal - minVal).coerceAtLeast(1.0)

        val totalDays = records.size

        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .pointerInput(totalDays) {
              detectTapGestures(
                onTap = { offset ->
                  val dayIndex = ((offset.x / size.width) * totalDays).roundToInt()
                  val clampedDay = dayIndex.coerceIn(1, totalDays)
                  onSelectDay(clampedDay)
                }
              )
            }
            .pointerInput(totalDays) {
              detectDragGestures(
                onDrag = { change, _ ->
                  val dayIndex = ((change.position.x / size.width) * totalDays).roundToInt()
                  val clampedDay = dayIndex.coerceIn(1, totalDays)
                  onSelectDay(clampedDay)
                },
                onDragEnd = { /* Keep last scrubbed day */ }
              )
            }
        ) {
          val canvasW = size.width
          val canvasH = size.height
          val paddingBottom = 20.dp.toPx()
          val chartH = canvasH - paddingBottom

          // Draw grid horizontal lines
          val gridLines = 4
          for (i in 0..gridLines) {
            val y = chartH * (i / gridLines.toFloat())
            drawLine(
              color = BrandLine.copy(alpha = 0.5f),
              start = Offset(0f, y),
              end = Offset(canvasW, y),
              strokeWidth = 1.dp.toPx()
            )
          }

          if (records.size < 2) return@Canvas

          // If compound, draw simple comparison curve first
          if (result.input.isCompound) {
            val simplePath = Path()
            records.forEachIndexed { index, rec ->
              val x = (index.toFloat() / (records.size - 1)) * canvasW
              val norm = ((rec.simpleComparisonClosing - minVal) / valRange).coerceIn(0.0, 1.0).toFloat()
              val y = chartH - (norm * chartH)
              if (index == 0) simplePath.moveTo(x, y) else simplePath.lineTo(x, y)
            }
            drawPath(
              path = simplePath,
              color = WarningAmber.copy(alpha = 0.8f),
              style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
              )
            )
          }

          // Main Curve Path
          val mainPath = Path()
          val fillPath = Path()

          records.forEachIndexed { index, rec ->
            val x = (index.toFloat() / (records.size - 1)) * canvasW
            val norm = ((rec.closing - minVal) / valRange).coerceIn(0.0, 1.0).toFloat()
            val y = chartH - (norm * chartH)
            if (index == 0) {
              mainPath.moveTo(x, y)
              fillPath.moveTo(x, chartH)
              fillPath.lineTo(x, y)
            } else {
              mainPath.lineTo(x, y)
              fillPath.lineTo(x, y)
            }
          }

          fillPath.lineTo(canvasW, chartH)
          fillPath.close()

          // Draw Gradient fill
          drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                BrandAccentBlue.copy(alpha = 0.28f),
                BrandAccentBlue.copy(alpha = 0.02f)
              ),
              startY = 0f,
              endY = chartH
            )
          )

          // Draw main curve stroke
          drawPath(
            path = mainPath,
            color = BrandAccentBlue,
            style = Stroke(
              width = 3.dp.toPx(),
              cap = StrokeCap.Round
            )
          )

          // Draw scrub indicator line if a day is selected
          val activeDay = selectedDay ?: records.size
          val activeIndex = (activeDay - 1).coerceIn(0, records.size - 1)
          val scrubX = (activeIndex.toFloat() / (records.size - 1)) * canvasW
          val scrubRec = records[activeIndex]
          val normScrub = ((scrubRec.closing - minVal) / valRange).coerceIn(0.0, 1.0).toFloat()
          val scrubY = chartH - (normScrub * chartH)

          drawLine(
            color = BrandAccentBlue.copy(alpha = 0.6f),
            start = Offset(scrubX, 0f),
            end = Offset(scrubX, chartH),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
          )

          // Draw glowing point at scrub location
          drawCircle(
            color = BrandAccentBlue.copy(alpha = 0.3f),
            radius = 8.dp.toPx(),
            center = Offset(scrubX, scrubY)
          )
          drawCircle(
            color = BrandAccentBlue,
            radius = 5.dp.toPx(),
            center = Offset(scrubX, scrubY)
          )
          drawCircle(
            color = Color.White,
            radius = 2.5.dp.toPx(),
            center = Offset(scrubX, scrubY)
          )
        }
      }

      // X-axis day indicators
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Day 1",
          style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted, fontSize = 11.sp)
        )
        if (records.size > 2) {
          Text(
            text = "Day ${records.size / 2}",
            style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted, fontSize = 11.sp)
          )
        }
        Text(
          text = "Day ${records.size}",
          style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted, fontSize = 11.sp)
        )
      }
    }
  }
}
