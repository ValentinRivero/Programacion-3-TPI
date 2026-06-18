package com.error404.mundialtpi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.error404.mundialtpi.models.DestinoDetalle
import com.error404.mundialtpi.models.DestinoLista
import com.error404.mundialtpi.models.DestinoLogin
import com.error404.mundialtpi.models.DestinoRegistro
import com.error404.mundialtpi.ui.screens.PantallaDetalle
import com.error404.mundialtpi.ui.screens.PantallaLista
import com.error404.mundialtpi.ui.screens.PantallaLogin
import com.error404.mundialtpi.ui.screens.PantallaRegistro
import com.error404.mundialtpi.viewmodel.AuthViewModel
import com.error404.mundialtpi.viewmodel.MundialViewModel

@Composable
fun AppNavigation(viewModel: MundialViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    // Start in Login if not logged in
    val startDest = if (authViewModel.isLoggedIn) DestinoLista else DestinoLogin

    NavHost(navController = navController, startDestination = startDest) {
        composable<DestinoLogin> {
            PantallaLogin(authViewModel, navController)
        }
        composable<DestinoRegistro> {
            PantallaRegistro(authViewModel, navController)
        }
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