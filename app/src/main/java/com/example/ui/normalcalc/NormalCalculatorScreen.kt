package com.example.ui.normalcalc

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.StandardCalculatorEngine
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

@Composable
fun NormalCalculatorScreen(
  viewModel: NormalCalculatorViewModel,
  language: AppLanguage,
  onToggleLanguage: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()
  val isBn = language == AppLanguage.BENGALI

  // If user opened full-screen history, render FullScreenHistoryView directly!
  if (state.isFullScreenHistoryOpen) {
    FullScreenHistoryView(
      viewModel = viewModel,
      language = language,
      onClose = { viewModel.closeFullScreenHistory() },
      modifier = modifier
    )
    return
  }

  val expressionScrollState = rememberScrollState()

  val numberCount = remember(state.expression) {
    countNumbersInExpression(state.expression)
  }

  // Auto-scroll expression box as user types or moves cursor
  LaunchedEffect(state.expression.length, state.cursorPosition) {
    if (expressionScrollState.maxValue > 0) {
      expressionScrollState.animateScrollTo(expressionScrollState.maxValue)
    }
  }

  // Maintain local TextFieldValue synchronized with ViewModel expression and cursor position
  var textFieldValue by remember(state.expression, state.cursorPosition) {
    mutableStateOf(
      TextFieldValue(
        text = state.expression,
        selection = TextRange(state.cursorPosition.coerceIn(0, state.expression.length))
      )
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    // 1. Sleek Compact Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = Strings.normalCalcTitle(language),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        // Multi-number count badge - Tapping it opens itemized list
        if (numberCount >= 2) {
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandAccentBlue.copy(alpha = 0.15f),
            border = BorderStroke(0.8.dp, BrandAccentBlue.copy(alpha = 0.35f)),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { viewModel.openItemizedList() }
              .testTag("top_number_count_badge")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = null,
                tint = BrandAccentBlue,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isBn) "${StandardCalculatorEngine.toBengaliDigits("$numberCount")} টি সংখ্যা" else "$numberCount items",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = BrandAccentBlue,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Language Toggle Pill
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onToggleLanguage)
            .testTag("calc_language_toggle")
        ) {
          Text(
            text = if (isBn) "বাং" else "EN",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = BrandAccentBlue,
              fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Scientific Mode Toggle Button
        IconButton(
          onClick = { viewModel.toggleScientific() },
          modifier = Modifier
            .size(34.dp)
            .testTag("scientific_toggle_button")
        ) {
          Icon(
            imageVector = Icons.Default.Functions,
            contentDescription = Strings.scientificMode(language),
            tint = if (state.isScientific) BrandAccentBlue else BrandMuted,
            modifier = Modifier.size(19.dp)
          )
        }

        // Full-Screen Calculation History Button with count badge
        Box(contentAlignment = Alignment.TopEnd) {
          IconButton(
            onClick = { viewModel.openFullScreenHistory() },
            modifier = Modifier
              .size(34.dp)
              .testTag("history_toggle_button")
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = Strings.fullScreenHistoryTitle(language),
              tint = BrandAccentBlue,
              modifier = Modifier.size(20.dp)
            )
          }
          if (state.history.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BrandAccentBlue,
              modifier = Modifier.padding(top = 2.dp, end = 2.dp)
            ) {
              Text(
                text = "${state.history.size}",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }
      }
    }

    // 2. Main Expression Card with Interactive Cursor & Direct Editing
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(bottom = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        // Multi-line scrollable interactive expression box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(expressionScrollState),
          contentAlignment = Alignment.BottomEnd
        ) {
          val exprDisplay = state.expression.ifBlank { "0" }
          val fontSize = when {
            exprDisplay.length <= 14 -> 28.sp
            exprDisplay.length <= 35 -> 22.sp
            exprDisplay.length <= 80 -> 18.sp
            else -> 15.sp
          }
          val lineHeight = when {
            exprDisplay.length <= 14 -> 34.sp
            exprDisplay.length <= 35 -> 28.sp
            else -> 22.sp
          }

          // Interactive BasicTextField: User can tap ANYWHERE to place cursor, e.g. inside "150"!
          BasicTextField(
            value = textFieldValue,
            onValueChange = { newVal ->
              textFieldValue = newVal
              viewModel.onExpressionAndCursorChanged(newVal.text, newVal.selection.end)
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = fontSize,
              lineHeight = lineHeight,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.End
            ),
            cursorBrush = SolidColor(BrandAccentBlue),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("calc_expression_display"),
            decorationBox = { innerTextField ->
              Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
              ) {
                if (textFieldValue.text.isEmpty()) {
                  Text(
                    text = "0",
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      fontSize = fontSize,
                      color = BrandMuted.copy(alpha = 0.5f),
                      textAlign = TextAlign.End
                    ),
                    modifier = Modifier.fillMaxWidth()
                  )
                }
                innerTextField()
              }
            }
          )
        }

        // 2b. Chained Sequential Numbers Row (e.g. [#1: 150 ✎] + [#2: 250 ✎] + [#3: 365 ✎]...)
        if (state.numberTokens.size >= 2) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isBn) "সংখ্যা এডিট:" else "Edit Number:",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = BrandMuted,
                fontSize = 10.5.sp
              )
            )

            state.numberTokens.forEach { token ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BrandAccentBlue.copy(alpha = 0.12f),
                border = BorderStroke(0.8.dp, BrandAccentBlue.copy(alpha = 0.35f)),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    viewModel.openTokenEditor(token)
                  }
                  .testTag("token_chip_${token.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "#${if (isBn) StandardCalculatorEngine.toBengaliDigits("${token.id}") else "${token.id}"}: ${if (isBn) StandardCalculatorEngine.toBengaliDigits(token.value) else token.value}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = BrandAccentBlue,
                      fontSize = 11.5.sp
                    )
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${token.value}",
                    tint = BrandAccentBlue,
                    modifier = Modifier.size(11.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2c. DEDICATED, UNMISSABLE "মোট যোগফল" (TOTAL SUM) SECTION
    // This solves the problem where the sum was missing or hidden!
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (state.isCalculated) BrandAccentBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
      ),
      border = BorderStroke(
        width = 1.dp,
        color = if (state.isCalculated) BrandAccentBlue else BrandLine.copy(alpha = 0.6f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp)
        .testTag("calc_total_sum_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Status badge & label
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (state.isCalculated) BrandAccentBlue else MaterialTheme.colorScheme.surface,
              border = BorderStroke(0.6.dp, BrandLine.copy(alpha = 0.4f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (state.isCalculated) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                  text = when {
                    state.isCalculated -> Strings.finalResultLabel(language)
                    state.resultPreview.isNotBlank() -> Strings.totalSumLabel(language)
                    else -> Strings.runningSumLabel(language)
                  },
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (state.isCalculated) Color.White else BrandAccentBlue,
                    fontSize = 11.sp
                  )
                )
              }
            }

            if (state.numberTokens.size >= 2) {
              Spacer(modifier = Modifier.width(6.dp))
              // Button to open itemized receipt dialog
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandAccentBlue.copy(alpha = 0.12f),
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .clickable { viewModel.openItemizedList() }
                  .testTag("open_receipt_button")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = BrandAccentBlue,
                    modifier = Modifier.size(11.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = Strings.itemizedListButton(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = BrandAccentBlue,
                      fontSize = 10.5.sp
                    )
                  )
                }
              }
            }
          }

          // Right: Action Buttons (Copy, Use Result, New Calc)
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.copyToClipboard(language) },
              modifier = Modifier.size(28.dp).testTag("copy_sum_button")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy Sum",
                tint = BrandMuted,
                modifier = Modifier.size(15.dp)
              )
            }

            if (state.isCalculated) {
              IconButton(
                onClick = { viewModel.useResultAsNewExpression() },
                modifier = Modifier.size(28.dp).testTag("use_result_button")
              ) {
                Icon(
                  imageVector = Icons.Default.RestartAlt,
                  contentDescription = Strings.useResultButton(language),
                  tint = BrandAccentBlue,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Result Value Display: Prominent, Big, Bold, Glowing!
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.Bottom
        ) {
          if (state.errorMessage != null) {
            Text(
              text = state.errorMessage ?: "",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            )
          } else {
            val displaySum = when {
              isBn && state.runningSumBengali.isNotBlank() -> state.runningSumBengali
              state.resultPreview.isNotBlank() -> state.resultPreview
              else -> if (isBn) "০" else "0"
            }

            Text(
              text = "= ",
              style = MaterialTheme.typography.titleLarge.copy(
                color = BrandAccentBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
              )
            )
            Text(
              text = displaySum,
              style = MaterialTheme.typography.headlineMedium.copy(
                color = if (state.isCalculated) BrandAccentBlue else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
              ),
              modifier = Modifier.testTag("calc_preview_result")
            )

            // If in Bengali mode, also show English digits in small font alongside
            if (isBn && state.resultPreview.isNotBlank() && state.resultPreview != state.runningSumBengali) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "(${state.resultPreview})",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = BrandMuted,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 2.dp)
              )
            }
          }
        }

        // Amount in words (কথায়: ...)
        if (state.amountInWords.isNotBlank() && state.errorMessage == null) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${Strings.amountInWordsLabel(language)} ${state.amountInWords}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = BrandMuted,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.End
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 3. Ergonomic Cursor Navigation Toolbar
    // Allows effortless cursor stepping between digits (e.g. moving between 1, 5, and 0 in 150)
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
      border = BorderStroke(0.8.dp, BrandLine.copy(alpha = 0.5f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Jump to Start |◀
        IconButton(
          onClick = { viewModel.moveCursorToStart() },
          modifier = Modifier.size(34.dp).testTag("cursor_start_button")
        ) {
          Icon(Icons.Default.FirstPage, contentDescription = "Start", tint = BrandAccentBlue, modifier = Modifier.size(18.dp))
        }

        // Step Left ◀
        IconButton(
          onClick = { viewModel.moveCursorLeft() },
          modifier = Modifier.size(34.dp).testTag("cursor_left_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = BrandAccentBlue, modifier = Modifier.size(18.dp))
        }

        // Indicator showing position in equation
        Text(
          text = if (isBn) "কার্সর: ${StandardCalculatorEngine.toBengaliDigits("${state.cursorPosition}")}/${StandardCalculatorEngine.toBengaliDigits("${state.expression.length}")}"
                 else "Cursor: ${state.cursorPosition}/${state.expression.length}",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = BrandMuted,
            fontSize = 11.sp
          )
        )

        // Step Right ▶
        IconButton(
          onClick = { viewModel.moveCursorRight() },
          modifier = Modifier.size(34.dp).testTag("cursor_right_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right", tint = BrandAccentBlue, modifier = Modifier.size(18.dp))
        }

        // Jump to End ▶|
        IconButton(
          onClick = { viewModel.moveCursorToEnd() },
          modifier = Modifier.size(34.dp).testTag("cursor_end_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.LastPage, contentDescription = "End", tint = BrandAccentBlue, modifier = Modifier.size(18.dp))
        }

        // Delete Forward (DEL)
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { viewModel.onDeleteForward() }
        ) {
          Text(
            text = "DEL",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onErrorContainer,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
          )
        }
      }
    }

    // 4. Optional Scientific Row
    AnimatedVisibility(visible = state.isScientific) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        CalcPillButton(text = "(", onClick = { viewModel.onParenthesis() })
        CalcPillButton(text = ")", onClick = { viewModel.onParenthesis() })
        CalcPillButton(text = "√", onClick = { viewModel.onSquareRoot() })
        CalcPillButton(text = "x²", onClick = { viewModel.onSquare() })
        CalcPillButton(text = "^", onClick = { viewModel.onOperator("^") })
        CalcPillButton(text = "π", onClick = { viewModel.onDigit("π") })
        CalcPillButton(text = "e", onClick = { viewModel.onDigit("e") })
      }
    }

    // 5. Ergonomic Keypad Grid (5 Rows)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
      // Row 1: AC, (), %, ÷
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CalcKeyButton(
          text = "AC",
          modifier = Modifier.weight(1f),
          color = MaterialTheme.colorScheme.errorContainer,
          textColor = MaterialTheme.colorScheme.onErrorContainer,
          testTag = "calc_key_ac",
          onClick = { viewModel.onClear() }
        )
        CalcKeyButton(
          text = "( )",
          modifier = Modifier.weight(1f),
          color = MaterialTheme.colorScheme.surfaceVariant,
          onClick = { viewModel.onParenthesis() }
        )
        CalcKeyButton(
          text = "%",
          modifier = Modifier.weight(1f),
          color = MaterialTheme.colorScheme.surfaceVariant,
          onClick = { viewModel.onPercentage() }
        )
        CalcKeyButton(
          text = "÷",
          modifier = Modifier.weight(1f),
          color = BrandAccentBlue.copy(alpha = 0.15f),
          textColor = BrandAccentBlue,
          onClick = { viewModel.onOperator("÷") }
        )
      }

      // Row 2: 7, 8, 9, ×
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CalcKeyButton(text = "7", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("7") })
        CalcKeyButton(text = "8", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("8") })
        CalcKeyButton(text = "9", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("9") })
        CalcKeyButton(
          text = "×",
          modifier = Modifier.weight(1f),
          color = BrandAccentBlue.copy(alpha = 0.15f),
          textColor = BrandAccentBlue,
          onClick = { viewModel.onOperator("×") }
        )
      }

      // Row 3: 4, 5, 6, −
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CalcKeyButton(text = "4", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("4") })
        CalcKeyButton(text = "5", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("5") })
        CalcKeyButton(text = "6", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("6") })
        CalcKeyButton(
          text = "−",
          modifier = Modifier.weight(1f),
          color = BrandAccentBlue.copy(alpha = 0.15f),
          textColor = BrandAccentBlue,
          onClick = { viewModel.onOperator("−") }
        )
      }

      // Row 4: 1, 2, 3, +
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CalcKeyButton(text = "1", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("1") })
        CalcKeyButton(text = "2", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("2") })
        CalcKeyButton(text = "3", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("3") })
        CalcKeyButton(
          text = "+",
          modifier = Modifier.weight(1f),
          color = BrandAccentBlue.copy(alpha = 0.15f),
          textColor = BrandAccentBlue,
          onClick = { viewModel.onOperator("+") }
        )
      }

      // Row 5: 0, ., ⌫, =
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CalcKeyButton(text = "0", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit("0") })
        CalcKeyButton(text = ".", modifier = Modifier.weight(1f), onClick = { viewModel.onDigit(".") })
        CalcIconKeyButton(
          icon = Icons.AutoMirrored.Filled.Backspace,
          modifier = Modifier.weight(1f),
          testTag = "calc_key_backspace",
          onClick = { viewModel.onBackspace() }
        )
        CalcKeyButton(
          text = "=",
          modifier = Modifier.weight(1f),
          color = BrandAccentBlue,
          textColor = Color.White,
          testTag = "calc_key_equals",
          onClick = { viewModel.onEquals() }
        )
      }
    }

    // Number Token Editor Dialog when tapping any number chip in the chained equation
    if (state.editingToken != null) {
      NumberTokenEditorDialog(
        token = state.editingToken!!,
        language = language,
        onDismiss = { viewModel.dismissTokenEditor() },
        onUpdateValue = { newVal ->
          viewModel.updateTokenValue(state.editingToken!!, newVal)
        },
        onDeleteToken = {
          viewModel.removeItemToken(state.editingToken!!)
        }
      )
    }

    // New Itemized Tally & Receipt Dialog (Shows running sum for each item, edit/delete, stats)
    if (state.isItemizedListOpen) {
      ItemizedTallyDialog(
        state = state,
        language = language,
        onDismiss = { viewModel.closeItemizedList() },
        onEditToken = { token ->
          viewModel.closeItemizedList()
          viewModel.openTokenEditor(token)
        },
        onDeleteToken = { token ->
          viewModel.removeItemToken(token)
        },
        onCopyReceipt = {
          viewModel.copyReceiptText(language)
        }
      )
    }
  }
}

@Composable
private fun CalcKeyButton(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.surface,
  textColor: Color = MaterialTheme.colorScheme.onSurface,
  testTag: String = "calc_key_$text",
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(52.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = color,
    shadowElevation = 1.dp
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          color = textColor
        )
      )
    }
  }
}

@Composable
private fun CalcIconKeyButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.surfaceVariant,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(52.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = color,
    shadowElevation = 1.dp
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
private fun CalcPillButton(
  text: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = BrandAccentBlue
      ),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}
