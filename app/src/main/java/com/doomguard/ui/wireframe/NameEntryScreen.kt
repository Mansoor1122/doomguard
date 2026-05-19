package com.doomguard.ui.wireframe

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doomguard.ui.DoomViewModel
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.WireframeColors
import com.doomguard.ui.theme.doomCyberPrimaryFill

@Composable
fun NameEntryScreen(
    vm: DoomViewModel,
    onContinue: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    val trimmed = name.trim()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DoomBrushes.hullVertical)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "What should we call you?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = WireframeColors.TextPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "We’ll greet you on Home. This stays on your device only.",
            color = WireframeColors.TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 40) name = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Your name") },
            placeholder = { Text("e.g. Alex") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (trimmed.isNotEmpty()) vm.setUserDisplayName(trimmed, onContinue)
                },
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WireframeColors.CyanGlow,
                unfocusedBorderColor = WireframeColors.Neutral.copy(alpha = 0.45f),
                cursorColor = WireframeColors.CyanBright,
                focusedLabelColor = WireframeColors.CyanBright,
                unfocusedLabelColor = WireframeColors.TextSecondary,
                focusedTextColor = WireframeColors.TextPrimary,
                unfocusedTextColor = WireframeColors.TextPrimary,
            ),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { vm.setUserDisplayName(trimmed, onContinue) },
            enabled = trimmed.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = WireframeColors.TextPrimary),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .doomCyberPrimaryFill(enabled = trimmed.isNotEmpty()),
                contentAlignment = Alignment.Center,
            ) {
                Text("Continue", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}
