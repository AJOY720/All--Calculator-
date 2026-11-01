package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandAccentBlue
import com.example.ui.theme.BrandDarkBlue
import com.example.ui.util.Strings

@Composable
fun HeaderBar(
  language: AppLanguage,
  onToggleLanguage: () -> Unit,
  onOpenSaved: () -> Unit,
  onShowInfo: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(
        brush = Brush.linearGradient(
          colors = listOf(BrandDarkBlue, BrandAccentBlue)
        ),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
      )
      .statusBarsPadding()
      .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App icon badge & Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
              text = Strings.appTitle(language),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
              )
            )
            Text(
              text = Strings.appSubtitle(language),
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            )
          }
        }

        // Top actions: Language pill toggle & Saved button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Language toggle button
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.25f),
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .clickable(onClick = onToggleLanguage)
              .testTag("language_toggle_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (language == AppLanguage.BENGALI) "বাং • EN" else "EN • বাং",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
            }
          }

          // Saved scenarios button
          IconButton(
            onClick = onOpenSaved,
            modifier = Modifier.testTag("saved_scenarios_button")
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = Strings.savedScenariosButton(language),
              tint = Color.White
            )
          }

          // Info dialog
          IconButton(
            onClick = onShowInfo,
            modifier = Modifier.testTag("info_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Info",
              tint = Color.White.copy(alpha = 0.9f)
            )
          }
        }
      }
    }
  }
}
