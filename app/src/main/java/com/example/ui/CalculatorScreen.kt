package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.DailyBreakdownTable
import com.example.ui.components.DisclaimerFooter
import com.example.ui.components.InputCard
import com.example.ui.components.ProjectionChart
import com.example.ui.components.ResultMetricsCard

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
      // Main Center Content (max-width 760dp for beautiful responsive layout)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        contentAlignment = Alignment.TopCenter
      ) {
        Column(
          modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxWidth()
        ) {
          Spacer(modifier = Modifier.height(14.dp))

          // 1. Calculation Input Card
          InputCard(
            state = state,
            onPrincipalChange = { viewModel.onPrincipalChange(it) },
            onRateChange = { viewModel.onRateChange(it) },
            onDaysChange = { viewModel.onDaysChange(it) },
            onCurrencyChange = { viewModel.onCurrencyChange(it) },
            onCompoundToggled = { viewModel.onCompoundToggled(it) },
            onDepositSeparateToggled = { viewModel.onDepositSeparateToggled(it) },
            onDailyAdditionChange = { viewModel.onDailyAdditionChange(it) },
            onToggleAdvanced = { viewModel.toggleAdvanced() },
            onAddPrincipal = { viewModel.addPrincipal(it) },
            onApplyPresetRate = { viewModel.applyPresetRate(it) },
            onApplyPresetDays = { viewModel.applyPresetDays(it) },
            onCalculate = { viewModel.calculate() },
            onDownloadCsv = { viewModel.shareCsvReport() },
            onOpenSaveDialog = { viewModel.openSaveDialog() }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 2. Calculation Results Card
          ResultMetricsCard(
            result = state.result,
            language = state.language,
            onCopySummary = { viewModel.copySummaryToClipboard() },
            onShareSummary = { viewModel.shareSummaryText() }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 3. Growth Projection Chart Card
          ProjectionChart(
            result = state.result,
            language = state.language,
            selectedDay = state.selectedScrubDay,
            onSelectDay = { viewModel.onScrubDay(it) }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 4. Day-by-Day Table Card
          DailyBreakdownTable(
            result = state.result,
            language = state.language,
            searchQuery = state.tableSearchQuery,
            onSearchChange = { viewModel.onTableSearch(it) },
            onSelectScrubDay = { viewModel.onScrubDay(it) }
          )

          // 5. Disclaimer Footer
          DisclaimerFooter(language = state.language)
        }
      }
    }
  }
}
