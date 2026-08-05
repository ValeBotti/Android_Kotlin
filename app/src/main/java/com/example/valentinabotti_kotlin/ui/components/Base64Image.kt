package com.example.valentinabotti_kotlin.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun Base64Image(base64String: String) {
    // Decodifica la stringa base64 in un ByteArray
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

    // Crea un Bitmap dai byte decodificati
    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

    // Usa ImageBitmap per visualizzare l'immagine
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop // Puoi cambiare il ContentScale a seconda del comportamento che desideri
    )
}