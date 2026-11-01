package com.example.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.agecalc.AgeCalculatorScreen
import com.example.ui.agecalc.AgeCalculatorViewModel
import com.example.ui.components.HeaderBar
import com.example.ui.components.InfoDialog
import com.example.ui.components.SaveScenarioDialog
import com.example.ui.components.SavedScenariosListDialog
import com.example.ui.loancalc.LoanCalculatorScreen
import com.example.ui.loancalc.LoanCalculatorViewModel
import com.example.ui.normalcalc.NormalCalculatorScreen
import com.example.ui.normalcalc.NormalCalculatorViewModel
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

enum class AppTab(val testTag: String) {
  REWARD_COMPOUND("tab_reward_compound"),
  NORMAL_CALC("tab_normal_calc"),
  AGE_CALC("tab_age_calc"),
  LOAN_CALC("tab_loan_calc")
}

@Composable
fun MainAppScreen(
  calculatorViewModel: CalculatorViewModel,
  normalCalcViewModel: NormalCalculatorViewModel,
  ageCalcViewModel: AgeCalculatorViewModel,
  loanCalcViewModel: LoanCalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val calcState by calculatorViewModel.uiState.collectAsState()
  var currentTab by remember { mutableStateOf(AppTab.REWARD_COMPOUND) }
  var showInfoDialog by remember { mutableStateOf(false) }

  val language = calcState.language

  // If user is on a secondary tab and presses back, go back to Reward Compound tab
  BackHandler(enabled = currentTab != AppTab.REWARD_COMPOUND) {
    currentTab = AppTab.REWARD_COMPOUND
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      // Full screen for Normal Calculator: Hide large HeaderBar when on normal calc tab!
      if (currentTab != AppTab.NORMAL_CALC) {
        HeaderBar(
          language = language,
          onToggleLanguage = { calculatorViewModel.toggleLanguage() },
          onOpenSaved = { calculatorViewModel.openSavedListDialog() },
          onShowInfo = { showInfoDialog = true }
        )
      }
    },
    bottomBar = {
      // Sleek, compact bottom navigation bar with 4 tabs
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("bottom_nav_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(54.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Tab 1: Reward Compound
          CompactNavItem(
            selected = currentTab == AppTab.REWARD_COMPOUND,
            icon = Icons.Default.TrendingUp,
            label = Strings.tabRewardCompound(language),
            testTag = AppTab.REWARD_COMPOUND.testTag,
            onClick = { currentTab = AppTab.REWARD_COMPOUND }
          )

          // Tab 2: Normal Calculator
          CompactNavItem(
            selected = currentTab == AppTab.NORMAL_CALC,
            icon = Icons.Default.Calculate,
            label = Strings.tabNormalCalc(language),
            testTag = AppTab.NORMAL_CALC.testTag,
            onClick = { currentTab = AppTab.NORMAL_CALC }
          )

          // Tab 3: Age Calculator
          CompactNavItem(
            selected = currentTab == AppTab.AGE_CALC,
            icon = Icons.Default.CalendarMonth,
            label = Strings.tabAgeCalc(language),
            testTag = AppTab.AGE_CALC.testTag,
            onClick = { currentTab = AppTab.AGE_CALC }
          )

          // Tab 4: Loan Calculator
          CompactNavItem(
            selected = currentTab == AppTab.LOAN_CALC,
            icon = Icons.Default.AccountBalance,
            label = Strings.tabLoanCalc(language),
            testTag = AppTab.LOAN_CALC.testTag,
            onClick = { currentTab = AppTab.LOAN_CALC }
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.REWARD_COMPOUND -> {
          CalculatorScreen(viewModel = calculatorViewModel)
        }
        AppTab.NORMAL_CALC -> {
          NormalCalculatorScreen(
            viewModel = normalCalcViewModel,
            language = language,
            onToggleLanguage = { calculatorViewModel.toggleLanguage() }
          )
        }
        AppTab.AGE_CALC -> {
          AgeCalculatorScreen(
            viewModel = ageCalcViewModel,
            language = language
          )
        }
        AppTab.LOAN_CALC -> {
          LoanCalculatorScreen(
            viewModel = loanCalcViewModel,
            language = language
          )
        }
      }
    }

    // Shared dialogs for Reward Compound
    if (calcState.showSaveDialog) {
      SaveScenarioDialog(
        language = language,
        onDismiss = { calculatorViewModel.closeSaveDialog() },
        onSave = { title -> calculatorViewModel.saveScenario(title) }
      )
    }

    if (calcState.showSavedListDialog) {
      SavedScenariosListDialog(
        savedList = calcState.savedList,
        language = language,
        onDismiss = { calculatorViewModel.closeSavedListDialog() },
        onLoad = { saved ->
          calculatorViewModel.loadScenario(saved)
          currentTab = AppTab.REWARD_COMPOUND
        },
        onDelete = { id -> calculatorViewModel.deleteScenario(id) }
      )
    }

    if (showInfoDialog) {
      InfoDialog(
        language = language,
        onDismiss = { showInfoDialog = false }
      )
    }
  }
}

@Composable
private fun CompactNavItem(
  selected: Boolean,
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (selected) BrandAccentBlue.copy(alpha = 0.14f) else Color.Transparent,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) BrandAccentBlue else BrandMuted,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
          color = if (selected) BrandAccentBlue else BrandMuted
        )
      )
    }
  }
}
