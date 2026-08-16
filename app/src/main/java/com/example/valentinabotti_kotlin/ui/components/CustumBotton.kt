package com.example.valentinabotti_kotlin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valentinabotti_kotlin.ui.theme.DeepPurple

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    backgroundColor: Color = DeepPurple,
    cornerRadius: Int = 30,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp), // Spazio tra icona e testo
            modifier = Modifier
        ) {
            icon?.let {
                it()
            }
            Text(
                text = text,
                color = textColor,
                style = TextStyle(fontSize = 16.sp)
            )
        }
    }
}
