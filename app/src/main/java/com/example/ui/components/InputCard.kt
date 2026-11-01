package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.SupportedCurrencies
import com.example.ui.CalculatorUiState
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

@Composable
fun InputCard(
  state: CalculatorUiState,
  onPrincipalChange: (String) -> Unit,
  onRateChange: (String) -> Unit,
  onDaysChange: (String) -> Unit,
  onCurrencyChange: (String) -> Unit,
  onCompoundToggled: (Boolean) -> Unit,
  onDepositSeparateToggled: (Boolean) -> Unit,
  onDailyAdditionChange: (String) -> Unit,
  onToggleAdvanced: () -> Unit,
  onAddPrincipal: (Double) -> Unit,
  onApplyPresetRate: (Double) -> Unit,
  onApplyPresetDays: (Int) -> Unit,
  onCalculate: () -> Unit,
  onDownloadCsv: () -> Unit,
  onOpenSaveDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val lang = state.language
  var currencyMenuExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(18.dp)
    ) {
      // Card Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = Strings.inputSectionTitle(lang),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        // Save scenario shortcut button
        TextButton(
          onClick = onOpenSaveDialog,
          modifier = Modifier.testTag("save_scenario_header_button")
        ) {
          Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = Strings.saveScenarioButton(lang),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Error message banner if any
      if (state.errorMessage != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Text(
            text = state.errorMessage,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // Input 1: Principal (প্রাথমিক টাকা / মূলধন)
      Text(
        text = Strings.principalLabel(lang),
        style = MaterialTheme.typography.labelMedium.copy(
          color = BrandMuted,
          fontSize = 13.sp
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = state.principalText,
        onValueChange = onPrincipalChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("principal_input"),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Next
        ),
        leadingIcon = {
          Text(
            text = state.currency.ifBlank { "#" },
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = BrandAccentBlue
            ),
            modifier = Modifier.padding(start = 12.dp)
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandAccentBlue,
          unfocusedBorderColor = BrandLine
        )
      )

      // Quick chips for Principal
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp, bottom = 12.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val increments = listOf(1000.0, 5000.0, 10000.0, 50000.0, 100000.0)
        increments.forEach { inc ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onAddPrincipal(inc) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = BrandAccentBlue
              )
              Text(
                text = "${inc.toInt() / 1000}k",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }
        }
      }

      // Input 2: Daily Rate (দৈনিক রিওয়ার্ড (%))
      Text(
        text = Strings.rateLabel(lang),
        style = MaterialTheme.typography.labelMedium.copy(
          color = BrandMuted,
          fontSize = 13.sp
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = state.rateText,
        onValueChange = onRateChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rate_input"),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Next
        ),
        trailingIcon = {
          Text(
            text = "%",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = BrandMuted
            ),
            modifier = Modifier.padding(end = 12.dp)
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandAccentBlue,
          unfocusedBorderColor = BrandLine
        )
      )

      // Quick chips for Rate
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp, bottom = 12.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val ratePresets = listOf(0.5, 1.0, 1.5, 2.0, 3.0, 5.0)
        ratePresets.forEach { r ->
          val isSelected = state.rateText == (if (r % 1.0 == 0.0) r.toInt().toString() else r.toString())
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onApplyPresetRate(r) }
          ) {
            Text(
              text = "$r%",
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }
      }

      // Input 3 & 4 in 2 Columns: Days & Currency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Total Days
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = Strings.daysLabel(lang),
            style = MaterialTheme.typography.labelMedium.copy(
              color = BrandMuted,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = state.daysText,
            onValueChange = onDaysChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("days_input"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Number,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BrandAccentBlue,
              unfocusedBorderColor = BrandLine
            )
          )
        }

        // Currency Selector
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = Strings.currencyLabel(lang),
            style = MaterialTheme.typography.labelMedium.copy(
              color = BrandMuted,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(BorderStroke(1.dp, BrandLine), RoundedCornerShape(10.dp))
                .clickable { currencyMenuExpanded = true }
                .testTag("currency_selector"),
              color = MaterialTheme.colorScheme.surface
            ) {
              Row(
                modifier = Modifier
                  .padding(horizontal = 12.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                val currentOpt = SupportedCurrencies.firstOrNull { it.symbol == state.currency }
                Text(
                  text = currentOpt?.label ?: state.currency.ifBlank { "None" },
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                  maxLines = 1
                )
                Icon(
                  imageVector = Icons.Default.KeyboardArrowDown,
                  contentDescription = null,
                  tint = BrandMuted
                )
              }
            }

            DropdownMenu(
              expanded = currencyMenuExpanded,
              onDismissRequest = { currencyMenuExpanded = false }
            ) {
              SupportedCurrencies.forEach { option ->
                DropdownMenuItem(
                  text = { Text(option.label) },
                  onClick = {
                    onCurrencyChange(option.symbol)
                    currencyMenuExpanded = false
                  },
                  trailingIcon = {
                    if (state.currency == option.symbol) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = BrandAccentBlue)
                    }
                  }
                )
              }
            }
          }
        }
      }

      // Quick Chips for Days
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp, bottom = 12.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val dayPresets = listOf(7, 15, 30, 60, 90, 180, 365)
        dayPresets.forEach { d ->
          val isSelected = state.daysText == d.toString()
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onApplyPresetDays(d) }
          ) {
            Text(
              text = if (lang == AppLanguage.BENGALI) "${d} দিন" else "${d}d",
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Checkbox 1: Compound Daily Rewards
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onCompoundToggled(!state.isCompound) }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = state.isCompound,
          onCheckedChange = onCompoundToggled,
          colors = CheckboxDefaults.colors(checkedColor = BrandAccentBlue),
          modifier = Modifier.testTag("compound_checkbox")
        )
        Text(
          text = Strings.compoundCheck(lang),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            fontWeight = if (state.isCompound) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.padding(start = 4.dp)
        )
      }

      // Checkbox 2: Deposit separate (compounding disabled)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onDepositSeparateToggled(!state.isDepositSeparate) }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = state.isDepositSeparate,
          onCheckedChange = onDepositSeparateToggled,
          colors = CheckboxDefaults.colors(checkedColor = BrandAccentBlue),
          modifier = Modifier.testTag("deposit_separate_checkbox")
        )
        Text(
          text = Strings.simpleCheck(lang),
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            fontWeight = if (state.isDepositSeparate) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.padding(start = 4.dp)
        )
      }

      // Advanced collapsible option: Daily addition
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onToggleAdvanced)
          .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = BrandAccentBlue,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = Strings.advancedOptions(lang),
          style = MaterialTheme.typography.labelMedium.copy(
            color = BrandAccentBlue,
            fontWeight = FontWeight.SemiBold
          ),
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = if (state.showAdvanced) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          tint = BrandAccentBlue
        )
      }

      AnimatedVisibility(visible = state.showAdvanced) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          Text(
            text = Strings.dailyAdditionLabel(lang),
            style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted)
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = state.dailyAdditionText,
            onValueChange = onDailyAdditionChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("daily_addition_input"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = {
              Text(
                text = "+",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = BrandAccentBlue
                ),
                modifier = Modifier.padding(start = 12.dp)
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Button Primary: Calculate
      Button(
        onClick = {
          focusManager.clearFocus()
          onCalculate()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("calculate_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue)
      ) {
        Icon(
          imageVector = Icons.Default.Calculate,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = Strings.calculateButton(lang),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Button Secondary: Download / Share CSV
      OutlinedButton(
        onClick = onDownloadCsv,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("download_button"),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BrandAccentBlue.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = BrandAccentBlue.copy(alpha = 0.08f),
          contentColor = BrandDarkBlue
        )
      ) {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
          tint = BrandAccentBlue
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = Strings.downloadCsvButton(lang),
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = BrandAccentBlue
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Note disclaimer below inputs
      Text(
        text = Strings.disclaimerNote(lang),
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 12.sp,
          lineHeight = 16.sp,
          color = BrandMuted
        )
      )
    }
  }
}
