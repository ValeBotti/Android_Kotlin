package com.example.valentinabotti_kotlin.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.valentinabotti_kotlin.model.Menu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.valentinabotti_kotlin.data.local.DBController
import com.example.valentinabotti_kotlin.data.local.Image
import com.example.valentinabotti_kotlin.data.local.Word
import com.example.valentinabotti_kotlin.model.ImageUI
import com.example.valentinabotti_kotlin.viewmodel.HomeListaMenuViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun MenuCard(menu: Menu, sid: String, navController: NavController, viewModel: HomeListaMenuViewModel) {

    var image by remember { mutableStateOf(ImageUI("")) }

    val context = LocalContext.current

    LaunchedEffect(menu) {
        try {
            val db = DBController.getDatabase(context)
            val base64 = db.imageDao().getBase64ByMid(menu.mid)
            val wordFromDB = db.wordDAO().getAllWord()

            if (wordFromDB.isEmpty()) {
                db.wordDAO().insertWord(Word(
                    name= menu.name
                ))
                Log.d("DAO", "word inserted in DAO")
            } else {
                Log.d("DAO", "word in DAO ${wordFromDB}")
            }

            Log.d("MenuCard", "Base64 from DB: $base64")
            Log.d("MenuCard", "Menu MID: ${menu.mid}")

            if (!base64.isNullOrEmpty()) {

                image = ImageUI(base64)
                Log.d("MenuCard", "Image retrieved from DB: ${image.base64}")
            } else {

                try {
                    image = viewModel.retrieveImageMenu(menu.mid, sid)
                    Log.d("MenuCard", "Image retrieved from server: ${image.base64}")


                    if (image.base64.isNotEmpty()) {
                        db.imageDao().insertImage(
                            Image(menu.mid, image.base64, menu.imageVersion)
                        )

                        Log.d("MenuCard", "Image inserted into DB: ${image.base64}")
                    } else {
                        Log.d("MenuCard", "Image not found on server")
                    }
                } catch (e: Exception) {
                    Log.e("MenuCard", "Error retrieving image from server: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("MenuCard", "Error retrieving image from DB: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate("dettagliMenu/{sid}/{uid}/${menu.mid}")
            }
            .background(Color.Black)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(150.dp)
        ) {
            if (image.base64.isNotEmpty()) {
                Base64Image(image.base64, menu.name, menu.shortDescription, menu.price, menu.deliveryTime)
            }
        }
    }
}