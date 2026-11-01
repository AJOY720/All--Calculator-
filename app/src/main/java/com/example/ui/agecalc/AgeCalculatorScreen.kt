package com.example.ui.agecalc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.theme.MetricBlueContainer
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.util.Strings
import java.text.DecimalFormat

@Composable
fun AgeCalculatorScreen(
  viewModel: AgeCalculatorViewModel,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()
  val isBn = language == AppLanguage.BENGALI

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 760.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Screen Title
      Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
          text = Strings.ageCalcTitle(language),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        )
        Text(
          text = Strings.ageCalcSubtitle(language),
          style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted)
        )
      }

      // Input Card: DOB & Target Date
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Date of Birth Selector Tile
          Text(
            text = Strings.dobLabel(language),
            style = MaterialTheme.typography.labelMedium.copy(
              color = BrandMuted,
              fontWeight = FontWeight.SemiBold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandLine),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { viewModel.openBirthDatePicker() }
              .testTag("dob_selector_tile")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Cake,
                  contentDescription = null,
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = formatFullDate(state.birthYear, state.birthMonth, state.birthDay, isBn),
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
              Icon(
                imageVector = Icons.Default.EditCalendar,
                contentDescription = "Edit",
                tint = BrandAccentBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Target Date Selector Tile
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Strings.targetDateLabel(language),
              style = MaterialTheme.typography.labelMedium.copy(
                color = BrandMuted,
                fontWeight = FontWeight.SemiBold
              )
            )
            TextButton(
              onClick = { viewModel.setTargetToToday() },
              modifier = Modifier.testTag("today_button")
            ) {
              Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isBn) "আজকের দিন" else "Today",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandLine),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { viewModel.openTargetDatePicker() }
              .testTag("target_date_selector_tile")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = formatFullDate(state.targetYear, state.targetMonth, state.targetDay, isBn),
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
              Icon(
                imageVector = Icons.Default.EditCalendar,
                contentDescription = "Edit",
                tint = BrandAccentBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Calculate Button
          Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("calculate_age_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue)
          ) {
            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = Strings.calculateAgeButton(language),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      val res = state.result
      if (res != null) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Exact Age Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = Strings.exactAgeTitle(language),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Big Pill Counters: Years, Months, Days
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AgeHeroPill(
                value = res.years.toString(),
                label = Strings.yearsLabel(language),
                modifier = Modifier.weight(1f)
              )
              AgeHeroPill(
                value = res.months.toString(),
                label = Strings.monthsLabel(language),
                modifier = Modifier.weight(1f)
              )
              AgeHeroPill(
                value = res.days.toString(),
                label = Strings.daysUnit(language),
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Next Birthday Banner
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = ProfitGreenContainer,
              border = BorderStroke(1.dp, ProfitGreen.copy(alpha = 0.3f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = CircleShape,
                  color = ProfitGreen.copy(alpha = 0.2f),
                  modifier = Modifier.size(36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Cake,
                      contentDescription = null,
                      tint = ProfitGreen,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = Strings.nextBirthdayTitle(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = ProfitGreen
                    )
                  )
                  val weekdayStr = if (isBn) res.nextBirthdayWeekdayBn else res.nextBirthdayWeekdayEn
                  val bdayRemainingText = if (isBn) {
                    "আর ${res.nextBirthdayMonths} মাস ${res.nextBirthdayDays} দিন বাকি ($weekdayStr)"
                  } else {
                    "in ${res.nextBirthdayMonths} months & ${res.nextBirthdayDays} days ($weekdayStr)"
                  }
                  Text(
                    text = bdayRemainingText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = ProfitGreen
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: Copy & Share
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { viewModel.copyAgeSummary(language) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("copy_age_button"),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = Strings.copySummaryButton(language),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
              }

              OutlinedButton(
                onClick = { viewModel.shareAgeSummary(language) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("share_age_button"),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = Strings.shareSummaryButton(language),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Life Statistics Grid
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = Strings.lifeStatsTitle(language),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            val numFormat = DecimalFormat("#,###")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                StatSmallCard(
                  title = Strings.totalMonths(language),
                  value = numFormat.format(res.totalMonths),
                  modifier = Modifier.weight(1f)
                )
                StatSmallCard(
                  title = Strings.totalWeeks(language),
                  value = numFormat.format(res.totalWeeks),
                  modifier = Modifier.weight(1f)
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                StatSmallCard(
                  title = Strings.totalDays(language),
                  value = numFormat.format(res.totalDays),
                  modifier = Modifier.weight(1f)
                )
                StatSmallCard(
                  title = Strings.totalHours(language),
                  value = numFormat.format(res.totalHours),
                  modifier = Modifier.weight(1f)
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                StatSmallCard(
                  title = Strings.totalMinutes(language),
                  value = numFormat.format(res.totalMinutes),
                  modifier = Modifier.weight(1f)
                )
                StatSmallCard(
                  title = if (isBn) "মোট সেকেন্ড" else "Total Seconds",
                  value = numFormat.format(res.totalSeconds),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming 5 Birthdays
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = Strings.upcomingBirthdaysTitle(language),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            res.upcomingBirthdays.forEachIndexed { idx, ub ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${ub.year} (${state.birthDay}/${state.birthMonth})",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                val wName = if (isBn) ub.weekdayBn else ub.weekdayEn
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = wName,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = BrandAccentBlue
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
              if (idx < res.upcomingBirthdays.size - 1) {
                Divider(color = BrandLine.copy(alpha = 0.5f), thickness = 0.8.dp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zodiac Sign Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = BrandAccentBlue.copy(alpha = 0.15f),
              modifier = Modifier.size(52.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = res.zodiacSymbol,
                  style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp)
                )
              }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = Strings.zodiacTitle(language),
                style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted)
              )
              Text(
                text = "${if (isBn) res.zodiacSignBn else res.zodiacSignEn} (${res.zodiacSignEn})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
              Text(
                text = if (isBn) res.zodiacTraitsBn else res.zodiacTraitsEn,
                style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted, fontSize = 12.sp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Pickers
  if (state.showBirthDatePicker) {
    SimpleDatePickerDialog(
      title = Strings.dobLabel(language),
      initialYear = state.birthYear,
      initialMonth = state.birthMonth,
      initialDay = state.birthDay,
      language = language,
      onDismiss = { viewModel.closeBirthDatePicker() },
      onConfirm = { y, m, d -> viewModel.onBirthDateSelected(y, m, d) }
    )
  }

  if (state.showTargetDatePicker) {
    SimpleDatePickerDialog(
      title = Strings.targetDateLabel(language),
      initialYear = state.targetYear,
      initialMonth = state.targetMonth,
      initialDay = state.targetDay,
      language = language,
      onDismiss = { viewModel.closeTargetDatePicker() },
      onConfirm = { y, m, d -> viewModel.onTargetDateSelected(y, m, d) }
    )
  }
}

@Composable
private fun AgeHeroPill(
  value: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = MetricBlueContainer
  ) {
    Column(
      modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          color = BrandAccentBlue,
          fontSize = 28.sp
        )
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          color = BrandMuted
        )
      )
    }
  }
}

@Composable
private fun StatSmallCard(
  title: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted, fontSize = 11.sp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        ),
        maxLines = 1
      )
    }
  }
}

private fun formatFullDate(year: Int, month: Int, day: Int, isBn: Boolean): String {
  val monthNamesBn = listOf(
    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
  )
  val monthNamesEn = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  )
  val mName = if (isBn) monthNamesBn.getOrElse(month - 1) { "$month" }
  else monthNamesEn.getOrElse(month - 1) { "$month" }

  return "$day $mName, $year"
}
