package com.example.ui.agecalc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.calculator.AgeCalculatorEngine
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue

@Composable
fun SimpleDatePickerDialog(
  title: String,
  initialYear: Int,
  initialMonth: Int, // 1-12
  initialDay: Int,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onConfirm: (year: Int, month: Int, day: Int) -> Unit
) {
  var dayText by remember { mutableStateOf(initialDay.toString()) }
  var monthText by remember { mutableStateOf(initialMonth.toString()) }
  var yearText by remember { mutableStateOf(initialYear.toString()) }
  var errorText by remember { mutableStateOf<String?>(null) }
  val isBn = language == AppLanguage.BENGALI

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column {
        if (errorText != null) {
          Text(
            text = errorText ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Day (১-৩১)
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isBn) "দিন (Day)" else "Day",
              style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = dayText,
              onValueChange = { if (it.length <= 2) dayText = it },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("picker_day_input")
            )
          }

          // Month (১-১২)
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isBn) "মাস (Month)" else "Month",
              style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = monthText,
              onValueChange = { if (it.length <= 2) monthText = it },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("picker_month_input")
            )
          }

          // Year (১৯০০-২১০০)
          Column(modifier = Modifier.weight(1.3f)) {
            Text(
              text = if (isBn) "বছর (Year)" else "Year",
              style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = yearText,
              onValueChange = { if (it.length <= 4) yearText = it },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("picker_year_input")
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val d = dayText.toIntOrNull()
          val m = monthText.toIntOrNull()
          val y = yearText.toIntOrNull()

          if (d == null || m == null || y == null || m !in 1..12 || y !in 1800..2200) {
            errorText = if (isBn) "সঠিক দিন, মাস ও বছর দিন" else "Please enter valid date"
            return@Button
          }
          val maxDays = AgeCalculatorEngine.daysInMonth(y, m)
          if (d !in 1..maxDays) {
            errorText = if (isBn) "এই মাসে সর্বোচ্চ $maxDays দিন রয়েছে" else "Month has max $maxDays days"
            return@Button
          }

          onConfirm(y, m, d)
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
        modifier = Modifier.testTag("picker_confirm_button")
      ) {
        Text(if (isBn) "ঠিক আছে" else "OK")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBn) "বাতিল" else "Cancel")
      }
    }
  )
}
