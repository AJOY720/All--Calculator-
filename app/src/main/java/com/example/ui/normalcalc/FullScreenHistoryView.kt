package com.example.ui.normalcalc

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandLine
import com.example.ui.theme.BrandMuted
import com.example.ui.util.HistoryDateFormatter
import com.example.ui.util.Strings

/**
 * Full-Screen Scrollable Calculation History View.
 * Displays up to 6 months of calculation history with smooth vertical scrolling,
 * date/period filters, instant search, copy, delete, and direct "Load & Edit in Calculator" action.
 */
@Composable
fun FullScreenHistoryView(
  viewModel: NormalCalculatorViewModel,
  language: AppLanguage,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()
  var showClearConfirmDialog by remember { mutableStateOf(false) }
  var showSearchBar by remember { mutableStateOf(state.historySearchQuery.isNotEmpty()) }
  var selectedItemForDetail by remember { mutableStateOf<NormalCalcHistoryItem?>(null) }
  val isBn = language == AppLanguage.BENGALI

  // Pressing back button closes the full-screen history and returns to calculator
  BackHandler(onBack = onClose)

  val filteredList = state.filteredHistory

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("full_screen_history_view")
  ) {
    // 1. Top App Bar with Navigation, Title, and Actions
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            IconButton(
              onClick = onClose,
              modifier = Modifier.testTag("history_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = Strings.backToCalculator(language),
                tint = BrandAccentBlue
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = Strings.fullScreenHistoryTitle(language),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )

                if (state.history.isNotEmpty()) {
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandAccentBlue.copy(alpha = 0.14f)
                  ) {
                    Text(
                      text = "${state.history.size}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandAccentBlue,
                        fontSize = 11.sp
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Text(
                text = Strings.fullScreenHistorySubtitle(language),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = BrandMuted,
                  fontSize = 11.sp
                )
              )
            }
          }

          // Top Action Buttons
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Search Toggle
            IconButton(
              onClick = {
                showSearchBar = !showSearchBar
                if (!showSearchBar) viewModel.setHistorySearchQuery("")
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (showSearchBar || state.historySearchQuery.isNotEmpty()) BrandAccentBlue else BrandMuted,
                modifier = Modifier.size(20.dp)
              )
            }

            // Quick Samples Menu: +50 Numbers or +6-Month Data
            IconButton(
              onClick = { viewModel.loadSample50NumbersCalculation(addToCalculatorToo = false) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Sample 50 Numbers",
                tint = BrandAccentBlue,
                modifier = Modifier.size(20.dp)
              )
            }

            // Clear All History
            if (state.history.isNotEmpty()) {
              IconButton(
                onClick = { showClearConfirmDialog = true },
                modifier = Modifier.size(36.dp).testTag("clear_all_history_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = Strings.clearHistory(language),
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }

        // Search Bar (Expandable)
        AnimatedVisibility(visible = showSearchBar) {
          Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
            OutlinedTextField(
              value = state.historySearchQuery,
              onValueChange = { viewModel.setHistorySearchQuery(it) },
              placeholder = {
                Text(
                  text = Strings.searchHistoryPlaceholder(language),
                  fontSize = 13.sp,
                  color = BrandMuted
                )
              },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = BrandMuted, modifier = Modifier.size(18.dp))
              },
              trailingIcon = {
                if (state.historySearchQuery.isNotEmpty()) {
                  IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BrandMuted, modifier = Modifier.size(16.dp))
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandAccentBlue,
                unfocusedBorderColor = BrandLine.copy(alpha = 0.6f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("history_search_field")
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Period Filter Chips Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FullScreenFilterChip(
            text = Strings.filterAll(language),
            isSelected = state.selectedFilter == HistoryFilterPeriod.ALL,
            count = state.history.size,
            onClick = { viewModel.setFilter(HistoryFilterPeriod.ALL) }
          )
          FullScreenFilterChip(
            text = Strings.filterToday(language),
            isSelected = state.selectedFilter == HistoryFilterPeriod.TODAY,
            onClick = { viewModel.setFilter(HistoryFilterPeriod.TODAY) }
          )
          FullScreenFilterChip(
            text = Strings.filterThisWeek(language),
            isSelected = state.selectedFilter == HistoryFilterPeriod.WEEK,
            onClick = { viewModel.setFilter(HistoryFilterPeriod.WEEK) }
          )
          FullScreenFilterChip(
            text = Strings.filterThisMonth(language),
            isSelected = state.selectedFilter == HistoryFilterPeriod.MONTH,
            onClick = { viewModel.setFilter(HistoryFilterPeriod.MONTH) }
          )
          FullScreenFilterChip(
            text = Strings.filterOlder(language),
            isSelected = state.selectedFilter == HistoryFilterPeriod.OLDER,
            onClick = { viewModel.setFilter(HistoryFilterPeriod.OLDER) }
          )
        }
      }
    }

    // 2. 6-Month Retention Banner & Automatic Memory notice
    Surface(
      shape = RoundedCornerShape(0.dp),
      color = BrandAccentBlue.copy(alpha = 0.08f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = Strings.historySixMonthsBadge(language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = BrandAccentBlue,
              fontSize = 11.sp
            )
          )
        }
        TextButton(
          onClick = { viewModel.loadSampleSixMonthHistory() },
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
        ) {
          Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandAccentBlue)
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = Strings.loadSixMonthSamples(language),
            style = MaterialTheme.typography.labelSmall.copy(
              color = BrandAccentBlue,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
        }
      }
    }

    HorizontalDivider(color = BrandLine.copy(alpha = 0.4f), thickness = 0.8.dp)

    // 3. Scrollable History List (LazyColumn for deep, infinite-like scrolling)
    if (filteredList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = BrandAccentBlue.copy(alpha = 0.1f),
            modifier = Modifier.size(72.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = BrandAccentBlue,
                modifier = Modifier.size(36.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = if (state.historySearchQuery.isNotBlank()) {
              if (isBn) "কোনো ফলাফল পাওয়া যায়নি" else "No matching calculations found"
            } else {
              Strings.noHistory(language)
            },
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = Strings.historyRetentionSubtext(language),
            style = MaterialTheme.typography.bodySmall.copy(
              color = BrandMuted,
              fontSize = 12.sp
            )
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { viewModel.loadSample50NumbersCalculation(addToCalculatorToo = false) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isBn) "৫০ সংখ্যার যোগফলের নমুনা যোগ করুন" else "Load 50-Number Sum Sample",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp)
          .testTag("history_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
      ) {
        items(filteredList, key = { it.id }) { item ->
          FullScreenHistoryCard(
            item = item,
            language = language,
            onLoadAndEditInCalculator = {
              viewModel.loadHistoryExpression(item)
              onClose()
            },
            onUseResult = {
              viewModel.loadHistoryResult(item)
              onClose()
            },
            onCopy = {
              viewModel.copyHistoryItem(item, language)
            },
            onDelete = {
              viewModel.deleteHistoryItem(item)
            },
            onViewBreakdown = {
              selectedItemForDetail = item
            }
          )
        }
      }
    }
  }

  // Clear All Confirmation Dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = {
        Text(
          text = Strings.clearAllHistoryConfirmTitle(language),
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = Strings.clearAllHistoryConfirmMessage(language),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.onClearHistory()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(text = Strings.confirmClear(language), color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text(text = if (isBn) "বাতিল" else "Cancel")
        }
      }
    )
  }

  // 50-number detailed breakdown modal if requested
  if (selectedItemForDetail != null) {
    FullHistoryDetailDialog(
      item = selectedItemForDetail!!,
      language = language,
      onDismiss = { selectedItemForDetail = null },
      onLoadExpression = {
        viewModel.loadHistoryExpression(selectedItemForDetail!!)
        selectedItemForDetail = null
        onClose()
      },
      onLoadResult = {
        viewModel.loadHistoryResult(selectedItemForDetail!!)
        selectedItemForDetail = null
        onClose()
      },
      onCopy = {
        viewModel.copyHistoryItem(selectedItemForDetail!!, language)
      }
    )
  }
}

/**
 * Individual Card in the Full Screen History List.
 * Includes complete equation with syntax formatting, date/time badge, result, and direct actions.
 */
@Composable
private fun FullScreenHistoryCard(
  item: NormalCalcHistoryItem,
  language: AppLanguage,
  onLoadAndEditInCalculator: () -> Unit,
  onUseResult: () -> Unit,
  onCopy: () -> Unit,
  onDelete: () -> Unit,
  onViewBreakdown: () -> Unit
) {
  val isBn = language == AppLanguage.BENGALI
  val count = remember(item.expression) { countNumbersInExpression(item.expression) }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, BrandLine.copy(alpha = 0.6f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Date & Time Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = BrandAccentBlue,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = HistoryDateFormatter.formatDateTime(item.timestamp, language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
              fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BrandAccentBlue.copy(alpha = 0.12f)
        ) {
          Text(
            text = HistoryDateFormatter.getRelativeTimeSpan(item.timestamp, language),
            style = MaterialTheme.typography.labelSmall.copy(
              color = BrandAccentBlue,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            ),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Chained Expression Display with scrollable box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
          .padding(10.dp)
      ) {
        Column {
          // If multi-number sum, show count tag
          if (count >= 3) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BrandAccentBlue.copy(alpha = 0.15f),
              modifier = Modifier.padding(bottom = 6.dp)
            ) {
              Text(
                text = Strings.numbersCountBadge(count, language),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = BrandAccentBlue,
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = item.expression,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = if (item.expression.length > 50) 15.sp else 18.sp,
              lineHeight = if (item.expression.length > 50) 22.sp else 26.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))
          HorizontalDivider(color = BrandLine.copy(alpha = 0.3f), thickness = 0.8.dp)
          Spacer(modifier = Modifier.height(6.dp))

          // Result Sum
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "= ",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandMuted,
                fontSize = 18.sp
              )
            )
            Text(
              text = item.result,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = BrandAccentBlue,
                fontSize = 22.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Action Buttons Row: "Load & Edit", "Use Result", "Copy", "Delete"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Primary Action: Load directly into Calculator Editor so user can edit 150 or any digit!
        Button(
          onClick = onLoadAndEditInCalculator,
          colors = ButtonDefaults.buttonColors(containerColor = BrandAccentBlue),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = Strings.loadInEditorButton(language),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Use Result button
          TextButton(
            onClick = onUseResult,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp)
          ) {
            Text(
              text = Strings.useResultButton(language),
              style = MaterialTheme.typography.labelSmall.copy(
                color = BrandAccentBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              )
            )
          }

          // Copy button
          IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandMuted, modifier = Modifier.size(16.dp))
          }

          // Delete button
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun FullScreenFilterChip(
  text: String,
  isSelected: Boolean,
  count: Int? = null,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) BrandAccentBlue else MaterialTheme.colorScheme.surfaceVariant,
    border = BorderStroke(1.dp, if (isSelected) BrandAccentBlue else BrandLine.copy(alpha = 0.5f)),
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
          fontSize = 11.5.sp
        )
      )
      if (count != null && count > 0) {
        Spacer(modifier = Modifier.width(4.dp))
        Surface(
          shape = RoundedCornerShape(5.dp),
          color = if (isSelected) Color.White.copy(alpha = 0.25f) else BrandAccentBlue.copy(alpha = 0.12f)
        ) {
          Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isSelected) Color.White else BrandAccentBlue,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}
