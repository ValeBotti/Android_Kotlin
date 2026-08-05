package com.example.valentinabotti_kotlin

import DettagliMenu
import HomeListaMenu
import ProfiloUtente
import Splash
import StatoConsegna
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.model.UidSid

@Composable
fun Navigation(
    navController: NavHostController,
    sid: String,
    uid: Int
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(route = Screen.Splash.route) {
            Splash(navController = navController)
        }

        composable(route = Screen.HomeListaMenu.route) {
            HomeListaMenu(navController = navController, sid= sid, uid= uid)
        }

        composable(route = Screen.ProfiloUtente.route) {
            ProfiloUtente(navController = navController, sid= sid, uid= uid)
        }

        composable(route = Screen.DettagliMenu.route) { backStackEntry ->
            val mid = backStackEntry.arguments?.getString("mid")?.toIntOrNull() ?: 0
            DettagliMenu(sid = sid, uid = uid, mid = mid, navController = navController)
        }

        composable(route = Screen.StatoConsegna.route) { backStackEntry ->
            val oid = backStackEntry.arguments?.getString("oid")?.toIntOrNull() ?: 0
            StatoConsegna(sid = sid, oid = oid, navController = navController)
        }
    }
}