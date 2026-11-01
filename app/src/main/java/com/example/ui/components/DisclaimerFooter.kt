package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.BrandMuted
import com.example.ui.util.Strings

@Composable
fun DisclaimerFooter(
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = Strings.footerNote(language),
      style = MaterialTheme.typography.bodySmall.copy(
        fontSize = 12.sp,
        color = BrandMuted,
        textAlign = TextAlign.Center
      )
    )
  }
}
