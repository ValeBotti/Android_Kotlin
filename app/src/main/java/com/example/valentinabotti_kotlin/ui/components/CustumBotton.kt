package com.example.valentinabotti_kotlin.ui.components

import android.content.res.Resources.Theme
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
import androidx.compose.material3.*
import com.example.valentinabotti_kotlin.ui.theme.DeepPurple

@Composable
fun CustomButton(
    text: String,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = DeepPurple,
    cornerRadius: Int = 30,
    paddingHorizontal: Int = 16,
    paddingVertical: Int = 8
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius.dp),
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Spazio tra icona e testo
            modifier = Modifier
                .padding(horizontal = paddingHorizontal.dp, vertical = paddingVertical.dp)
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
