package com.example.valentinabotti_kotlin.model

sealed class Screen (val route: String) {//accetta solo queste classi come sottoclassi
    object Splash : Screen("splash")
    object HomeListaMenu : Screen("homeListaMenu/{sid}/{uid}")
    object ProfiloUtente : Screen("profiloUtente/{sid}/{uid}")
    object DettagliMenu : Screen("dettagliMenu/{sid}/{uid}/{mid}")
    object StatoConsegna : Screen("statoConsegna/{sid}/{oid}")
}