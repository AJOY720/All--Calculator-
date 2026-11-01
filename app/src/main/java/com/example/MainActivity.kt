package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainAppScreen
import com.example.ui.CalculatorViewModel
import com.example.ui.agecalc.AgeCalculatorViewModel
import com.example.ui.loancalc.LoanCalculatorViewModel
import com.example.ui.normalcalc.NormalCalculatorViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val calculatorViewModel: CalculatorViewModel by viewModels()
  private val normalCalcViewModel: NormalCalculatorViewModel by viewModels()
  private val ageCalcViewModel: AgeCalculatorViewModel by viewModels()
  private val loanCalcViewModel: LoanCalculatorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppScreen(
          calculatorViewModel = calculatorViewModel,
          normalCalcViewModel = normalCalcViewModel,
          ageCalcViewModel = ageCalcViewModel,
          loanCalcViewModel = loanCalcViewModel
        )
      }
    }
  }
}
