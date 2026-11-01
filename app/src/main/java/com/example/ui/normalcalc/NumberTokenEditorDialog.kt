package com.example.ui.normalcalc

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

/**
 * Dialog allowing user to directly edit a specific number in a chained equation.
 * E.g. in "150 + 230 + 300 = 680", user can modify the first "150" to "180", "15", or "0".
 */
@Composable
fun NumberTokenEditorDialog(
  token: NumberToken,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onUpdateValue: (String) -> Unit
) {
  var editingValue by remember { mutableStateOf(token.value) }
  val isBn = language == AppLanguage.BENGALI

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
        .testTag("number_token_editor_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = BrandAccentBlue.copy(alpha = 0.12f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = null,
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "${Strings.editNumberTitle(language)} (#${if (isBn) toBn(token.id) else token.id})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
              Text(
                text = Strings.editNumberSubtitle(language),
                style = MaterialTheme.typography.bodySmall.copy(
                  color = BrandMuted,
                  fontSize = 12.sp
                )
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = BrandMuted,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current Value vs New Value Indicator
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Strings.originalNumber(language),
              style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted, fontSize = 12.sp)
            )
            Text(
              text = token.value,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandAccentBlue,
                fontSize = 15.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Field for Editing the Number
        OutlinedTextField(
          value = editingValue,
          onValueChange = { input ->
            // Allow only digits and decimal
            if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1) {
              editingValue = input
            }
          },
          label = { Text(Strings.newNumber(language)) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandAccentBlue,
            focusedLabelColor = BrandAccentBlue
          ),
          textStyle = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 22.sp
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("number_token_input_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Preset Adjustments (+10, -10, +50, -50, Set to 0)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val numVal = editingValue.toDoubleOrNull() ?: 0.0

          QuickAdjustButton(text = "০ (0)", modifier = Modifier.weight(1f)) {
            editingValue = "0"
          }
          QuickAdjustButton(text = "-১০", modifier = Modifier.weight(1f)) {
            val res = (numVal - 10).coerceAtLeast(0.0)
            editingValue = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
          }
          QuickAdjustButton(text = "+১০", modifier = Modifier.weight(1f)) {
            val res = numVal + 10
            editingValue = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
          }
          QuickAdjustButton(text = "+৫০", modifier = Modifier.weight(1f)) {
            val res = numVal + 50
            editingValue = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons (Cancel / Update)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text(
              text = if (isBn) "বাতিল" else "Cancel",
              style = MaterialTheme.typography.labelLarge.copy(color = BrandMuted)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = {
              if (editingValue.isNotBlank()) {
                onUpdateValue(editingValue)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("number_token_update_button")
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = Strings.updateNumberButton(language),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuickAdjustButton(
  text: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = modifier
  ) {
    TextButton(
      onClick = onClick,
      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = BrandAccentBlue,
          fontSize = 11.sp
        )
      )
    }
  }
}

private fun toBn(num: Int): String {
  val bnDigits = listOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
  return num.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
}
