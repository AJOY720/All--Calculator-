package com.example.ui.loancalc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.data.calculator.LoanCalculatorEngine
import com.example.data.calculator.LoanCategoryPreset
import com.example.data.calculator.LoanInterestType
import com.example.data.calculator.LoanTenureUnit
import com.example.data.calculator.LoanYearSummary
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.theme.MetricBlueContainer
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.util.HistoryDateFormatter
import com.example.ui.util.Strings
import kotlin.math.roundToInt

@Composable
fun LoanCalculatorScreen(
  viewModel: LoanCalculatorViewModel,
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
      // Screen Title & Subtitle
      Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = Strings.loanCalcTitle(language),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 19.sp
            )
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = Strings.loanCalcSubtitle(language),
          style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted)
        )
      }

      // Presets Selector Chips
      PresetChipsRow(
        selectedPreset = state.selectedPreset,
        language = language,
        onPresetSelected = { viewModel.onPresetSelected(it) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Input Form Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Currency Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Strings.currencyLabel(language),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandMuted
              )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("৳", "$", "₹", "€", "£").forEach { curr ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (state.currencySymbol == curr) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.onCurrencyChange(curr) }
                    .testTag("currency_chip_$curr")
                ) {
                  Text(
                    text = curr,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (state.currencySymbol == curr) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 1. Principal Loan Amount
          Text(
            text = Strings.principalAmountLabel(language),
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = BrandMuted
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = state.principalInput,
            onValueChange = { viewModel.onPrincipalChange(it) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("loan_principal_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            leadingIcon = {
              Text(
                text = state.currencySymbol,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = BrandAccentBlue
                ),
                modifier = Modifier.padding(start = 12.dp)
              )
            },
            trailingIcon = {
              if (state.principalInput.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearPrincipal() }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                }
              }
            },
            shape = RoundedCornerShape(12.dp)
          )

          // Quick Principal Increment Chips
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val chips = if (isBn) {
              listOf("+৫০হাজার" to 50000.0, "+১লাখ" to 100000.0, "+৫লাখ" to 500000.0, "+১০লাখ" to 1000000.0, "+২৫লাখ" to 2500000.0)
            } else {
              listOf("+50K" to 50000.0, "+100K" to 100000.0, "+500K" to 500000.0, "+1M" to 1000000.0, "+2.5M" to 2500000.0)
            }
            chips.forEach { (label, amt) ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BrandAccentBlue.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, BrandAccentBlue.copy(alpha = 0.3f)),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { viewModel.addPrincipal(amt) }
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = BrandAccentBlue
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2. Interest Rate (% p.a.)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Strings.interestRateLabel(language),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandMuted
              )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              IconButton(
                onClick = { viewModel.adjustRate(-0.5) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease Rate", tint = BrandAccentBlue)
              }
              IconButton(
                onClick = { viewModel.adjustRate(+0.5) },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = "Increase Rate", tint = BrandAccentBlue)
              }
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = state.interestRateInput,
            onValueChange = { viewModel.onInterestRateChange(it) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("loan_interest_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            leadingIcon = {
              Icon(Icons.Default.Percent, contentDescription = null, tint = BrandMuted, modifier = Modifier.size(18.dp))
            },
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // 3. Loan Tenure with Years / Months Unit Selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = Strings.tenureLabel(language),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandMuted
              )
            )

            // Years vs Months Toggle
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.padding(bottom = 2.dp)
            ) {
              Row {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (state.tenureUnit == LoanTenureUnit.YEARS) BrandAccentBlue else Color.Transparent,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.onTenureUnitChange(LoanTenureUnit.YEARS) }
                ) {
                  Text(
                    text = Strings.yearsLabel(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (state.tenureUnit == LoanTenureUnit.YEARS) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (state.tenureUnit == LoanTenureUnit.MONTHS) BrandAccentBlue else Color.Transparent,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.onTenureUnitChange(LoanTenureUnit.MONTHS) }
                ) {
                  Text(
                    text = Strings.monthsLabel(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (state.tenureUnit == LoanTenureUnit.MONTHS) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = state.tenureInput,
            onValueChange = { viewModel.onTenureChange(it) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("loan_tenure_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            leadingIcon = {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BrandMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              Row(modifier = Modifier.padding(end = 4.dp)) {
                IconButton(
                  onClick = { viewModel.adjustTenure(-1) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "Decrease Tenure", tint = BrandAccentBlue)
                }
                IconButton(
                  onClick = { viewModel.adjustTenure(+1) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Increase Tenure", tint = BrandAccentBlue)
                }
              }
            },
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // 4. Interest Calculation Method (Reducing Balance vs Flat Rate)
          Text(
            text = Strings.interestMethodLabel(language),
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = BrandMuted
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MethodSelectChip(
              text = Strings.reducingBalanceLabel(language),
              isSelected = state.interestType == LoanInterestType.REDUCING_BALANCE,
              modifier = Modifier.weight(1f),
              onClick = { viewModel.onInterestTypeChange(LoanInterestType.REDUCING_BALANCE) }
            )
            MethodSelectChip(
              text = Strings.flatRateLabel(language),
              isSelected = state.interestType == LoanInterestType.FLAT_RATE,
              modifier = Modifier.weight(1f),
              onClick = { viewModel.onInterestTypeChange(LoanInterestType.FLAT_RATE) }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Results Hero Card
      state.result?.let { result ->
        LoanResultHeroCard(
          result = result,
          currencySymbol = state.currencySymbol,
          language = language,
          onCopy = { viewModel.copySummary(language) },
          onShare = { viewModel.shareSummary(language) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prepayment Simulator Card
        PrepaymentSimulatorCard(
          extraMonthlyInput = state.extraMonthlyPaymentInput,
          currencySymbol = state.currencySymbol,
          prepaymentBenefit = result.prepaymentBenefit,
          language = language,
          onValueChange = { viewModel.onExtraMonthlyPaymentChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Repayment Amortization Schedule Card
        AmortizationScheduleCard(
          yearlyList = result.yearlySchedule,
          monthlyList = result.monthlySchedule,
          viewMode = state.scheduleViewMode,
          currencySymbol = state.currencySymbol,
          language = language,
          onViewModeChange = { viewModel.setScheduleViewMode(it) }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PresetChipsRow(
  selectedPreset: LoanCategoryPreset,
  language: AppLanguage,
  onPresetSelected: (LoanCategoryPreset) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    LoanCategoryPreset.values().forEach { preset ->
      val (label, icon) = when (preset) {
        LoanCategoryPreset.HOME -> Strings.homeLoan(language) to Icons.Default.Home
        LoanCategoryPreset.PERSONAL -> Strings.personalLoan(language) to Icons.Default.Person
        LoanCategoryPreset.CAR -> Strings.carLoan(language) to Icons.Default.DirectionsCar
        LoanCategoryPreset.EDUCATION -> Strings.educationLoan(language) to Icons.Default.School
        LoanCategoryPreset.BUSINESS -> Strings.businessLoan(language) to Icons.Default.Work
        LoanCategoryPreset.CUSTOM -> Strings.customLoan(language) to Icons.Default.MonetizationOn
      }
      val isSelected = selectedPreset == preset
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BrandAccentBlue else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isSelected) BrandAccentBlue else BrandLine),
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onPresetSelected(preset) }
          .testTag("loan_preset_${preset.name}")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else BrandAccentBlue,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
          )
        }
      }
    }
  }
}

@Composable
private fun MethodSelectChip(
  text: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) BrandAccentBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) BrandAccentBlue else BrandLine
    ),
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) BrandAccentBlue else MaterialTheme.colorScheme.onSurface,
          fontSize = 11.5.sp
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun LoanResultHeroCard(
  result: com.example.data.calculator.LoanCalculationResult,
  currencySymbol: String,
  language: AppLanguage,
  onCopy: () -> Unit,
  onShare: () -> Unit
) {
  val isBn = language == AppLanguage.BENGALI

  val emiStr = LoanCalculatorEngine.formatCurrency(result.monthlyEmi, currencySymbol)
  val emiExactStr = LoanCalculatorEngine.formatCurrencyWithDecimal(result.monthlyEmi, currencySymbol)
  val totalInterestStr = LoanCalculatorEngine.formatCurrency(result.totalInterest, currencySymbol)
  val totalInterestExactStr = LoanCalculatorEngine.formatCurrencyWithDecimal(result.totalInterest, currencySymbol)
  val totalPaymentStr = LoanCalculatorEngine.formatCurrency(result.totalPayment, currencySymbol)
  val totalPaymentExactStr = LoanCalculatorEngine.formatCurrencyWithDecimal(result.totalPayment, currencySymbol)
  val principalStr = LoanCalculatorEngine.formatCurrency(result.principal, currencySymbol)
  val principalExactStr = LoanCalculatorEngine.formatCurrencyWithDecimal(result.principal, currencySymbol)

  val displayEmi = if (isBn) HistoryDateFormatter.toBengaliDigits(emiStr) else emiStr
  val displayEmiExact = if (isBn) HistoryDateFormatter.toBengaliDigits(emiExactStr) else emiExactStr
  val displayInterest = if (isBn) HistoryDateFormatter.toBengaliDigits(totalInterestStr) else totalInterestStr
  val displayInterestExact = if (isBn) HistoryDateFormatter.toBengaliDigits(totalInterestExactStr) else totalInterestExactStr
  val displayPayment = if (isBn) HistoryDateFormatter.toBengaliDigits(totalPaymentStr) else totalPaymentStr
  val displayPaymentExact = if (isBn) HistoryDateFormatter.toBengaliDigits(totalPaymentExactStr) else totalPaymentExactStr
  val displayPrincipal = if (isBn) HistoryDateFormatter.toBengaliDigits(principalStr) else principalStr
  val displayPrincipalExact = if (isBn) HistoryDateFormatter.toBengaliDigits(principalExactStr) else principalExactStr

  val pPct = String.format(java.util.Locale.US, "%.1f", result.principalPercentage)
  val pPctStr = if (isBn) HistoryDateFormatter.toBengaliDigits(pPct) else pPct
  val iPct = String.format(java.util.Locale.US, "%.1f", result.interestPercentage)
  val iPctStr = if (isBn) HistoryDateFormatter.toBengaliDigits(iPct) else iPct

  val interestOfPrincipal = if (result.principal > 0) (result.totalInterest / result.principal) * 100.0 else 0.0
  val interestOfPrincipalFormatted = String.format(java.util.Locale.US, "%.1f", interestOfPrincipal)
  val interestOfPrincipalStr = if (isBn) HistoryDateFormatter.toBengaliDigits(interestOfPrincipalFormatted) else interestOfPrincipalFormatted

  val years = result.tenureMonths / 12.0
  val yearsFormatted = String.format(java.util.Locale.US, "%.1f", years).removeSuffix(".0")
  val yearsStr = if (isBn) HistoryDateFormatter.toBengaliDigits(yearsFormatted) else yearsFormatted
  val tenureCountStr = if (isBn) HistoryDateFormatter.toBengaliDigits(result.tenureMonths.toString()) else result.tenureMonths.toString()

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1. Primary Highlight Hero: Monthly EMI
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = BrandAccentBlue.copy(alpha = 0.08f),
        border = BorderStroke(1.5.dp, BrandAccentBlue.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              shape = CircleShape,
              color = BrandAccentBlue.copy(alpha = 0.15f),
              modifier = Modifier.size(24.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = Strings.monthlyEmiTitle(language),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandAccentBlue,
                fontSize = 13.sp
              )
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = displayEmi,
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = BrandDarkBlue,
                fontSize = 32.sp
              ),
              modifier = Modifier.testTag("loan_monthly_emi_result")
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = Strings.perMonthSuffix(language),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandMuted
              )
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          val tenureSubtitle = if (isBn) {
            "মোট $tenureCountStr টি কিস্তি ($yearsStr বছর) • সঠিক: $displayEmiExact"
          } else {
            "Total $tenureCountStr EMIs ($yearsStr yrs) • Exact: $displayEmiExact"
          }
          Text(
            text = tenureSubtitle,
            style = MaterialTheme.typography.labelSmall.copy(
              color = BrandMuted,
              fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. High-Contrast Side-by-Side: Principal Amount vs Total Interest Payable
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Tile 1: Principal Amount (আসল ঋণ)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
          border = BorderStroke(1.dp, BrandLine),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(BrandAccentBlue, CircleShape)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = Strings.principalCardTitle(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = BrandDarkBlue,
                  fontSize = 11.sp
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = displayPrincipal,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
              ),
              modifier = Modifier.testTag("loan_principal_result")
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
              text = "$pPctStr% ${if (isBn) "মোটের" else "of total"}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = BrandAccentBlue
              )
            )
          }
        }

        // Tile 2: Total Interest Payable (মোট প্রদেয় সুদ)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = WarningAmberContainer.copy(alpha = 0.35f),
          border = BorderStroke(1.2.dp, WarningAmber.copy(alpha = 0.45f)),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(WarningAmber, CircleShape)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = Strings.totalInterestCardTitle(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309), // Deep amber for clear high contrast
                  fontSize = 11.sp
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = displayInterest,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF92400E), // Deep contrasting amber/brown
                fontSize = 16.sp
              ),
              modifier = Modifier.testTag("loan_total_interest_result")
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
              text = "$iPctStr% ${if (isBn) "মোটের" else "of total"}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFB45309)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Full-Width Prominent Card: Total Payment (Principal + Interest)
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = ProfitGreenContainer.copy(alpha = 0.45f),
        border = BorderStroke(1.2.dp, ProfitGreen.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = CircleShape,
                color = ProfitGreen.copy(alpha = 0.15f),
                modifier = Modifier.size(22.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = ProfitGreen,
                    modifier = Modifier.size(13.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = Strings.totalPaymentCardTitle(language),
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = ProfitGreen,
                  fontSize = 12.5.sp
                )
              )
            }

            // Formula chip
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ProfitGreen.copy(alpha = 0.12f)
            ) {
              Text(
                text = if (isBn) "আসল + মোট সুদ" else "Principal + Interest",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = ProfitGreen
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = displayPayment,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Black,
              color = Color(0xFF065F46), // Deep green high contrast
              fontSize = 24.sp
            ),
            modifier = Modifier.testTag("loan_total_payment_result")
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Transparent arithmetic breakdown equation
          val equationStr = if (isBn) {
            "$displayPrincipal (আসল) + $displayInterest (সুদ) = $displayPayment"
          } else {
            "$displayPrincipal (Principal) + $displayInterest (Interest) = $displayPayment"
          }
          Text(
            text = equationStr,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              color = BrandMuted,
              fontWeight = FontWeight.Medium
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Principal vs Interest Visual Proportion Bar
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = Strings.breakdownChartTitle(language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = BrandMuted,
              fontSize = 11.5.sp
            )
          )
          Text(
            text = Strings.interestOfPrincipalLabel(interestOfPrincipalStr, language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFFB45309)
            )
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress distribution bar
        val princFraction = (result.principalPercentage / 100.0).toFloat().coerceIn(0.05f, 0.95f)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
        ) {
          Box(
            modifier = Modifier
              .weight(princFraction)
              .fillMaxSize()
              .background(BrandAccentBlue)
          )
          Box(
            modifier = Modifier
              .weight(1f - princFraction)
              .fillMaxSize()
              .background(WarningAmber)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .background(BrandAccentBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "${Strings.principalShareLabel(language)}: $displayPrincipal ($pPctStr%)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .background(WarningAmber, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "${Strings.interestShareLabel(language)}: $displayInterest ($iPctStr%)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF92400E)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Complete Itemized Breakdown Box
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, BrandLine.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = Strings.itemizedBreakdownTitle(language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = BrandDarkBlue,
              fontSize = 12.sp
            )
          )
          Spacer(modifier = Modifier.height(8.dp))

          BreakdownRowItem(
            label = if (isBn) "ঋণের মূল পরিমাণ (Principal)" else "Loan Principal Amount",
            value = displayPrincipal,
            exactValue = displayPrincipalExact,
            color = BrandDarkBlue
          )
          Spacer(modifier = Modifier.height(6.dp))

          BreakdownRowItem(
            label = if (isBn) "(+) মোট প্রদেয় সুদ (Total Interest)" else "(+) Total Interest Payable",
            value = displayInterest,
            exactValue = displayInterestExact,
            color = Color(0xFFB45309)
          )

          Divider(
            modifier = Modifier.padding(vertical = 6.dp),
            color = BrandLine
          )

          BreakdownRowItem(
            label = if (isBn) "(=) সর্বমোট প্রদেয় (Total Repayment)" else "(=) Total Amount Payable",
            value = displayPayment,
            exactValue = displayPaymentExact,
            color = ProfitGreen,
            isBold = true
          )
          Spacer(modifier = Modifier.height(6.dp))

          BreakdownRowItem(
            label = if (isBn) "মাসিক কিস্তি (Monthly EMI)" else "Monthly Installment (EMI)",
            value = displayEmi,
            exactValue = displayEmiExact,
            color = BrandAccentBlue
          )
          Spacer(modifier = Modifier.height(6.dp))

          BreakdownRowItem(
            label = if (isBn) "মোট কিস্তির সংখ্যা" else "Total Number of EMIs",
            value = if (isBn) "$tenureCountStr টি কিস্তি" else "$tenureCountStr Months",
            exactValue = null,
            color = BrandMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      Divider(color = BrandLine.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Action Buttons: Copy & Share
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onCopy,
          modifier = Modifier
            .weight(1f)
            .testTag("loan_copy_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = Strings.copyLoanReport(language), style = MaterialTheme.typography.labelMedium)
        }

        Button(
          onClick = onShare,
          modifier = Modifier
            .weight(1f)
            .testTag("loan_share_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = Strings.shareLoanReport(language), style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}

@Composable
private fun BreakdownRowItem(
  label: String,
  value: String,
  exactValue: String?,
  color: Color,
  isBold: Boolean = false
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        fontSize = 11.5.sp,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      ),
      modifier = Modifier.weight(1f)
    )
    Column(horizontalAlignment = Alignment.End) {
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 12.5.sp,
          fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
          color = color
        )
      )
      if (exactValue != null && exactValue != value) {
        Text(
          text = exactValue,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 9.5.sp,
            color = BrandMuted
          )
        )
      }
    }
  }
}

@Composable
private fun PrepaymentSimulatorCard(
  extraMonthlyInput: String,
  currencySymbol: String,
  prepaymentBenefit: com.example.data.calculator.PrepaymentBenefit?,
  language: AppLanguage,
  onValueChange: (String) -> Unit
) {
  val isBn = language == AppLanguage.BENGALI

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Savings,
          contentDescription = null,
          tint = ProfitGreen,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = Strings.extraMonthlyTitle(language),
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = Strings.extraMonthlyHelper(language),
        style = MaterialTheme.typography.bodySmall.copy(color = BrandMuted, fontSize = 11.sp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = extraMonthlyInput,
        onValueChange = onValueChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("loan_prepayment_input"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        leadingIcon = {
          Text(
            text = currencySymbol,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = ProfitGreen),
            modifier = Modifier.padding(start = 12.dp)
          )
        },
        placeholder = {
          Text(
            text = if (isBn) "উদাঃ ১,০০০" else "e.g. 1,000",
            style = MaterialTheme.typography.bodyMedium.copy(color = BrandMuted)
          )
        },
        shape = RoundedCornerShape(12.dp)
      )

      // Prepayment Benefit Result
      if (prepaymentBenefit != null && prepaymentBenefit.extraMonthlyPayment > 0.0) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = ProfitGreenContainer.copy(alpha = 0.5f),
          border = BorderStroke(1.dp, ProfitGreen.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Months saved
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = Strings.tenureSavedLabel(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = ProfitGreen,
                  fontWeight = FontWeight.Medium
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              val mStr = "${prepaymentBenefit.monthsSaved} ${Strings.monthsLabel(language)}"
              Text(
                text = if (isBn) HistoryDateFormatter.toBengaliDigits(mStr) else mStr,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = ProfitGreen
                )
              )
            }

            Divider(
              modifier = Modifier
                .height(30.dp)
                .width(1.dp),
              color = ProfitGreen.copy(alpha = 0.3f)
            )

            // Interest saved
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = Strings.interestSavedLabel(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = ProfitGreen,
                  fontWeight = FontWeight.Medium
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              val rawSaved = LoanCalculatorEngine.formatCurrency(prepaymentBenefit.interestSaved, currencySymbol)
              Text(
                text = if (isBn) HistoryDateFormatter.toBengaliDigits(rawSaved) else rawSaved,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = ProfitGreen
                )
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AmortizationScheduleCard(
  yearlyList: List<LoanYearSummary>,
  monthlyList: List<com.example.data.calculator.LoanMonthItem>,
  viewMode: ScheduleViewMode,
  currencySymbol: String,
  language: AppLanguage,
  onViewModeChange: (ScheduleViewMode) -> Unit
) {
  var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
  val isBn = language == AppLanguage.BENGALI

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with expand/collapse
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expanded = !expanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = Strings.scheduleTitle(language),
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.5.sp
            )
          )
        }
        Icon(
          imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = BrandMuted
        )
      }

      AnimatedVisibility(
        visible = expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          // Toggle Yearly vs Monthly
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (viewMode == ScheduleViewMode.YEARLY) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onViewModeChange(ScheduleViewMode.YEARLY) }
            ) {
              Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(
                  text = Strings.yearlyViewLabel(language),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (viewMode == ScheduleViewMode.YEARLY) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (viewMode == ScheduleViewMode.MONTHLY) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onViewModeChange(ScheduleViewMode.MONTHLY) }
            ) {
              Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(
                  text = Strings.monthlyViewLabel(language),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (viewMode == ScheduleViewMode.MONTHLY) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Horizontal scrollable table container
          Surface(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, BrandLine.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
            ) {
              // Table Header Row
              Row(
                modifier = Modifier
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                  .padding(horizontal = 10.dp, vertical = 8.dp)
              ) {
                val colPeriod = if (viewMode == ScheduleViewMode.YEARLY) Strings.yearHeaderLabel(language) else Strings.monthHeaderLabel(language)
                TableCell(text = colPeriod, width = 60.dp, isHeader = true)
                TableCell(text = Strings.principalHeaderLabel(language), width = 90.dp, isHeader = true)
                TableCell(text = Strings.interestHeaderLabel(language), width = 90.dp, isHeader = true)
                TableCell(text = Strings.balanceHeaderLabel(language), width = 95.dp, isHeader = true)
              }

              Divider(color = BrandLine.copy(alpha = 0.5f))

              if (viewMode == ScheduleViewMode.YEARLY) {
                yearlyList.forEachIndexed { idx, item ->
                  val bg = if (idx % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                  Row(
                    modifier = Modifier
                      .background(bg)
                      .padding(horizontal = 10.dp, vertical = 7.dp)
                  ) {
                    val yearStr = if (isBn) HistoryDateFormatter.toBengaliDigits("Y${item.year}") else "Y${item.year}"
                    val pStr = formatAmt(item.principalPaid, currencySymbol, isBn)
                    val iStr = formatAmt(item.interestPaid, currencySymbol, isBn)
                    val bStr = formatAmt(item.closingBalance, currencySymbol, isBn)

                    TableCell(text = yearStr, width = 60.dp, isBold = true)
                    TableCell(text = pStr, width = 90.dp, textColor = BrandAccentBlue)
                    TableCell(text = iStr, width = 90.dp, textColor = WarningAmber)
                    TableCell(text = bStr, width = 95.dp)
                  }
                }
              } else {
                // Monthly view (display up to first 60 months with a note if larger, or all months)
                monthlyList.take(60).forEachIndexed { idx, item ->
                  val bg = if (idx % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                  Row(
                    modifier = Modifier
                      .background(bg)
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    val mStr = if (isBn) HistoryDateFormatter.toBengaliDigits("M${item.month}") else "M${item.month}"
                    val pStr = formatAmt(item.principalPaid, currencySymbol, isBn)
                    val iStr = formatAmt(item.interestPaid, currencySymbol, isBn)
                    val bStr = formatAmt(item.closingBalance, currencySymbol, isBn)

                    TableCell(text = mStr, width = 60.dp, isBold = true)
                    TableCell(text = pStr, width = 90.dp, textColor = BrandAccentBlue)
                    TableCell(text = iStr, width = 90.dp, textColor = WarningAmber)
                    TableCell(text = bStr, width = 95.dp)
                  }
                }
                if (monthlyList.size > 60) {
                  Text(
                    text = if (isBn) "...আরও ${monthlyList.size - 60} মাসের কিস্তি রয়েছে" else "...and ${monthlyList.size - 60} more months",
                    style = MaterialTheme.typography.labelSmall.copy(color = BrandMuted),
                    modifier = Modifier.padding(10.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TableCell(
  text: String,
  width: androidx.compose.ui.unit.Dp,
  isHeader: Boolean = false,
  isBold: Boolean = false,
  textColor: Color? = null
) {
  Box(
    modifier = Modifier
      .width(width)
      .padding(horizontal = 2.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        fontSize = if (isHeader) 11.5.sp else 11.sp,
        color = textColor ?: if (isHeader) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      ),
      maxLines = 1
    )
  }
}

private fun formatAmt(amount: Double, currency: String, isBn: Boolean): String {
  val formatted = LoanCalculatorEngine.formatCurrency(amount, currency)
  return if (isBn) HistoryDateFormatter.toBengaliDigits(formatted) else formatted
}
