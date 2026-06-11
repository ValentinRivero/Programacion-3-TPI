package com.error404.mundialtpi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.error404.mundialtpi.models.DestinoDetalle
import com.error404.mundialtpi.models.DestinoLista
import com.error404.mundialtpi.ui.screens.PantallaDetalle
import com.error404.mundialtpi.ui.screens.PantallaLista
import com.error404.mundialtpi.viewmodel.MundialViewModel

@Composable
fun AppNavigation(viewModel: MundialViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = DestinoLista) {
        composable<DestinoLista> {
            PantallaLista(viewModel, navController)
        }
        composable<DestinoDetalle> { backStackEntry ->
            val ruta = backStackEntry.toRoute<DestinoDetalle>()
            PantallaDetalle(
                partidoId = ruta.partidoId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}